define([
  'ember',
  '../am_view_data_store',
  '../am_view_data_source',
  '../specs/computer_item_installed_profile_spec',
  '../models/computer_item_installed_profile'
], function(
  Em,
  AmViewDataStore,
  AmViewDataSource,
  ComputerInstalledProfileSpec,
  ComputerInstalledProfileModel
) {
  'use strict';

  return AmViewDataStore.extend({
    Model: ComputerInstalledProfileModel,
    Spec: ComputerInstalledProfileSpec,

    createDataSourceForQuery: function(query) {
      var endPoint;
      if (query.isSearch) {
        var context = query.get('context');
        if (context && context.computerId) {
          endPoint = '/api/computers/' + context.computerId + '/installedprofiles';
        } else throw ['context required (computerId)', query];
      } else throw ['unsupported query type', query];

      return AmViewDataSource.create({
        query: query,
        endPoint: endPoint
      });
    }
  });
});
