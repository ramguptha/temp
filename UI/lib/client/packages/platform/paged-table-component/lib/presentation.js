define([
  'ember',
  'packages/platform/paged-component'
], function(
  Em,
  PagedComponent
) {
  'use strict';

  // Presentation
  // ============
  //
  // Internal classes used by AbsTable to control rendering.

  // Column Presentation
  // -------------------
  //
  // Transforms FieldConfig to the native column presentation format.

  var ColumnPresentation = Em.Object.extend({
    RowPresentation: null,

    overlayPresentation: null,
    overlayComponent: null,
    loadingComponent: null,

    absTable: null,

    sort: Em.computed.oneWay('absTable.pager.searchQuery.sort'),
    columns: Em.computed.oneWay('absTable.columns'),
    defaultColumnWidth: Em.computed.oneWay('absTable.defaultColumnWidth'),

    hasControlColumn: Em.computed.oneWay('absTable.hasControlColumn'),
    hasSelection: Em.computed.oneWay('absTable.hasSelection'),

    dataColumns: function() {
      var self = this;
      var columns = this.get('columns') || [];
      var defaultColumnWidth = this.get('defaultColumnWidth');

      var sortAttrMap = {}, sort = this.get('sort');
      if (!sort) { return []; }

      sort.forEach(function(sortItem) {
        sortAttrMap[sortItem.attr] = sortItem.dir;
      });

      return columns.map(function(column) {
        var isGrouped = column.get('isGrouped');

        var width = null;
        if (isGrouped) {
          width = self.get('absTable.groupedColumnWidthMultiple');
        } else {
          width = (column.get('width') || defaultColumnWidth);
        }

        var isSorted = !Em.isEmpty(sortAttrMap[column.get('id')]);

        var isShowingLabelIcon = column.get('labelIconClass') && (width <= column.get('labelIconWidthThreshold'));
        var header = column.renderLabel();

        return {
          config: column,
          style: new Em.Handlebars.SafeString('width:' + width + 'px;'),
          width: width,

          isGrouped: isGrouped,
          isFrozen: column.get('isFrozen'),
          isSorted: !isGrouped && isSorted,
          isShowingLabelIcon: isShowingLabelIcon,

          sortDir: isSorted ? sortAttrMap[column.get('id')] : null,
          content: header,
          labelIconClass: column.get('labelIconClass')
        };
      });
    }.property('sort', 'defaultColumnWidth', 'columns.@each.width'),

    freeDataColumns: function() {
      var dataColumns = this.get('dataColumns');

      var groupedColumnCount = 0;
      var width = 0;
      for (var i = 0; i < dataColumns.length && dataColumns[i].isGrouped; i++) {
        groupedColumnCount += 1;
        width += dataColumns[i].width;
      }

      var freeDataColumns = Em.A();
      if (groupedColumnCount > 0) {
        freeDataColumns.pushObject({
          config: null,
          style: new Em.Handlebars.SafeString('width:' + width + 'px;'),
          width: width,

          isGrouped: true,
          isFrozen: true,
          isSorted: false,
          isShowingLabelIcon: false,

          sortDir: null,
          content: ''
        });
      }

      freeDataColumns.pushObjects(dataColumns.slice(groupedColumnCount));

      return freeDataColumns;
    }.property('dataColumns.[]'),

    freeDataColumnCount: Em.computed.oneWay('freeDataColumns.length'),

    freeColumnCount: function() {
      return this.get('freeDataColumnCount') + (this.get('hasControlColumn') ? 1 : 0);
    }.property('freeDataColumnCount', 'hasControlColumn'),

    frozenDataColumns: function() {
      return this.get('freeDataColumns').slice(0, this.get('frozenDataColumnCount'));
    }.property('freeDataColumns.[]', 'frozenDataColumnCount'),

    frozenDataColumnCount: function() {
      var columns = this.get('columns') || [];
      var count = 0;

      var hasGroups = false;
      for (var i = 0; i < columns.get('length'); i++) {
        var column = columns.objectAt(i);

        if (column.get('isGrouped')) {
          hasGroups = true;
        }

        if (column.get('isFrozen')) {
          count += 1;
        } else {
          break;
        }
      }

      return count + (hasGroups ? 1 : 0);
    }.property('columns@each.isFrozen', 'columns.[]'),

    frozenColumnCount: function() {
      return this.get('frozenDataColumnCount') + (this.get('hasControlColumn') ? 1 : 0);
    }.property('frozenDataColumnCount', 'hasControlColumn')
  });

  // RowPresentation
  // ---------------
  //
  // Encapsulates presentation for a row.
  
  var RowPresentation = PagedComponent.RowPresentation.extend({
    CellContext: Em.Object.extend({
      pagedComponent: null,
      cellIndex: null,

      row: null,
      node: Em.computed.oneWay('row.node'),

      column: function() {
        var freeDataColumns = this.get('row.columnPresentation.freeDataColumns');
        return freeDataColumns ? freeDataColumns.objectAt(this.get('cellIndex')) : null;
      }.property('row.columnPresentation.freeDataColumns.[]', 'cellIndex'),

      name: Em.computed.oneWay('column.config.id'),
      presenter: Em.computed.oneWay('column.config'),

      defaultComponent: Em.computed.oneWay('pagedComponent.valueComponent'),
      presenterComponent: Em.computed.oneWay('presenter.valueComponent'),
      valueComponent: function() {
        return this.get('presenterComponent') || this.get('defaultComponent');
      }.property('defaultComponent', 'presenterComponent'),

      defaultColumnWidth: 100,

      style: function() {
        var defaultColumnWidth = this.get('defaultColumnWidth');
        var width = this.get('column.width') || defaultColumnWidth;

        return new Em.Handlebars.SafeString('width:' + width + 'px;');
      }.property('defaultColumnWidth', 'column.width'),

      click: function(e) {
        this._super(e);
        this.get('pagedComponent').send('cellClick', this.get('row'), this.get('cell'));
      }
    }),

    columnPresentation: null,
    selection: null,

    isHighlighted: false,

    hasControlColumn: Em.computed.oneWay('columnPresentation.hasControlColumn'),
    hasSelection: Em.computed.oneWay('selection.hasSelection'),

    freeColumnCount: Em.computed.oneWay('columnPresentation.freeColumnCount'),
    frozenColumnCount: Em.computed.oneWay('columnPresentation.frozenColumnCount'),

    freeDataCells: function() {
      var self = this;

      this.trace('GET freeDataCells');
      var columnPresentation = this.get('columnPresentation');
      var columns = this.get('columnPresentation.freeDataColumns');

      return columns.map(function(column, i) {
        return self.CellContext.create({
          pagedComponent: columnPresentation.get('absTable'),
          row: self,
          cellIndex: i
        });
      });
    }.property('columnPresentation.freeDataColumns.[]'),

    frozenDataCells: function() {
      this.trace('GET frozenDataCells');
      return this.get('freeDataCells').slice(0, this.get('columnPresentation.frozenDataColumnCount'));
    }.property('columnPresentation.frozenDataColumnCount', 'freeDataCells.[]'),

    style: function() {
      var traceColour = this.get('tracer.colour');
      var tracing = this.get('tracer.tracing');
      var backgroundStyle = (traceColour && tracing) ? 'background-color: ' + traceColour + ';' : '';

      return new Em.Handlebars.SafeString(backgroundStyle);
    }.property('tracer.tracing', 'tracer.colour'),

    overlayActions: null,
    overlayActionsDisabled: false
  });

  // Overlay Presentation
  // --------------------

  var OverlayPresentation = Em.Object.extend({
    virtualPage: null,
    virtualContainerMetrics: null,
    virtualPageMetrics: null,

    rowIndex: null,
    row: function() {
      var row = null;

      var rows = this.get('virtualPage.rows');
      if (rows) {
        row = rows.objectAt(this.get('rowIndex'));
      }

      return row;
    }.property('virtualPage.rows.[]', 'rowIndex'),

    pagedComponent: Em.computed.oneWay('virtualPage.pagedComponent'),
    columnPresentation: Em.computed.oneWay('virtualPage.columnPresentation'),
    node: Em.computed.oneWay('row.node'),

    nodeId: function() {
      return this.get('node').id;
    }.property('node'),

    nodeData: function() {
      return this.get('node').nodeData;
    }.property('node'),

    parentNode: function() {
      return this.get('node').parentNode;
    }.property('node'),

    parentNodeId: function() {
      var parentNode = this.get('node').parentNode;
      return parentNode ? parentNode.id : null;
    }.property('node'),

    isGroup: function() {
      return this.get('node').isGroup;
    }.property('node'),

    column: function() {
      var dataColumns = this.get('columnPresentation.dataColumns.[]');
      var node = this.get('node');
      return (dataColumns && node) ? dataColumns.objectAt(node.depth - 1) : null;
    }.property('columnPresentation.dataColumns.[]', 'node'),

    nodeStyle: function() {
      return new Em.Handlebars.SafeString('padding-left: ' + ((this.get('node.depth') - 1) * 20 + (this.get('pagedComponent.hasContextMenus') ? 30 : 0)) + 'px;');
    }.property('node.depth'),

    valueComponent: function() {
      return this.get('column.config.valueComponent') || this.get('pagedComponent.valueComponent');
    }.property('column.config.valueComponent', 'pagedComponent.valueComponent'),

    // Wrap up valueComponent and its dependencies in a "context" object, previously because we were forced to use 
    // #with to render the component in order to handle dynamic changes to the class.
    //
    // TODO: Investigate removing this now that {{#with}} is no longer necessary.
    valueContext: function() {
      return {
        pagedComponent: this.get('pagedComponent'),
        node: this.get('node'),
        presenter: this.get('column.config'),
        valueComponent: this.get('valueComponent')
      };
    }.property('pagedComponent', 'node', 'column.config', 'valueComponent'),

    // Optional array of subContent. Each element is an instance of Presenter.
    subContent: null,

    subContexts: function() {
      var self = this;

      var pagedComponent = this.get('pagedComponent');
      var node = this.get('node');
      var subContent = this.get('subContent');

      var subContexts = Em.A();

      if (subContent) {
        subContexts = subContent.map(function(presenter) {
          return {
            id: presenter.get('name'),

            pagedComponent: pagedComponent,
            node: node,
            presenter: presenter,

            label: presenter.renderLabel(),
            valueComponent: presenter.get('valueComponent') || pagedComponent.get('valueComponent')
          };
        });
      }

      return subContexts;
    }.property('pagedComponent', 'node', 'subContent.[]'),

    // Deferred properties

    isDeferred: Em.computed.oneWay('row.isDeferred'),
    isLoading: Em.computed.oneWay('row.isLoading'),
    error: Em.computed.oneWay('row.error'),

    // PagedComponent and derived properties

    tTryAgain: Em.computed.oneWay('row.tTryAgain'),
    tShowErrorDetail: Em.computed.oneWay('row.tShowErrorDetail'),
    tUnableLoadData: Em.computed.oneWay('row.tUnableLoadData'),

    style: function() {
      var rowOffset = this.get('virtualContainerMetrics.rowHeight') * this.get('rowIndex');
      var top = this.get('virtualPageMetrics.offsetTop') + rowOffset;
      var topStyle = 'top:' + top + 'px;';

      var widthStyle = 'width:' + this.get('virtualContainerMetrics.bodyClientWidth') + 'px;';
      var heightStyle = 'height:' + this.get('virtualContainerMetrics.rowHeight') + 'px;';

      return new Em.Handlebars.SafeString(topStyle + widthStyle + heightStyle);
    }.property(
      'virtualContainerMetrics.scrollbarWidth',
      'virtualContainerMetrics.bodyClientWidth',
      'virtualContainerMetrics.rowHeight',
      'virtualPageMetrics.offsetTop',
      'rowIndex'
    )
  });

  return {
    ColumnPresentation: ColumnPresentation,
    RowPresentation: RowPresentation,
    OverlayPresentation: OverlayPresentation
  };
});
