define([
  'ember',
  '../am_view_data_store',
  '../am_view_data_source',
  '../specs/computer_item_system_software_spec',
  '../models/computer_item_system_software'
], function(
  Em,
  AmViewDataStore,
  AmViewDataSource,
  ComputerSystemSoftwareSpec,
  ComputerSystemSoftwareModel
) {
  'use strict';

  return AmViewDataStore.extend({
    Model: ComputerSystemSoftwareModel,
    Spec: ComputerSystemSoftwareSpec,

    createDataSourceForQuery: function(query) {
      var endPoint;
      if (query.isSearch) {
        var context = query.get('context');
        if (context && context.computerId) {
          endPoint = '/api/computers/' + context.computerId + '/systemsoftware';
        } else throw ['context required (computerId)', query];
      } else throw ['unsupported query type', query];

      return AmViewDataSource.create({
        query: query,
        endPoint: endPoint
      });
    }
  });
});
