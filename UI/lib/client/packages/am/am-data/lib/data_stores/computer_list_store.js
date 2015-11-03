define([
  'ember',
  '../am_view_data_store',
  '../am_view_data_source',
  '../specs/computer_list_spec',
  '../models/computer_list'
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
        if (context && context.computerListName) {
          endPoint = '/api/computers/views/' + context.computerListName;
        } else throw ['context required (computerListName)', query];
      } else if (query.isSingleton) endPoint = '/api/computers';
      else throw ['unknown query type', query];

      return AmViewDataSource.create({
        query: query,
        endPoint: endPoint
      });
    }

  });
});
