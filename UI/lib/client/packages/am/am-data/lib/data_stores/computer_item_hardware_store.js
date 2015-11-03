define([
  'ember',
  '../am_view_data_store',
  '../am_view_data_source',
  '../specs/computer_item_hardware_spec',
  '../models/computer_item_hardware'
], function(
  Em,
  AmViewDataStore,
  AmViewDataSource,
  ComputerHardwarePcSpec,
  ComputerHardwareModel
) {
  'use strict';

  return AmViewDataStore.extend({
    Model: ComputerHardwareModel,
    Spec: ComputerHardwarePcSpec,

    createDataSourceForQuery: function(query) {
      var endPoint;
      if (query.isSearch) {
        var context = query.get('context');
        if (context && context.computerId) {
          endPoint = '/api/computers/' + context.computerId + '/hardware';
        } else throw ['context required (computerId)', query];
      } else throw ['unsupported query type', query];

      return AmViewDataSource.create({
        query: query,
        endPoint: endPoint
      });
    }
  });
});
