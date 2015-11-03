define([
  'packages/platform/queried-counter',
  'packages/platform/data'
], function(
  QueriedCounter,
  AbsData
) {
  'use strict';

  // Data-Sourced Counter
  // ====================
  //
  // A Data-Sourced Counter is a counter that is loaded via our data package.

  return QueriedCounter.extend({
    dataStore: null,

    init: function() {
      this._super();

      // Observers
      this.getProperties('dataStore.invalidatedAt'.w());
    },

    dataStoreDidChangeOrInvalidate: function() {
      this.reset();
    }.observes('dataStore.invalidatedAt'),

    getCount: function(context, successCallback, errorCallback) {
      return this.acquireCountFromDataStore(context, successCallback, errorCallback);
    },

    // Break getCount() implementation out into a separate method for easy asynchronous invocation by subclasses
    // that need to override it.
    acquireCountFromDataStore: function(context, successCallback, errorCallback) {
      var wrappedSuccessCallback = function(dataSource) {
        successCallback(Em.get(dataSource.objectAt(0), 'total'));
      };

      var wrappedErrorCallback = function(dataSource) {
        errorCallback(dataSource.get('lastLoadError'));
      };

      var searchQuery = this.get('searchQuery');

      this.get('dataStore').count(null, searchQuery, wrappedSuccessCallback, wrappedErrorCallback, this);
    }
  });
});
