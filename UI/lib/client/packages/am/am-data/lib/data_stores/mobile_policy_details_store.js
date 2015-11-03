define([
  'ember',
   '../am_view_data_store',
  '../am_view_data_source',
  '../specs/mobile_policy_details_spec',
  '../models/mobile_policy_details'
], function(
  Em,
  AmViewDataStore,
  AmViewDataSource,
  MobilePolicyDetailsSpec,
  MobilePolicyDetails
  ){
  'use strict';

  return AmViewDataStore.extend({
    Model: MobilePolicyDetails,
    Spec: MobilePolicyDetailsSpec,

    createDataSourceForQuery: function(query) {
      var endPoint = '/api/mobilepolicies';

      return AmViewDataSource.create({
        query: query,
        endPoint: endPoint
      });
    }
  });
});
