define([
  'ember',

  'packages/platform/aggregate',
  'packages/platform/query',
  'packages/platform/advanced-filter',
  'packages/platform/selection',
  'packages/platform/data-pager',
  'packages/platform/data-counter',
  'packages/platform/data-table-component',
  'packages/platform/desktop',

  'env'
], function(
  Em,

  Aggregate,
  Query,
  AdvancedFilter,
  Selection,
  DataPager,
  DataCounter,
  DataTableComponent,
  Desktop,

  env
) {
  'use strict';

  // Data Table Controller
  // =====================
  //
  // A controller with convenience properties setup to drive a data table component.
  //
  // Required properties:
  //
  // - dataStore
  // - visibleColumnNames

  return Em.Controller.extend({
    Search: Query.Search,
    SearchableSelectView: Desktop.SearchableSelectView,

    AdvancedFilter: AdvancedFilter,
    AdvancedFilterController: Desktop.AdvancedFilterController,

    DataPager: DataPager,
    DataCounter: DataCounter,
    FieldConfig: DataTableComponent.FieldConfig,

    Selection: Selection,

    overlayPresentation: DataTableComponent.OverlayPresentation,
    overlayComponent: 'paged-table-overlay',

    env: env,

    init: function() {
      this._super();

      var searchQuery = this.get('searchQuery');
      var spec = this.get('spec');
      if (!searchQuery && !Em.isNone(spec)) {
        this.set('searchQuery', this.createSearchQuery(spec));
      }

      if (!this.get('advancedFilterController')) {
        this.set('advancedFilterController', this.createAdvancedFilterController());
      }
    },

    createSearchQuery: function(spec) {
      return this.Search.create({
        dataTableController: this,

        adhocSearchableNamesBinding: 'dataTableController.searchableColumnNames',

        advancedFilterBinding: 'dataTableController.advancedFilter',

        contextBinding: 'dataTableController.context',
        contextFilterBinding: 'dataTableController.contextFilter',

        sortBinding: 'dataTableController.sort',
        groupBinding: 'dataTableController.group'
      });
    },

    createAdvancedFilterController: function() {
      return this.AdvancedFilterController.create({
        parentController: this,
        filterBinding: 'parentController.advancedFilter',
        dataStoreSpecBinding: 'parentController.spec'
      });
    },

    advancedFilterController: null,
    searchFilterSupported: true,

    // REQUIRED
    dataStore: null,

    paused: false,
    spec: Em.computed.oneWay('dataStore.Spec'),

    dataPager: function() {
      return this.DataPager.create({
        dataTableController: this,
        dataStoreBinding: 'dataTableController.dataStore',
        searchQueryBinding: 'dataTableController.searchQuery'
      });
    }.property(),

    // Search Query
    // ------------

    searchQuery: null,

    advancedFilter: null,

    context: null,
    contextFilter: null,

    sort: function() {
      return Em.A([{
        attr: this.get('spec.idNames.0'),
        dir: 'asc'
      }]);
    }.property('spec.idNames.[]'),

    group: Em.A(),

    clearSearchFilter: function() {
      var searchQuery = this.get('searchQuery');

      if (searchQuery) {
        searchQuery.setProperties({
          searchFilter: null,
          searchAttr: null
        });
      }
    },

    // Selection
    // ---------

    selector: function() {
      return Selection.ToggleMany.create();
    }.property(),

    selections: function() {
      return Em.A();
    }.property(),

    // Column Names
    // ------------

    // All columns, displayed or not
    //
    // Sample output: [ 'identifier', 'driveVolumes', 'version.build', 'version.major' ]
    columnNames: function() {
      var columnNames = Em.A();
      var spec = this.get('spec');

      if (!Em.isNone(spec)) {
        var names = spec.get('names');
        var resources = spec.get('resourceByName');

        names.forEach(function(name) {
          var resourceType = resources[name].type;
          if (Aggregate.OneToOne.detectInstance(resourceType)) {
            var mappedNames = resourceType.get('Spec.names').map(function(subName) {
              return name + '.' + subName;
            });
            columnNames.pushObjects(mappedNames);
          } else {
            columnNames.pushObject(name);
          }
        });
      }

      return columnNames;
    }.property('spec.names.[]'),

    // Columns for display
    visibleColumnNames: Em.A(),

    // Columns for ad-hoc search
    searchableColumnNames: function() {
      var searchableColumnNames = Em.A();

      var spec = this.get('spec');
      var visibleColumnNames = this.get('visibleColumnNames');
      if (spec && !Em.isEmpty(visibleColumnNames)) {
        searchableColumnNames = spec.filterSearchableNames(visibleColumnNames);
      }

      return searchableColumnNames;
    }.property('spec', 'visibleColumnNames.[]'),

    // Columns for the Data Table
    // --------------------------
    //
    // Because of 1-N aggregates, it is impossible to have a columns property that can capture every possible
    // column. Instead, delegate column creation to createColumns(). Note that sub-classes often have other 
    // dependencies driving their column structures. In such cases, _visibleColumns_ and _searchableColumns_
    // should be overridden to name them.

    visibleColumns: function() {
      return this.createColumns(this.get('visibleColumnNames'));
    }.property('spec', 'visibleColumnNames.[]', 'searchQuery.group.[]'),

    searchableColumns: function() {
      return this.createColumns(this.get('searchableColumnNames'));
    }.property('spec', 'searchableColumnNames.[]'),

    createColumns: function(names) {
      var self = this;
      var columns = Em.A();
      var spec = this.get('spec');
      var group = this.get('searchQuery.group') || Em.A();

      if (spec && Em.Enumerable.detect(names)) {
        columns = names.map(function(name, id) {
          return self.FieldConfig.create({
            id: name,
            spec: spec,
            isGrouped: group.contains(name)
          });
        });
      }

      return columns;
    },

    filterAndReorderColumns: function(columns, names) {
      var filteredColumns = Em.A();

      if (!Em.isEmpty(names)) {
        filteredColumns = names.map(function(name) {
          return columns.findBy('id', name);
        });
      }

      return filteredColumns;
    },

    // Options for adhoc-search
    // ------------------------

    searchableOptions: function() {
      return this.createSearchableOptions(this.get('searchableColumns'));
    }.property('searchableColumns.[]'),

    createSearchableOptions: function(columns) {
      return columns.map(function(column) {
        return {
          name: column.get('id'),
          label: column.renderLabel()
        };
      });
    },

    // Counter
    // -------

    dataCounter: function() {
      return this.DataCounter.create({
        dataTableController: this,
        dataStoreBinding: 'dataTableController.dataStore',
        searchQueryBinding: 'dataTableController.searchQuery',
        autoCountDelayInMillisecondsBinding: 'dataTableController.countDelayInMilliseconds',
        pausedBinding: 'dataTableController.paused'
      });
    }.property(),

    countDelayInMilliseconds: Em.computed.oneWay('env.countDelayInMilliseconds'),

    tTotalSummary: 'shared.totalSummary'.tr('total'),
    total: function() {
      return this.get('dataCounter.total');
    }.property('dataCounter.total')
  });
});
