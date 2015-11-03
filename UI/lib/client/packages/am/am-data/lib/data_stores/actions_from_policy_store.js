define([
  'ember',
  'packages/platform/data',
  'query',
  '../am_view_data_store',
  '../am_view_data_source',
  '../specs/actions_from_policy_spec',
  '../models/in_house_application'
], function(
  Em,
  AbsData,
  Query,
  AmViewDataStore,
  AmViewDataSource,
  ActionsFromPolicySpec
  ) {
  'use strict';

  return AmViewDataStore.extend({
    Model: AbsData.get('Model').extend({
      Spec: ActionsFromPolicySpec
    }),
    Spec: ActionsFromPolicySpec,

    createDataSourceForQuery: function(query) {
      var endPoint = '/api/policies/' + query.get('context').mobilePolicyId + '/actions';

      return AmViewDataSource.create({
        query: query,
        endPoint: endPoint
      });
    }
  });
});
