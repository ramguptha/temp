define([
  'ember',
  'query',
  '../am_view_data_store',
  '../am_view_data_source',
  '../specs/in_house_application_spec',
  '../models/in_house_application'
], function(
  Em,
  Query,
  AmViewDataStore,
  AmViewDataSource,
  InHouseApplicationSpec,
  InHouseApplication
  ) {
  'use strict';

  return AmViewDataStore.extend({
    Model: InHouseApplication,
    Spec: InHouseApplicationSpec,

    createDataSourceForQuery: function(query) {
      var endPoint;
      if (query.isSearch) endPoint = '/api/inhouseapps/views/all';
      else throw ['unknown query type', query];

      return AmViewDataSource.create({
        query: query,
        endPoint: endPoint,
        pushEndpoint: 'allinhouseapplications'
      });
    }
  });
});
