define([
  'ember',
  'packages/platform/data',

  '../am_view_data_store',
  '../am_view_data_source',
  '../specs/actions_spec'
], function(
  Em,
  AbsData,

  AmViewDataStore,
  AmViewDataSource,
  ActionsSpec
) {
  'use strict';

  return AmViewDataStore.extend({
    Model: AbsData.get('Model').extend({
      Spec: ActionsSpec
    }),
    Spec: ActionsSpec,

    createDataSourceForQuery: function(query) {
      var endPoint, pushEndpoint;
      if (query.isSearch) {
        endPoint = '/api/actions/views/All';
        pushEndpoint = 'allmobileactions';
      } else if (query.isSingleton) {
        endPoint = '/api/actions';
      } else throw ['unsupported query type', query];

      return AmViewDataSource.create({
        query: query,
        endPoint: endPoint,
        pushEndpoint: pushEndpoint
      });
    }
  });
});

