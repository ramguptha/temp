define([
  'ember',
  'packages/platform/tracer',
  './virtual_page_metrics'
], function(
  Em,
  Tracer,
  VirtualPageMetrics
) {
  'use strict';

  // VirtualPage
  // ===========
  //
  // Drives the rendering of a virtual page of table content.

  return Em.Object.extend(Tracer.IsTraced, {
    VirtualPageMetrics: VirtualPageMetrics,
    rowPresentation: null,

    tracer: null,

    pagedComponent: null,
    pager: Em.computed.oneWay('pagedComponent.pager'),
    domIdMapper: Em.computed.oneWay('pagedComponent.domIdMapper'),

    deferredNodes: null,

    // Row Data
    // --------

    // Re-use the same row objects over the lifetime of the virtual page. Rows will be assigned slices of this array.
    rowPool: function() {
      return [];
    }.property(),

    // A slice of _rowPool_, for rendering.
    rows: function() {
      return [];
    }.property(),

    // Size _rowPool_ appropriately and return a slice of it for further processing.
    getRows: function(count) {
      var rows = this.get('rowPool');

      for (var i = rows.get('length'); i < count; i++) {
        rows.push(this.createRowPresentation(i));
      }

      return rows.slice(0, count);
    },

    sizeRows: function(length) {
      var rows = this.get('rows');

      // Update rows so that number of elements matches resultSet
      var initialRowLength = rows.get('length');

      if (initialRowLength < length) {         
        // Grow!
        var rowPool = this.getRows(length);
        rows.replace(
          initialRowLength, 0, rowPool.slice(initialRowLength, length)
        );
      } else {
        // Shrink.
        rows.replace(length, initialRowLength - length, []);
      }

      return rows;
    },

    createRowPresentation: function(rowIndex) {
      return this.rowPresentation.create({
        virtualPage: this,
        rowIndex: rowIndex,
        domIdMapper: this.get('domIdMapper'),
        tracer: Tracer.Child.create({
          parent: this.get('tracer'),
          name: 'row' + rowIndex,
          colour: this.get('tracer.colour')
        })
      });
    },

    // Metrics
    // -------

    virtualContainerMetrics: null,

    virtualPageMetrics: function() {
      return this.VirtualPageMetrics.create({
        virtualContainerMetrics: this.get('virtualContainerMetrics'),
        virtualPage: this
      });
    }.property(),

    // Ranges
    // ------
    //
    // A Range describes the content that the virtual page is expected to display. It is in the following format:
    //
    //     { offset: number, count: number }

    requestedRange: null,

    loadedRange: function() {
      var loadedRange = null;

      var requestedRange = this.get('requestedRange');
      if (requestedRange) {
        loadedRange = {
          offset: requestedRange.offset,
          count: this.get('rows.length')
        };
      }

      return loadedRange;
    }.property('requestedRange', 'rows.length'),

    containsRange: function(range) {
      var containsRange = false;

      var loadedRange = this.get('loadedRange');
      if (loadedRange) {
        var loadedRangeEnd = loadedRange.offset + loadedRange.count;
        var rangeEnd = range.offset + range.count;

        containsRange = (loadedRange.offset <= range.offset) && (loadedRangeEnd >= rangeEnd);
      }

      return containsRange;
    },

    // Data Loading
    // ------------

    updateRange: function(range) {
      var requestedRange = this.get('requestedRange');

      var requestedRangeIsNull = (null === requestedRange);
      var rangeIsNull = (null === range);

      if (
        (requestedRangeIsNull !== rangeIsNull) ||
        (!rangeIsNull && !requestedRangeIsNull && (requestedRange.offset !== range.offset)) ||
        (!rangeIsNull && !requestedRangeIsNull && (requestedRange.count !== range.count))
      ) {
        this.trace('update range', range);

        this.set('requestedRange', range);
        this.scheduleRefresh();
      }
    },

    scheduleRefresh: function() {
      this.trace('scheduleRefresh');
      Em.run.once(this, this.refresh);
    },

    refresh: function() {
      this.trace('refresh:run');

      if ('inDOM' !== this.get('pagedComponent._state')) {
        this.trace('refresh:pagedComponent is no longer in DOM');
        return;
      }

      var resultSet = null;
      var range = this.get('requestedRange');
      var pager = this.get('pager');

      var deferredNodes = [];

      if (range && pager) {
        resultSet = pager.read(range.offset, range.count);

        var rows = this.sizeRows(resultSet.get('length'));
        resultSet.forEach(function(node, i) {
          if (node.isDeferred) {
            deferredNodes.push(node);
          }

          var row = rows.objectAt(i);
          row.set('node', node);
        });
      } else {
        this.clear();
      }

      this.set('deferredNodes', deferredNodes);

      this.didRefresh(resultSet);

      var pagedComponent = this.get('pagedComponent');
      var externalError = pagedComponent.get('externalError');

      if (!externalError && deferredNodes.some(function(node) {
        return !Em.get(node.nodeData, 'isFailed');
      })) {
        Em.run.once(pagedComponent, pagedComponent.loadDeferredNodes);
      }
    },

    didRefresh: Em.K,

    clear: function() {
      this.get('rows').clear();
    }
  });
});
