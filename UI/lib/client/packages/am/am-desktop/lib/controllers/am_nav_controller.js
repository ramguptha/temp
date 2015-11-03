define([
  'ember',
  'packages/platform/nav-page-view'
], function (
  Em,

  NavPageView
) {
  'use strict';

  return NavPageView.NavController.extend({
    rowHeight: 35,

    content: null,
    dataStore: null,

    searchableNames: Em.computed.alias('model.searchableColumnNames'),

    searchQuery: function() {
      return this.Search.create({
        navController: this,
        adhocSearchableNamesBinding: 'navController.searchableNames',

        sort: Em.A([
          { attr: 'name', dir: 'asc' }
        ])
      });
    }.property(),

    dataPager: function() {
      return this.DataPager.create({
        navController: this,
        dataStoreBinding: 'navController.dataStore',
        searchQueryBinding: 'navController.contextQuery'
      });
    }.property(),

    contextQuery: function() {
      var contextQuery = this.get('searchQuery').copy();
      var dataStoreContext = this.get('dataStoreContext');
      if (dataStoreContext) {
        contextQuery.set('context', dataStoreContext);
      }
      return contextQuery;
    }.property('dataStoreContext', 'searchQuery.observableUri')
  });
});
