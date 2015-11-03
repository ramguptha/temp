define([
  'ember',
  '../am_view_data_store',
  '../am_view_data_source',
  '../specs/computer_item_missing_patch_pc_spec',
  '../models/computer_item_missing_patch_pc'
], function(
  Em,
  AmViewDataStore,
  AmViewDataSource,
  Spec,
  Model
) {
  'use strict';

  return AmViewDataStore.extend({
    Model: Model,
    Spec: Spec,

    createDataSourceForQuery: function(query) {
      var endPoint;
      if (query.isSearch) {
        var context = query.get('context');
        if (context && context.computerId) {
          endPoint = '/api/computers/' + context.computerId + '/missingpatches';
        } else throw ['context required (computerId)', query];
      } else throw ['unsupported query type', query];

      return AmViewDataSource.create({
        query: query,
        endPoint: endPoint
      });
    }
  });
});
