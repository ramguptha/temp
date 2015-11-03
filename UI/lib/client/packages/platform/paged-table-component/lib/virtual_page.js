define([
  'ember',
  'packages/platform/paged-component'
], function(
  Em,
  PagedComponent
) {
  'use strict';

  // VirtualPage
  // ===========
  //
  // Drives the rendering of a virtual page of table content.

  return PagedComponent.VirtualPage.extend({
    columnPresentation: null,
    selection: null,

    frozenDataColumns: Em.computed.oneWay('columnPresentation.frozenDataColumns'),
    freeDataColumns: Em.computed.oneWay('columnPresentation.freeDataColumns'),

    // Styles
    // ------

    freeStyle: function() {
      var style = 'display:none';

      var loadedRange = this.get('loadedRange');
      if (loadedRange && 0 !== loadedRange.count) {
        var topStyle = 'top:' + this.get('virtualPageMetrics.offsetTop') + 'px;';

        var pagedComponent = this.get('pagedComponent');
        var columns = this.get('freeDataColumns');
        var widthStyle = pagedComponent.getTableWidthStyle(
          columns, this.get('virtualContainerMetrics.controlColumnWidth'), this.get('columnPresentation.hasControlColumn')
        );

        style = topStyle + widthStyle;
      }

      return new Em.Handlebars.SafeString(style);
    }.property(
      'loadedRange',

      'virtualPageMetrics.offsetTop',

      'pagedComponent',
      'freeDataColumns.[]',
      'virtualContainerMetrics.controlColumnWidth',
      'columnPresentation.hasControlColumn'
    ),

    frozenWidthStyle: function() {
      var pagedComponent = this.get('pagedComponent');
      var columns = this.get('frozenDataColumns');

      // Force the frozen column to overlap the corresponding column underneath
      return new Em.Handlebars.SafeString(
        (
          pagedComponent.getTableWidth(columns, this.get('virtualContainerMetrics.controlColumnWidth'), this.get('columnPresentation.hasControlColumn')) + 2
        ) + 'px'
      );
    }.property(
      'pagedComponent',
      'frozenDataColumns.[]',
      'virtualContainerMetrics.controlColumnWidth',
      'columnPresentation.hasControlColumn'
    ),

    frozenTopStyle: function() {
      return new Em.Handlebars.SafeString(this.get('virtualPageMetrics.offsetTop') + 'px');
    }.property('virtualPageMetrics.offsetTop'),

    frozenDisplayStyle: function() {
      var displayStyle = '';

      var loadedRange = this.get('loadedRange');
      if (!loadedRange || 0 === loadedRange.count) {
        displayStyle = 'none';
      }

      return new Em.Handlebars.SafeString(displayStyle);
    }.property('loadedRange'),

    // Row Data
    // --------

    createRowPresentation: function(rowIndex) {
      var columnPresentation = this.get('columnPresentation');
      var selection = this.get('selection');

      return this._super(rowIndex).setProperties({
        columnPresentation: columnPresentation,
        selection: selection
      });
    },

    // Overlay Data
    // ------------

    // Re-use the same overlay objects over the lifetime of the virtual page. Overlays will be assigned slices of
    // this array.
    overlayPool: function() {
      return [];
    }.property(),

    // Size _rowPool_ appropriately and return a slice of it for further processing.
    getOverlays: function(count) {
      var columnPresentation = this.get('columnPresentation');
      var overlays = this.get('overlayPool');

      for (var i = overlays.get('length'); i < count; i++) {
        overlays.push(columnPresentation.overlayPresentation.create({
          virtualPage: this,
          virtualContainerMetrics: this.get('virtualContainerMetrics'),
          virtualPageMetrics: this.get('virtualPageMetrics')
        }));
      }

      return overlays.slice(0, count);
    },

    overlays: function() {
      return [];
    }.property(),

    // Data Loading
    // ------------

    didRefresh: function(resultSet) {
      this.updateOverlays(resultSet);
    },

    updateOverlays: function(resultSet) {
      var overlays = this.get('overlays');

      if (resultSet) {
        var rows = this.get('rows');
        var overlayRows = [];
        resultSet.forEach(function(node, i) {
          if (node.isDeferred || node.isGroup) {
            var row = rows.objectAt(i);
            overlayRows.push(row);
          }
        });

        // Show Deferred node in overlay
        var initialOverlayLength = this.get('overlays.length');

        var overlayCount = overlayRows.length;

        if (initialOverlayLength < overlayCount) {         
          // Grow!
          var overlayPool = this.getOverlays(overlayCount);
          overlays.replace(
            initialOverlayLength, 0, overlayPool.slice(initialOverlayLength, overlayCount)
          );
        } else {
          // Shrink.
          overlays.replace(overlayCount, initialOverlayLength - overlayCount, []);
        }

        for (var i = 0; i < overlayCount; i++) {
          var overlayRow = overlayRows[i];

          overlays.objectAt(i).setProperties({
            rowIndex: overlayRow.get('rowIndex'),
            node: overlayRow.get('node')
          });
        }
      } else {
        overlays.clear();
      }
    },

    clear: function() {
      this.get('rows').clear();
      this.get('overlays').clear();
    }
  });
});
