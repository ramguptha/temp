define([
  'ember',
  'query',
  '../am_view_data_store',
  '../am_view_data_source',
  '../specs/mobile_policy_filter_params_spec',
  'packages/platform/data/lib/single_result_proxy'
], function (
  Em,
  Query,
  AmViewDataStore,
  AmViewDataSource,
  PolicyParamsSpec,
  SingleResultProxy
) {
  'use strict';

  return AmViewDataStore.extend({
    Spec: PolicyParamsSpec,

    createDataSourceForQuery: function (query) {
      return AmViewDataSource.create({
        query: query,
        endPoint: '/api/smartpolicy/params'
      });
    }
  });
});
