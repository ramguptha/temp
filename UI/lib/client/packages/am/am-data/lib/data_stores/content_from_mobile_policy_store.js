define([
  'ember',
  '../am_view_data_store',
  '../am_view_data_source',
  '../specs/content_from_mobile_policy_spec',
  '../models/content'
], function(
  Em,
  AmViewDataStore,
  AmViewDataSource,
  ContentFromMobilePolicySpec,
  Content
) {
  'use strict';

  return AmViewDataStore.extend({
    Model: Content,
    Spec: ContentFromMobilePolicySpec,

    createDataSourceForQuery: function(query) {
      var endPoint;
      if (query.isSearch) {
        var context = query.get('context');
        if (context && context.mobilePolicyId) {
          endPoint = '/api/policies/' + context.mobilePolicyId + '/content';
        } else throw ['context required (mobilePolicyId)', query];
      } else throw ['unsupported query type', query];

      return AmViewDataSource.create({
        query: query,
        endPoint: endPoint
      });
    }
  });
});
