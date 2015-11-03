define([
  'ember',
  '../am_view_data_store',
  '../am_view_data_source',
  '../specs/computer_item_cpu_spec',
  '../models/computer_item_cpu'
], function(
  Em,
  AmViewDataStore,
  AmViewDataSource,
  ComputerSpec,
  Computer
) {
  'use strict';

  return AmViewDataStore.extend({
    Model: Computer,
    Spec: ComputerSpec,

    createDataSourceForQuery: function(query) {
      var endPoint;
      if (query.isSearch) {
        var context = query.get('context');
        if (context && context.computerId) {
          endPoint = '/api/computers/' + context.computerId + '/cpu';
        } else throw ['context required (computerId)', query];
      } else throw ['unsupported query type', query];

      return AmViewDataSource.create({
        query: query,
        endPoint: endPoint
      });
    }
  });
});
