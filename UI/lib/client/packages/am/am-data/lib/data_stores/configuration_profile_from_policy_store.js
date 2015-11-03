define([
  'ember',
  'query',
  '../am_view_data_store',
  '../am_view_data_source',
  '../specs/configuration_profile_from_policy_spec',
  '../models/mobile_policy_configuration_profile'
], function (
  Em,
  Query,
  AmViewDataStore,
  AmViewDataSource,
  MobilePolicyConfigProfileSpec,
  MobilePolicyConfigProfile
  ) {
  'use strict';

  return AmViewDataStore.extend({
    Model: MobilePolicyConfigProfile,
    Spec: MobilePolicyConfigProfileSpec,

    createDataSourceForQuery: function (query) {

      var endPoint = '/api/policies/' + query.get('context').mobilePolicyId + '/configurationprofiles';

      return AmViewDataSource.create({
        query: query,
        endPoint: endPoint
      });
    }
  });
});