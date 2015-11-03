define([
  'ember',

  'packages/platform/locale',
  'packages/platform/tracer',
  'packages/platform/selection',
  'packages/platform/sink',
  'packages/platform/pager',
  'packages/platform/bound-alias-shim',

  './lib/dom_id_mapper',
  './lib/row_presentation',
  './lib/virtual_container_metrics',
  './lib/virtual_page_metrics',
  './lib/virtual_page',

  './lib/components/item_component',
  './lib/components/value_component',
  './lib/components/empty_result_component',
  './lib/components/loading_component'
], function(
  Em,

  Locale,
  Tracer,
  Selection,
  Sink,
  Pager,
  boundAliasShim,

  DomIdMapper,
  RowPresentation,
  VirtualContainerMetrics,
  VirtualPageMetrics,
  VirtualPage,

  ItemComponent,
  ValueComponent,
  EmptyResultComponent,
  LoadingComponent
) {
  'use strict';

  // Paged Component
  // ===============
  //
  // Abstract base class for all components that render data owned by a Data Pager.
  //
  // Bindable properties:
  // 
  // - pager: a QueriedPager instance, used for all data loading.
  // - paused: pause all loading when true.
  // - selectedIds: set of selected node ids.
  // - selector: a Selection.Selector instance, to implement selection behaviour.
  // - selectOnRowClick: true or false. If true, send "updateOneSelection" from the rowClick handler.
  // - disabledIds: ids of rows that ignore a row click.
  // - externalError: injectable error, for display and handling similarly to a load error.
  // - rowPresentation: row presentation class
  //
  // Bindable resource keys:
  //
  // - rLoadingMore: Loading in progress.
  // - rNoResults: Empty result.
  // - rTryAgain: Retry a failed load.
  // - rShowErrorDetail: Link to show details of why the last load failed.
  // - rUnableLoadData: Last load failed.
  //
  // Bindable actions:
  //
  // - rowClick(rowPresentation): sent when a row was clicked on.
  // - showErrorDetail(error): show the user details of the last error, or externalError if set.
  // - retryLoad(externalError): retry the load which caused externalError to be set.

  var REQUIRED_OVERRIDE = function() { throw 'Implement me'; };
  var REQUIRED_PROPERTY = REQUIRED_OVERRIDE.property();

  return Em.Component.extend(Tracer.IsTraced, {
    Tracer: Tracer,
    DomIdMapper: DomIdMapper,
    VirtualContainerMetrics: VirtualContainerMetrics,
    VirtualPage: VirtualPage,

    pageSize: 100,

    rowPresentation: RowPresentation,

    isEmptyResultSet: function() {
      return 0 === this.get('pager.readLength') && this.get('pager.isFullyLoaded');
    }.property('pager.readLength', 'pager.isFullyLoaded'),

    // This property is used to drive triggers based on scroll interactions, such as hiding context menus
    scrollEventCount: 0,

    // Used in the handlebars to indicate the type of cursor on the element with an action bound to it
    hasRowClick: true,

    actions: {

      retryLoad: function(deferred) {
        var externalError = this.get('externalError');

        if (externalError) {
          this.sendAction('retryLoad', externalError);
        } else {
          this.invokeDeferred(this.get('pager'), deferred);
        }
      },

      showError: function(error) {
        this.sendAction('showErrorDetail', { error: error });
      },

      rowClick: function(rowPresentation) {
        if (rowPresentation.get('isDisabled') || !rowPresentation.get('hasRowClick')) {
          return;
        }

        this.sendAction('rowClick', rowPresentation);

        if (this.get('selectOnRowClick')) {
          this.send('updateOneSelection', rowPresentation.get('node.id'));
        }
      },

      updateAllSelections: function() {
        this.updateAllSelections();
      },

      updateOneSelection: function(nodeId) {
        this.updateOneSelection(nodeId);
      }
    },

    pager: Pager.create(),

    // Metrics and the DOM
    // -------------------

    virtualContainerMetrics: function() {
      return this.VirtualContainerMetrics.create({ component: this });
    }.property(),

    virtualContainerClientHeightDidChange: function() {
      this.updateVirtualPages();
    }.observes('virtualContainerMetrics.containerClientHeight'),

    updateMetrics: REQUIRED_OVERRIDE,

    scheduleUpdateMetrics: function() {
      Em.run.scheduleOnce('afterRender', this, this.updateMetrics);
    },

    continuouslyUpdateMetrics: function() {
      var state = this.get('_state');
      if ('destroyed' !== state && 'destroying' !== state) {
        this.updateMetrics();

        Em.run.later(this, this.continuouslyUpdateMetrics, 1000);
      }
    },

    appLayoutMetricsDidChange: function() {
      this.updateMetrics();
    }.observes('App.layoutMetricsChangedAt'),

    bindVirtualContainerScroll: REQUIRED_OVERRIDE,
    unbindVirtualContainerScroll: REQUIRED_OVERRIDE,

    virtualContainerScroll: function(e) {
      var self = e.data;
      var $elt = e.target;

      self.setProperties({
        'virtualContainerMetrics.scrollLeft': $elt.scrollLeft,
        'virtualContainerMetrics.scrollTop': $elt.scrollTop
      });

      self.updateVirtualPages();

      self.incrementProperty('scrollEventCount');
    },

    domIdMapper: function() {
      return this.DomIdMapper.create();
    }.property(),

    // Lifecycle Callbacks
    // -------------------

    init: function() {
      this._super();

      this.continuouslyUpdateMetrics();

      this.set('virtualContainerMetrics.virtualRows', this.get('pager.readLength'));

      // Observers
      this.getProperties('paused pager.contentChangedAt pager.structureGrewAt pager.structureShrunkAt pager.structureResetAt pager.structureChangeAt selectedIds isSelectAllEnabled isAllSelected virtualContainerMetrics.containerClientHeight App.layoutMetricsChangedAt'.w());
    },

    didInsertElement: function() {
      this.updateMetrics();
      this.updateVirtualPages();
      this.bindVirtualContainerScroll();
    },

    willDestroyElement: function() {
      this.unbindVirtualContainerScroll();
    },

    // Load Status
    // -----------

    isFullyLoaded: Em.computed.oneWay('pager.isFullyLoaded'),
    readTail: Em.computed.oneWay('pager.readTail'),
    deferredTail: Em.computed.oneWay('pager.deferredTail'),

    // Pause / Resume
    // --------------

    paused: false,

    pausedDidChange: function() {
      if (this.get('paused')) {
        this.set('virtualContainerMetrics.virtualRows', 0);
      } else {
        var readLength = this.get('pager.readLength');
        this.set('virtualContainerMetrics.virtualRows', readLength);
      }

      this.updateVirtualPages();
      this.refreshVirtualPages();
    }.observes('paused'),

    // Data Pager Observers
    // --------------------
    //
    // These observers watch the corresponding properties of _pager_, invoking convenience handlers then they
    // trigger. Sub-classes are expected to override handlers of interest.

    // The page structure remained the same, but the content related to the structure (related nodeData or other) has
    // changed.
    pageContentChangedAtObserver: function() {
      this.pageContentDidChange();
    }.observes('pager.contentChangedAt'),

    // The page structure has grown.
    pageStructureGrewAtObserver: function() {
      this.pageStructureDidGrow();
    }.observes('pager.structureGrewAt'),

    // The page structure has shrunk.
    pageStructureShrunkAtObserver: function() {
      this.pageStructureDidShrink();
    }.observes('pager.structureShrunkAt'),

    // The page structure has been reset to its default (unloaded) state.
    pageStructureResetAtObserver: function() {
      this.pageStructureDidReset();
    }.observes('pager.structureResetAt'),

    pageStructureChangedAtObserver: function() {
      this.pageStructureDidChange();
    }.observes('pager.structureChangedAt'),

    // Paged Data Callbacks
    // --------------------

    pageContentDidChange: function() {
      this.trace('pageContentDidChange');

      this.refreshVirtualPages();
    },

    pageStructureDidGrow: function() {
      this.trace('pageStructureDidGrow');
    },

    pageStructureDidShrink: function() {
      this.trace('pageStructureDidShrink');
    },

    pageStructureDidReset: function() {
      this.trace('pageStructureDidReset');
    },

    pageStructureDidChange: function() {
      this.trace('pageStructureDidChange');

      var readLength = this.get('pager.readLength');
      this.set('virtualContainerMetrics.virtualRows', readLength);

      this.updateVirtualPages();
      this.refreshVirtualPages();

      this.scheduleUpdateMetrics();
    },

    // Virtual Positioning / Content
    // -----------------------------

    virtualPage0: function() {
      return this.createVirtualPage('1', 'rgba(255,128,128,0.5)');
    }.property(),

    virtualPage1: function() {
      return this.createVirtualPage('2', 'rgba(128,255,128,0.5)');
    }.property(),

    virtualPage2: function() {
      return this.createVirtualPage('3', 'rgba(128,128,255,0.5)');
    }.property(),

    createVirtualPage: function(traceName, traceColour) {
      return this.VirtualPage.create({
        rowPresentation: this.rowPresentation,

        tracer: this.Tracer.Child.create({
          parent: this.get('tracer'),
          name: traceName,
          colour: traceColour
        }),

        pagedComponent: this,
        virtualContainerMetrics: this.get('virtualContainerMetrics')
      });
    },

    virtualPages: function() {
      return [this.get('virtualPage0'), this.get('virtualPage1'), this.get('virtualPage2')];
    }.property('virtualPage0', 'virtualPage1', 'virtualPage2'),

    refreshVirtualPages: function() {
      this.get('virtualPage0').scheduleRefresh();
      this.get('virtualPage1').scheduleRefresh();
      this.get('virtualPage2').scheduleRefresh();
    },

    updateVirtualPages: function() {

      // Three virtual pages, which map to the row address space in order:
      //
      //     0, 1, 2, 0, 1, 2, 0, 1, ...
      // 
      // Valid offsets: 
      //
      // - Page 0: N * 3 * virtualPageSize
      // - Page 1: ((N * 3) + 1) * virtualPageSize
      // - Page 2: ((N * 3) + 2) * virtualPageSize
      //
      // Given a visibleRange offset, map it to whole units of virtualPageSize. Then starting page is
      // page units modulus page count. Ensure that range is valid, then set the others around it.

      var virtualPages = this.get('virtualPages');
      var virtualPageSize = this.get('virtualContainerMetrics.virtualPageSize');

      var visibleRange = this.get('virtualContainerMetrics.visibleRange');

      var startVirtualPageOffset = (virtualPageSize !== 0) ? Math.floor(visibleRange.offset / virtualPageSize) : 0;
      var startVirtualPageIdx = startVirtualPageOffset % virtualPages.get('length');

      var virtualPage = virtualPages.objectAt(startVirtualPageIdx);
      virtualPage.updateRange({
        offset: startVirtualPageOffset * virtualPageSize,
        count: virtualPageSize
      });

      virtualPage = virtualPages.objectAt((startVirtualPageIdx + 1) % virtualPages.get('length'));
      virtualPage.updateRange({
        offset: (startVirtualPageOffset + 1) * virtualPageSize,
        count: virtualPageSize
      });

      virtualPage = virtualPages.objectAt((startVirtualPageIdx + 2) % virtualPages.get('length'));
      var pageOffset = startVirtualPageOffset - 1;
      virtualPage.updateRange({
        offset: ((pageOffset < 0) ? (startVirtualPageOffset + 2) : pageOffset) * virtualPageSize,
        count: virtualPageSize
      });
    },

    loadDeferredNodes: function() {
      this.loadIterate(this.get('pager'));
    },

    // Load worker.
    loadIterate: function(pager) {
      var self = this;

      self.trace('load iterate');

      if (this.get('paused')) {
        self.trace('load iterate: paused');
        return;
      }

      if (this.get('externalError')) {
        self.trace('load iterate: externalError');
        return;
      }

      var deferredNodes = Em.A();
      this.get('virtualPages').forEach(function(page) {
        deferredNodes.pushObjects(page.get('deferredNodes'));
      });

      deferredNodes.forEach(function(deferred) {
        var loadStatus = deferred.nodeData;
        if (loadStatus.get('isFailed')) {
          self.trace('load iterate: skip failed deferred', deferred.id, deferred);
          return;
        }

        if (loadStatus.get('isInvoked') || pager.get('loadQueue').contains(deferred)) {
          self.trace('load iterate: already loading', deferred.id, deferred);
          return;
        }

        self.trace('load iterate: invoke', deferred.id, deferred);
        self.invokeDeferred(pager, deferred);
      });
    },

    invokeDeferred: function(pager, deferred) {
      var self = this;

      pager.load(
        deferred,
        null,

        // Success - try to read
        function(context, data) {
          self.trace('load success');

          if ('inDOM' !== self.get('_state')) {
            self.trace('no longer in DOM');
            return;
          }

          self.set('virtualContainerMetrics.virtualRows', pager.get('readLength'));

          self.trace('load complete');
        }, 

        // Cancelled - start loading again
        function(context) {
          self.trace('load cancel');
        }, 

        // Error
        function(context, error) {
          self.trace('load error');
        }
      );
    },

    // Selection
    // ---------

    selector: function() {
      return Selection.Disabled.create();
    }.property(),

    selectedIds: Sink.create(),

    hasSelection: boundAliasShim('selector.enabled'),
    hasSelectAll: boundAliasShim('selector.canSelectAll'),

    hasSingleSelection: false,

    updateOneSelection: function(nodeId) {
      var selected = null;

      var selector = this.get('selector');
      if (selector) {
        selected = selector.updateOneSelection(this.get('selectedIds'), this.get('allSelectableIds'), nodeId);
      }

      return selected;
    },

    updateAllSelections: function() {
      var isAllSelected = null;

      var selector = this.get('selector');
      if (selector) {
        isAllSelected = selector.updateAllSelections(
          this.get('selectedIds'), this.get('allSelectableIds'), this.get('isAllSelected')
        );
      }

      return isAllSelected;
    },

    isSelectAllEnabled: Em.computed.and('isFullyLoaded', 'selector.canSelectAll', 'haveSelectableIds'),
    isSelectAllDisabled: Em.computed.not('isSelectAllEnabled'),

    isAllSelected: function() {
      var allSelectableIdsLength = this.get('allSelectableIds.length'),
        allSelectableIds = this.get('allSelectableIds'),
        selectedIds = this.get('selectedIds');

      selectedIds = selectedIds.filter(function(item) {
        return allSelectableIds.contains(item);
      });

      return (allSelectableIdsLength > 0) && (allSelectableIdsLength === selectedIds.length);
    }.property('allSelectableIds.length', 'selectedIds.length'),

    haveSelectableIds: function() {
      return this.get('allSelectableIds.length') > 0;
    }.property('allSelectableIds.length'),

    allSelectableIds: function() {
      return this.getAllSelectableIds(this.get('disabledIds'));
    }.property('pager.changedAt', 'disabledIds.[]'),

    getAllSelectableIds: function(disabledIds) {
      var selectableIds = [];

      var pager = this.get('pager');
      if (pager) {
        pager.walk(function(node) {
          if (node.isRecord && !disabledIds.contains(node.id)) {
            selectableIds.push(node.id);
          }
        });
      }

      return selectableIds;
    },

    // Enable / Disable Rows
    // ---------------------

    disabledIds: Sink.create(),

    // Tracing
    // -------

    tracer: function() {
      return this.Tracer.Root.create();
    }.property(),

    // Error handling
    // --------------

    // Consumers of this component can inject an error into it for rendering. This is useful if the table is paused
    // waiting for an external process to complete, and that process fails. When external error is set, it renders
    // in place of deferred nodes.
    externalError: null,

    // Strings
    // -------

    // Loading ...
    rLoadingMore: 'shared.loadingMore',
    tLoadingMore: Locale.translated({ property: 'rLoadingMore' }),

    // No Results
    rNoResults: 'shared.noResults',
    tNoResults: Locale.translated({ property: 'rNoResults' }),

    // No Items to Display
    rNoItems: 'shared.noItemsToDisplay',
    tNoItems: Locale.translated({ property: 'rNoItems' }),

    // Try Again
    rTryAgain: 'shared.buttons.tryAgain',
    tTryAgain: Locale.translated({ property: 'rTryAgain' }),

    // Show Error Detail
    rShowErrorDetail: 'shared.errors.showErrorDetail',
    tShowErrorDetail: Locale.translated({ property: 'rShowErrorDetail' }),

    // Unable to Load Data
    rUnableLoadData: 'shared.errors.unableLoadData',
    tUnableLoadData: Locale.translated({ property: 'rUnableLoadData' })

  }).reopenClass({
    DomIdMapper: DomIdMapper,
    RowPresentation: RowPresentation,

    ItemComponent: ItemComponent,
    ValueComponent: ValueComponent,
    EmptyResultComponent: EmptyResultComponent,
    LoadingComponent: LoadingComponent,

    VirtualContainerMetrics: VirtualContainerMetrics,
    VirtualPageMetrics: VirtualPageMetrics,
    VirtualPage: VirtualPage
  });
});
