define([
  'ember',
  '../am_view_data_store',
  '../am_view_data_source',
  '../specs/computer_item_memory_spec',
  '../models/computer_item_memory'
], function(
  Em,
  AmViewDataStore,
  AmViewDataSource,
  ComputerMemorySpec,
  ComputerMemoryModel
) {
  'use strict';

  return AmViewDataStore.extend({
    Model: ComputerMemoryModel,
    Spec: ComputerMemorySpec,

    createDataSourceForQuery: function(query) {
      var endPoint;
      if (query.isSearch) {
        var context = query.get('context');
        if (context && context.computerId) {
          endPoint = '/api/computers/' + context.computerId + '/memory';
        } else throw ['context required (computerId)', query];
      } else throw ['unsupported query type', query];

      return AmViewDataSource.create({
        query: query,
        endPoint: endPoint
      });
    }
  });
});
