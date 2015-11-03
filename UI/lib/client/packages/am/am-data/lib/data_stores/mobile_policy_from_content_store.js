define([
  'ember',
  '../am_view_data_store',
  '../am_view_data_source',
  '../specs/mobile_policy_from_content_spec',
  '../models/mobile_policy'
], function(
  Em,
  AmViewDataStore,
  AmViewDataSource,
  MobilePolicyFromContentSpec,
  MobilePolicy
) {
  'use strict';

  return AmViewDataStore.extend({
    Model: MobilePolicy,
    Spec: MobilePolicyFromContentSpec,

    createDataSourceForQuery: function(query) {
      var endPoint;
      if (query.isSearch) {
        var context = query.get('context');
        if (context && context.contentId) {
          endPoint = '/api/content/' + context.contentId + '/policies';
        } else throw ['context required (contentId)', query];
      } else throw ['unsupported query type', query];

      return AmViewDataSource.create({
        query: query,
        endPoint: endPoint
      });
    }
  });
});
