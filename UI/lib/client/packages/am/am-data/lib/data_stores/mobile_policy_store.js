define([
  'ember',
  '../am_view_data_store',
  '../am_view_data_source',
  '../specs/mobile_policy_spec',
  '../models/mobile_policy'
], function(
  Em,
  AmViewDataStore,
  AmViewDataSource,
  MobilePolicySpec,
  MobilePolicy
) {
  'use strict';

  return AmViewDataStore.extend({
    Model: MobilePolicy,
    Spec: MobilePolicySpec,

    createDataSourceForQuery: function(query) {
      var endPoint, pushEndpoint;

      if (query.isSearch) {
        var context = query.get('context');
        if (context && context.contentId) {
          endPoint = '/api/content/' + context.contentId + '/policies';
        } else {
          pushEndpoint = 'allmobilepolicies';
          endPoint = '/api/views/allmobilepolicies';
        }
      } else if (query.isSingleton) {
        endPoint = '/api/policies';
      } else throw ['unknown query type', query];

      return AmViewDataSource.create({
        query: query,
        endPoint: endPoint,
        pushEndpoint: pushEndpoint
      });
    }
  });
});
