define([
  'ember',
  'query',
  '../am_view_data_store',
  '../am_view_data_source',
  '../specs/third_party_application_spec',
  '../models/third_party_application'
], function(
  Em,
  Query,
  AmViewDataStore,
  AmViewDataSource,
  ThirdPartyApplicationSpec,
  ThirdPartyApplication
  ) {
  'use strict';

  return AmViewDataStore.extend({
    Model: ThirdPartyApplication,
    Spec: ThirdPartyApplicationSpec,

    createDataSourceForQuery: function(query) {
      var endPoint;
      if (query.isSearch) endPoint = '/api/thirdpartyapps/views/all';
      else throw ['unknown query type', query];

      return AmViewDataSource.create({
        query: query,
        endPoint: endPoint,
        pushEndpoint: 'allthirdpartyapplications'
      });
    }
  });
});
