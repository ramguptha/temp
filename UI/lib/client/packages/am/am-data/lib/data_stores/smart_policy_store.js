define([
  'ember',
  'query',
  '../am_view_data_store',
  '../am_view_data_source',
  '../specs/smart_policy_spec',
  '../models/smart_policy_profile'
], function(
  Em,
  Query,
  AmViewDataStore,
  AmViewDataSource,
  SmartPolicySpec,
  SmartPolicyProfile
  ) {
  'use strict';

  return AmViewDataStore.extend({
    Model: SmartPolicyProfile,
    Spec: SmartPolicySpec,

    createDataSourceForQuery: function(query) {
      var endPoint = '/api/policies/smart';

      return AmViewDataSource.create({
        query: query,
        endPoint: endPoint
      });
    }
  });
});

