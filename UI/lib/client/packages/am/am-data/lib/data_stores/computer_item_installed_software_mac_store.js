define([
  'ember',
  '../am_view_data_store',
  '../am_view_data_source',
  '../specs/computer_item_installed_software_mac_spec',
  '../models/computer_item_installed_software_mac'
], function(
  Em,
  AmViewDataStore,
  AmViewDataSource,
  ComputerInstalledSoftwareSpec,
  ComputerInstalledSoftwareModel
) {
  'use strict';

  return AmViewDataStore.extend({
    Model: ComputerInstalledSoftwareModel,
    Spec: ComputerInstalledSoftwareSpec,

    createDataSourceForQuery: function(query) {
      var endPoint;
      if (query.isSearch) {
        var context = query.get('context');
        if (context && context.computerId) {
          endPoint = '/api/computers/' + context.computerId + '/software';
        } else throw ['context required (computerId)', query];
      } else throw ['unsupported query type', query];

      return AmViewDataSource.create({
        query: query,
        endPoint: endPoint
      });
    }
  });
});
