define([
  'ember',
  'packages/platform/data',

  '../am_view_data_store',
  '../am_view_data_source',
  '../specs/mobile_policy_from_action_spec'
], function(
  Em,
  AbsData,

  AmViewDataStore,
  AmViewDataSource,
  MobilePolicyFromActionSpec
) {
  'use strict';

  return AmViewDataStore.extend({
    Model: AbsData.get('Model').extend({
      Spec: MobilePolicyFromActionSpec
    }),

    Spec: MobilePolicyFromActionSpec,

    createDataSourceForQuery: function(query) {
      var endPoint;
      if (query.isSearch) {
        var context = query.get('context');
        if (context && context.actionId) {
          endPoint = '/api/actions/' + context.actionId + '/policies';
        } else throw ['context required (actionId)', query];
      } else throw ['unsupported query type', query];

      return AmViewDataSource.create({
        query: query,
        endPoint: endPoint
      });
    }
  });
});
