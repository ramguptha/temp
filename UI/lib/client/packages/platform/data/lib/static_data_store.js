define([
  'ember',
  './data_store_base',
  './mock_data_source',
  'logger'
], function(
  Em,
  DataStoreBase,
  MockDataSource,
  logger
) {
  'use strict';

  // StaticDataStore
  // ===============
  //
  // Wraps an array or data source in a data store suitable for client-side querying.
  return DataStoreBase.extend({

    // The backing content of the data store.
    content: null,
    materializedObjects: null,

    init: function() {
      this._super();
      this.contentMembershipDidChange();
    },

    createDataSourceForQuery: function(query) {
      return MockDataSource.create({
        query: query
      });
    },

    contentMembershipDidChange: function() {
      var content = this.get('content');
      var materializedObjectsById = {};
      content.forEach(function(obj) {
        materializedObjectsById[obj.get('id')] = obj;
      });

      this.setProperties({
        materializedObjects: content,
        materializedObjectsById: materializedObjectsById
      });

      this.invalidate();
    }.observes('content.[]'),

    clearContent: function() {
      var _content = this.get('content');
      _content.replace(0, _content.get('length'), Em.A());
    }
  });
});
