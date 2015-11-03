define([
  'ember',
  '../am_view_data_store',
  '../am_view_data_source',
  '../specs/computer_item_volume_mac_spec',
  '../models/computer_item_volume_mac'
], function(
  Em,
  AmViewDataStore,
  AmViewDataSource,
  ComputerVolumeSpec,
  ComputerVolumeModel
) {
  'use strict';

  return AmViewDataStore.extend({
    Model: ComputerVolumeModel,
    Spec: ComputerVolumeSpec,

    createDataSourceForQuery: function(query) {
      var endPoint;
      if (query.isSearch) {
        var context = query.get('context');
        if (context && context.computerId) {
          endPoint = '/api/computers/' + context.computerId + '/volumes';
        } else throw ['context required (computerId)', query];
      } else throw ['unsupported query type', query];

      return AmViewDataSource.create({
        query: query,
        endPoint: endPoint
      });
    }
  });
});
