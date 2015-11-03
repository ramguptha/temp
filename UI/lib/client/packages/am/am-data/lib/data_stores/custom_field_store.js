define([
  'ember',
  '../am_view_data_store',
  '../am_view_data_source',
  '../specs/custom_field_spec',
  '../models/custom_field'
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
    Spec:   Spec,

    createDataSourceForQuery: function(query) {
      var endPoint;

      if (query.isSearch) {
        var context = query.get('context');
        if (context && context.customFieldId) {
          endPoint = '/api/customfields/' + context.customFieldId;
        } else {
          endPoint = '/api/views/allcustomfields';
        }
      } else throw ['unknown query type', query];

      return AmViewDataSource.create({
        query: query,
        endPoint: endPoint
      });
    }
  });
});
