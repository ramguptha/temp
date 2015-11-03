define([
  'ember',
  '../am_view_data_store',
  '../am_view_data_source',
  '../specs/computer_item_network_adapter_spec',
  '../models/computer_item_network_adapter'
], function(
  Em,
  AmViewDataStore,
  AmViewDataSource,
  ComputerNetworkadapterSpec,
  ComputerNetworkadapterModel
) {
  'use strict';

  return AmViewDataStore.extend({
    Model: ComputerNetworkadapterModel,
    Spec: ComputerNetworkadapterSpec,

    createDataSourceForQuery: function(query) {
      var endPoint;
      if (query.isSearch) {
        var context = query.get('context');
        if (context && context.computerId) {
          endPoint = '/api/computers/' + context.computerId + '/networkadapters';
        } else throw ['context required (computerId)', query];
      } else throw ['unsupported query type', query];

      return AmViewDataSource.create({
        query: query,
        endPoint: endPoint
      });
    }
  });
});
