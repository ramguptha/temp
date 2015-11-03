define([
  'ember',
  'query',
  '../am_view_data_store',
  '../am_view_data_source',
  '../specs/in_house_application_from_policy_spec',
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
      var endPoint = '/api/policies/' + query.get('context').mobilePolicyId + '/inhouseapps';

      return AmViewDataSource.create({
        query: query,
        endPoint: endPoint
      });
    }
  });
});
