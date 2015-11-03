define([
  'guid',
  'am-data',

  './am_list_controller'
], function(
  Guid,
  AmData,

  AmListController
) {
  'use strict';

  // ListControllerColumns
  // =====================
  //
  // Absolute Manage related customizations of AmListController
  // This controller:
  // - Will control the visibility of columns in the inherited classes
  // - Has the flag selectColumnsSupported set to true in order to show the Show/Hide Columns button
  //
  return AmListController.extend({
    actions: {
      selectColumns: function() {
        this.get('target').send('selectColumns', this);
      }
    },

    // This flag controls displaying the Show/Hide Columns button
    selectColumnsSupported: true,

    listColumns: null,
    listColumnsLock: Guid.generate(),
    listColumnsLoading: false,

    // Should be implemented by inherited classes
    userPrefsEndpointName: null,

    init: function() {
      this._super();

      this.loadListColumns();
    },

    getDataSourceForPage: function (lock, query) {
      query = this.applyDataStoreContext(query);

      return query ? this.get('dataStore').acquire(null, query) : null;
    },

    loadListColumns: function() {
      var controller = this;

      controller.setProperties({
        paused: true,
        listColumnsLoading: true
      });

      AmData.get('stores.userPrefsStore').acquireOne(this.get('listColumnsLock'), this.get('userPrefsEndpointName'),
        function(dataSource) {
          controller.setProperties({
            visibleColumnNames: dataSource.get('content'),
            paused: false,
            listColumnsLoading: false
          });
        },
        function() {
          controller.setProperties({
            paused: false,
            listColumnsLoading: false
          });
        }, false, false);
    },

    onVisibleColumnNamesChanged: function() {
      if (this.get('paused') || this.get('listColumnsLoading')) return;

      // If after updating the visibility of columns through show/hide column modal
      // the searched column in the ad-hoc search was going to be invisible
      // reset the list of columns in the search to 'All' option
      var visibleColumnNames = this.get('visibleColumnNames'),
        searchAttr = this.get('searchQuery.searchAttr');

      if (!visibleColumnNames.contains(searchAttr)) {
        this.set('searchQuery.searchAttr', null);
      }

      var action = AmData.get('actions.UpdateUserPrefAction').create({
        value: visibleColumnNames,
        endPoint: 'user/prefs/' + this.get('userPrefsEndpointName')
      });
      action.invoke();

    }.observes('visibleColumnNames.[]')
  });
});
