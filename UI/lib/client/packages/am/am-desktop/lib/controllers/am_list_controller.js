define([
  'ember',
  'packages/platform/enum-util',
  'packages/platform/data-table-controller'
], function (
  Em,
  EnumUtil,
  DataTableController
) {
  'use strict';

  // AmListController
  // =====================
  //
  // Absolute Manage related customizations of Data Table Controller
  // Data Tables in AM share the same functionality with some exceptions in terms of:
  // 1- Expected behaviour on clicking on a row
  // 2- Filtering some records out
  // 3- Renderings some columns headers and data
  //

  return DataTableController.extend({

    actions: {
      // Drill down using the node's id if the clicking happens
      rowClick: function(row) {
        // We want to only check the checkboxes. No need to do anything here
        if (this.get('selectOnRowClick')) { return; }

        // Drill down on the selected row
        this.send('gotoListItem', row.get('node.id'));
      },

      resize: function(id) {
        this._super(id);
      }
    },

    // Controls the visibility of Show/Hide Columns button
    // The inherited class (listControllerColumns) is the base class for any controller with this flag on
    selectColumnsSupported: false,

    // Selection
    // ---------

    selectionEnabled: false,

    // Set to true if:
    // - Drilling down will happen upon clicking on a row
    // - The checkbox on that row should be checked.
    hasRowClick: false,

    // Set to true if upon selection of a row, the checkbox on that row should be checked.
    // Please note to get this work, the 'hasRowClick' needs to be set to true too.
    selectOnRowClick: false,

    // This flag is in charge of laying out the selection buttons horizontally
    // Only some of the AM views have the button groups as a drop down list
    isButtonGroupHorizontal: true,

    // Path to the label of the selection actions' drop down button.
    // In AM the label for this button is 'Commands'
    actionsLabelPath: 'amComputer.computerListPage.commandsTitle',

    // Data Table Bindings
    // -------------------

    countDelayInMilliseconds: 1000,

    dataCounter: function() {
      return this.DataCounter.create({
        dataTableController: this,
        dataStoreBinding: 'dataTableController.dataStore',
        searchQueryBinding: 'dataTableController.contextQuery',
        autoCountDelayInMillisecondsBinding: 'dataTableController.countDelayInMilliseconds',
        pausedBinding: 'dataTableController.paused'
      });
    }.property(),

    dataPager: function() {
      return this.DataPager.create({
        listController: this,
        dataStoreBinding: 'listController.dataStore',
        searchQueryBinding: 'listController.contextQuery',

        // Filter data if applicable
        // If no data is filtered do nothing
        preFilterData: function(loadingData) {
          var listController = this.get('listController');

          var filteredData = listController.getFilteredData(loadingData.data);

          if (loadingData.length !== filteredData.length) {
            loadingData.data = filteredData;
          }
        }
      });
    }.property(),

    visibleColumnNames: Em.A(),

    // Perform specific formatting to specific columns that apply to all the AM applications
    createColumns: function(names) {
      var columns = this._super(names), self = this;

      columns.forEach(function(column) {
        // We need to show specific images for column 'icon'
        if (column.get('name') === 'icon') {
          // Icon column is not sortable
          column.setProperties({
            apiBase: self.get('apiBase'),
            isSortable: false,
            valueComponent: 'am-formatted-icon'
          });
        }
      });

      return columns;
    },

    // If there is a potential need for excluding some records,
    // over-ride this function
    getFilteredData: function(data) {
      return data;
    },

    // Returns data of each row using an array of ids
    getRowData: function(ids) {
      return this.get('dataPager').lookupData(ids);
    },

    listRowData: Em.A(),

    // In the new design of grid, we wouldn't have access to the data of a row that is invisible,
    // meaning if we select a row and using search, we filter it out, we won't have its data to
    // provide the selection action with.
    // This not so beautiful implementation is to provide the selection actions with the entire list data at all time
    setListRowData: function() {
      var pager = this.get('dataPager');
      var listRowData = this.get('listRowData');

      if (pager.get('isFullyLoaded')) {
        var data = pager.get('root.children').mapBy('nodeData');

        if (Em.isEmpty(this.get('searchQuery.searchFilter'))) {
          this.set('listRowData', data);
        } else {
          var listRowDataIds = listRowData.mapBy('id') || Em.A();

          // make sure no new items were added while we have a searchFilter and if there were, add them to the listRowData array
          data.forEach(function(item) {
            var id = item.get('id') || null;

            if(id && !listRowDataIds.contains(id)) {
              listRowData.pushObject(item);
            }
          });

        }
      }

    }.observes('dataPager.isFullyLoaded'),



    getSelectionActionContext: function(selectedIds, listRowData) {
      var context = Em.A();
      listRowData.forEach(function (row) {
        if (selectedIds.contains(row.get('data.id').toString())) {
          context.pushObject(row);
        }
      });

      return context;
    },

    // Query
    // -----

    // Controls display of search box. Only SummaryList views don't display the search box.
    adhocSearchSupported: true,

    // Default sorting will be changed per need in inherited code modules
    sort: Em.A([{
      attr: 'name',
      dir: 'asc'
    }]),

    // Copy of SearchQuery which will have 'context' if applicable
    contextQuery: function() {
      var contextQuery = this.get('searchQuery').copy();
      var dataStoreContext = this.get('dataStoreContext');

      if (dataStoreContext) {
        contextQuery.set('context', dataStoreContext);
      }
      return contextQuery;
    }.property('dataStoreContext', 'searchQuery.observableUri'),

    // Potential list of ids to be excluded from the displayed list
    excludedIds: Em.A(),

    // Most of the filtered lists in AM's modals exclude the already selected ids
    // This function will be called from getFilteredData is necessary
    excludeIds: function(data, ids) {
      return EnumUtil.exclude(data, 'id', ids, null);
    },

    // Will be called from the router any time we need to reset something in the controller upon switching between views
    resetController: function() {
      // Does not seem to be necessary to run this command at this moment.
      // Also the inherited classes can add this call if applicable.
      // this.clearSearchFilter();
      this.clearSelections();
    },

    clearSelections: function() {
      this.set('selections', Em.A());
    },

    clearSelectionsListWhenDataStoreInvalidates: function() {
      this.clearSelections();
    }.observes('dataStore.invalidatedAt')
  });
});
