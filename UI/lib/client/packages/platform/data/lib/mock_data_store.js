define([
  'ember',
  './data_store',
  './mock_data_source',
  'logger'
], function(
  Em,
  DataStore,
  MockDataSource,
  logger
) {
  'use strict';

  var maxAssignedId = 0;

  return DataStore.extend({
    MockData: null,

    materializedObjects: null,

    init: function() {
      this._super();

      var attrHashes = this.get('MockData').map(function(spec, idx) {
        if (undefined === spec.id) {
          spec.id = this.nextId();
        }
        return spec;
      }, this);

      this.loadAttrHashes(attrHashes);
      var materializedObjectsById = this.get('materializedObjectsById');
      this.set('materializedObjects', attrHashes.map(function(attrs) {
        return materializedObjectsById[attrs.id];
      }));
    },

    nextId: function() {
      return maxAssignedId += 1;
    },

    createDataSourceForQuery: function(query) {
      return MockDataSource.create({
        query: query
      });
    }
  });
});
