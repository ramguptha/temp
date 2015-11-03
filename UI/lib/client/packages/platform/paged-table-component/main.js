define([
  'ember',
  'packages/platform/presenter',
  'packages/platform/paged-component',
  'packages/platform/apply-styles',

  './lib/presentation',
  './lib/selection',
  './lib/virtual_container_metrics',
  './lib/virtual_page',
  './lib/resizer',

  './lib/components/overlay_component',

  'text!./lib/templates/paged_table.handlebars',
  'text!./lib/templates/free_page.handlebars',
  'text!./lib/templates/frozen_page.handlebars',

  'text!./lib/templates/overlay_presentation.handlebars'
], function(
  Em,

  Presenter,
  PagedComponent,
  applyStyles,

  Presentation,
  Selection,
  VirtualContainerMetrics,
  VirtualPage,
  Resizer,

  OverlayComponent,

  pagedTableTemplate,
  freePageTemplate,
  frozenPageTemplate,

  overlayPresentationTemplate
) {
  'use strict';

  // Paged Table
  // ===========
  //
  // Renders a paged table with infinite scrolling.
  //
  // Bindable properties:
  //
  // - hideSelectionCheckboxes: (default false) don't show selection checkboxes.
  // - rowHeight: (default 30) height of all rows.
  // - defaultColumnWidth: (default 100)
  // - controlColumnWidth: (default 30)
  // - columns: columns to render
  // - valueComponent: default component to render for values. Overridden by columns.@each.valueComponent.
  // - overlayComponent: for rendering nodes in overlays.
  // - pausedComponent: shown when loading is paused
  // - loadingComponent: for rendering the loading status of nodes.
  // - emptyResultComponent: shown when nothing matches the search criteria
  // - isGroupedGrid: true when Grid is a grouped grid(e.g. software Overview tab), otherwise, set it false
  //
  // Bindable actions:
  //
  // - cellClick(rowPresentation, cellContext): sent when a cell is clicked on.
  // - valueClick(nodeId, rowId, columnId, value): send when a value is clicked on.

  // Field Config
  // ------------
  //
  // Encapsulates configuration for a data field (either a table column or an overlay field).

  var FieldConfig = Presenter.extend({
    id: Em.computed.alias('name'),

    columnData: null,
    isFrozen: false,
    isGrouped: false,
    isSortable: true,
    label: null,
    width: null,

    renderLabel: function() {
      return Em.Handlebars.Utils.escapeExpression(this.get('label'));
    },

    labelIconClass: null,
    labelIconWidthThreshold: 50,

    renderValue: function(nodeData) {
      var value = nodeData[this.get('id')];
      return Em.Handlebars.Utils.escapeExpression(value);
    },

    valueComponent: null
  });

  // PagedTableComponent
  // -------------------
  //
  // The main thing!

  var PagedTableFreePageComponent = Em.Component.extend({
    layout: Em.Handlebars.compile(freePageTemplate),
    presenter: null
  });

  var PagedTableFrozenPageComponent = Em.Component.extend({
    applyStyles: applyStyles,
    layout: Em.Handlebars.compile(frozenPageTemplate),
    presenter: null,

    init: function() {
      this._super();

      // Observers
      this.getProperties('presenter.frozenTopStyle presenter.frozenWidthStyle presenter.frozenDisplayStyle'.w());
    },

    didInsertElement: function() {
      this.updateTableStyle();
    },

    // bind-attr isn't appropriate for the table, because we want to render changes immediately in order for
    // for free behaviour to be smooth.
    updateTableStyle: function() {
      this.applyStyles(this, 'table.frozen', {
        top: this.get('presenter.frozenTopStyle'),
        width: this.get('presenter.frozenWidthStyle'),
        display: this.get('presenter.frozenDisplayStyle')
      });
    }.observes('presenter.frozenTopStyle', 'presenter.frozenWidthStyle', 'presenter.frozenDisplayStyle')     
  });

  var PagedTableOverlayPresentationComponent = PagedComponent.ItemComponent.extend({
    layout: Em.Handlebars.compile(overlayPresentationTemplate),
    classNameBindings: ':fill row.hasRowClick:pointer'.w()
  });

  var PagedTableComponent = PagedComponent.extend({
    isGroupedGrid: null,
    FieldConfig: FieldConfig,

    VirtualContainerMetrics: VirtualContainerMetrics,
    VirtualPage: VirtualPage,

    ColumnPresentation: Presentation.ColumnPresentation,

    Selection: Selection,
    Resizer: Resizer,

    applyStyles: applyStyles,

    rowPresentation: Presentation.RowPresentation,
    overlayPresentation: Presentation.OverlayPresentation,

    overlayComponent: 'paged-table-overlay',
    valueComponent: 'paged-table-value',
    pausedComponent: 'paged-table-loading',
    loadingComponent: 'paged-table-loading',
    emptyResultComponent: 'paged-table-empty-result',

    layout: Em.Handlebars.compile(pagedTableTemplate),
    classNames: 'is-paged-table fill reset-origin'.w(),

    actions: {

      // Get the existing sort array
      // If it's empty, use columnId and default dir
      // If length is more than 1 (multi sort), reset and use columnId
      // If length is 1 check the attr and direction
      sortColumn: function(column) {
        // TODO: Find out why after clicking this button, a second instance of tipsy is being created
        Em.$('.tipsy:last').remove();

        if (!column.config.get('isSortable')) {
          return;
        }

        var sort = this.get('pager.searchQuery.sort'),
            length = sort.length,
            columnId = column.config.name,
            attr = columnId, dir = 'asc',
            lastSort;

        if (length === 1) {
          lastSort = sort[0];
          if (lastSort.attr === columnId && lastSort.dir === 'asc') {
            dir = 'desc';
          }
        }

        if (this.get('isGroupedGrid')){
          lastSort = sort.pop();
          if (lastSort.attr === columnId && lastSort.dir === 'asc') {
            dir = 'desc';
          }
          sort.pushObject({ attr: attr, dir: dir });
        } else {
          this.set('pager.searchQuery.sort', Em.A([{ attr: attr, dir: dir }]));
        }
      },

      // Start resizing the column on the mouseDown on the header's resize element
      startResizing: function(columnPresentation) {
        // Get the next columnPresentation
        var columns = this.get('columns'),
          defaultColumnWidth = this.get('defaultColumnWidth'),
          padding = this.get('resizer.padding'),

          leftColumn = columnPresentation.config,
          leftColumnId = leftColumn.get('id'),
          leftColumnWidth = leftColumn.get('width') || defaultColumnWidth;

        // Height of bounds is height of grid minus height of scrollbar
        var boundsHeight = this.$().height() - this.get('virtualContainerMetrics.scrollbarHeight');
        var boundsTop = this.$().offset().top;

        // Left of bounds is the offset of the TH corresponding to the left columnPresentation
        var leftColumnLeftPos = this.$('th[data-column-name="' + leftColumnId + '"]').offset().left,
          boundsLeft = leftColumnLeftPos - 2;

        // Set bounds, resizePosition, origin and corresponding column (NOT presentation) for resize interaction
        this.get('resizer').setProperties({
          // Get the minimum allowance for minimizing a column from resizer.padding
          bounds: { left: boundsLeft + padding, top: boundsTop, height: boundsHeight },
          resizePosition: leftColumnWidth - padding,
          origin: leftColumnWidth - padding,
          columnId: leftColumnId
        })
      },

      cellClick: function(rowPresentation, cellContext) {
        if (rowPresentation.get('isDisabled')) {
          return;
        }

        this.sendAction('cellClick', rowPresentation, cellContext);
      },

      overlayClick: function(overlayPresentation) {
        this.get('pager').toggle(overlayPresentation.get('node'));
      },

      valueClick: function(node, columnId, value) {
        this.sendAction('valueClick', Em.get(node, 'id'), columnId, value);
      }
    },

    // If set, forces selection checkboxes hidden regardless of other selection settings
    hideSelectionCheckboxes: false,

    showingSelectionCheckboxes: function() {
      return this.get('hasSelection') && !this.get('hideSelectionCheckboxes');
    }.property('hasSelection', 'hideSelectionCheckboxes'),

    hasContextMenus: false,

    hasControlColumn: Em.computed.or('showingSelectionCheckboxes', 'hasContextMenus'),

    // Configuration
    // -------------

    columns: null,
    groupedColumnWidthMultiple: 20,
    showHeader: true,

    columnPresentation: function() {
      return this.ColumnPresentation.create({
        RowPresentation: this.RowPresentation,

        OverlayPresentationView: this.OverlayPresentationView,

        overlayPresentation: this.overlayPresentation,
        overlayComponent: this.overlayComponent,
        loadingComponent: this.loadingComponent,

        absTable: this
      });
    }.property(),

    // Virtual Pages
    // -------------

    createVirtualPage: function(traceName, traceColour) {
      return this._super(traceName, traceColour).setProperties({
        columnPresentation: this.get('columnPresentation'),
        selection: this.get('selection')
      });
    },

    // Selection
    // ---------

    selection: function() {
      return this.Selection.create({ absTable: this });
    }.property(),

    // Table Metrics
    // -------------

    // Injectable properties. The metrics object will be initialized with these.
    defaultColumnWidth: 100,
    rowHeight: 30,

    controlColumnWidth: function() {
      var padding = 10;
      var controlElementCount = (this.get('showingSelectionCheckboxes') ? 1 : 0) + (this.get('hasContextMenus') ? 1 : 0);

      return controlElementCount * 10 + (controlElementCount + 1) * padding;
    }.property('showingSelectionCheckboxes', 'hasContextMenus'),

    resizer: function() {
      return this.Resizer.create({});
    }.property(),

    measureScrollbars: function() {
      // Calculate scrollbar metrics, based on http://davidwalsh.name/detect-scrollbar-width
      var $scrollDiv = document.createElement('div');
      $scrollDiv.className = 'scrollbar-measure';
      document.body.appendChild($scrollDiv);

      var scrollbarHeight = $scrollDiv.offsetHeight - $scrollDiv.clientHeight;
      var scrollbarWidth = $scrollDiv.offsetWidth - $scrollDiv.clientWidth;

      document.body.removeChild($scrollDiv);

      return this.get('virtualContainerMetrics').setProperties({
        scrollbarWidth: scrollbarWidth,
        scrollbarHeight: scrollbarHeight
      });
    },

    measureVirtualContainer: function(scrollWidth, scrollHeight) {
      var bodyClientWidth = 0;
      var bodyClientHeight = 0;

      if (this.get('_state') === 'inDOM') {
        var $freeContainer = this.$('.free-container')[0];

        bodyClientWidth = $freeContainer.clientWidth;
        bodyClientHeight = $freeContainer.clientHeight;
      }

      return this.get('virtualContainerMetrics').setProperties({
        bodyClientWidth: bodyClientWidth,
        bodyClientHeight: bodyClientHeight
      });
    },

    updateMetrics: function() {
      if ('inDOM' === this.get('_state')) {
        this.measureScrollbars();
        this.measureVirtualContainer(
          this.get('virtualContainerMetrics.scrollbarWidth'), this.get('virtualContainerMetrics.scrollbarHeight')
        );
      }
    },

    getTableWidth: function(columns, controlColumnWidth, hasControlColumn) {
      var totalWidth = columns.reduce(function(total, column) { return total + column.width; }, 0);

      return hasControlColumn ? (totalWidth + controlColumnWidth) : totalWidth;
    },

    // Styles
    // ------

    frozenHeaderTableStyle: function() {
      var columns = this.get('columnPresentation.frozenDataColumns');
      var width = this.getTableWidth(columns, this.get('virtualContainerMetrics.controlColumnWidth'), this.get('hasControlColumn'));

      // Force the frozen header to overlap the corresponding header underneath
      return new Em.Handlebars.SafeString('width:' + (2 + width) + 'px');
    }.property('columnPresentation.frozenDataColumns', 'virtualContainerMetrics.controlColumnWidth', 'hasControlColumn'),

    frozenContainerStyleProperties: function() {
      return { left: this.get('virtualContainerMetrics.scrollLeft') + 'px' };
    }.property('virtualContainerMetrics.scrollLeft'),

    frozenContainerStylePropertiesDidChange: function() {
      // Need a higher frequency than the runloop provides
      this.applyStyles(this, '.frozen-container', this.get('frozenContainerStyleProperties'));
    }.observes('frozenContainerStyleProperties'),

    freeHeaderTableStyleProperties: function() {
      var columns = this.get('columnPresentation.freeDataColumns');
      var width = this.getTableWidth(columns, this.get('virtualContainerMetrics.controlColumnWidth'), this.get('hasControlColumn')) + 'px';

      return {
        width: width,
        left: (-1 * this.get('virtualContainerMetrics.scrollLeft')) + 'px'
      };
    }.property(
      'columnPresentation.freeDataColumns.[]',
      'virtualContainerMetrics.controlColumnWidth',
      'hasControlColumn',
      'virtualContainerMetrics.scrollLeft'
    ),

    freeHeaderTableStylePropertiesDidChange: function() {
      if (!this.get('showHeader')) {
        return;
      }

      this.applyStyles(this, '.header-container .paged-table.free', this.get('freeHeaderTableStyleProperties'));
    }.observes('freeHeaderTableStyleProperties').on('init'),

    getTableWidthStyle: function(columns, controlColumnWidth, hasControlColumn) {
      return 'width:' + this.getTableWidth(columns, controlColumnWidth, hasControlColumn) + 'px;';
    },

    virtualSizerStyle: function() {
      var top = this.get('virtualContainerMetrics.virtualHeight') - 1;

      return new Em.Handlebars.SafeString('position:absolute;' + 'top:' + top + 'px;' + 'left:0px;');
    }.property('virtualContainerMetrics.virtualHeight'),

    // Lifecycle Callbacks
    // -------------------

    init: function() {
      this._super();

      // Observers
      this.get('frozenContainerStyleProperties');
    },

    didInsertElement: function() {
      this._super();

      this.frozenContainerStylePropertiesDidChange();

      this.$('div.body-container .paged-table').on('mouseover mouseout', 'tr', this.toggleRowHighlightListener.bind(this));
    },

    willDestroyElement: function(){
      this.$('div.body-container .paged-table').off('mouseover mouseout', 'tr', this.toggleRowHighlightListener.bind(this));

      // TODO: Tipsy seems to create double elements on column header click. Ensure they are removed.
      Em.$('.tipsy').remove();

      this._super();
    },

    // Paged Data Callbacks
    // --------------------

    pageStructureDidReset: function() {
      if ('inDOM' === this.get('_state')) {
        this.$('.free-container').prop('scrollTop', 0);
      }

      this._super();
    },

    // Event Handlers
    // --------------

    bindVirtualContainerScroll: function() {
      this.$('.free-container').on('scroll', this, this.virtualContainerScroll);
    },

    unbindVirtualContainerScroll: function() {
      this.$('.free-container').off('scroll', this.virtualContainerScroll);
    },

    // Row Highlighting
    // ----------------

    getRowPresentationById: function (id) {
      var presentation = null;
      var vrPages = this.get('virtualPages');

      for (var i = 0; i < vrPages.length; i++) {
        presentation = vrPages[i].get('rows').findBy('id', id);
        if (!Em.isNone(presentation)) {
          break;
        }
      }
      return presentation;
    },

    toggleRowHighlightListener: function(e) {
      var rowId = this.$(e.target).closest('tr').attr('data-id');
      var presentation = this.getRowPresentationById(rowId);

      if (presentation) {
        presentation.set('isHighlighted', e.type === 'mouseover');
      }
    }
  });

  return PagedTableComponent.reopenClass({
    appClasses: {
      PagedTableComponent: PagedTableComponent,
      PagedTableFreePageComponent: PagedTableFreePageComponent,
      PagedTableFrozenPageComponent: PagedTableFrozenPageComponent,
      PagedTableOverlayPresentationComponent: PagedTableOverlayPresentationComponent,
      PagedTableOverlayComponent: OverlayComponent,
      PagedTableValueComponent: PagedComponent.ValueComponent,
      PagedTableLoadingComponent: PagedComponent.LoadingComponent,
      PagedTableEmptyResultComponent: PagedComponent.EmptyResultComponent
    },

    FieldConfig: FieldConfig,

    ColumnPresentation: Presentation.ColumnPresentation,
    RowPresentation: Presentation.RowPresentation,
    OverlayPresentation: Presentation.OverlayPresentation,

    OverlayComponent: OverlayComponent
  });
});
