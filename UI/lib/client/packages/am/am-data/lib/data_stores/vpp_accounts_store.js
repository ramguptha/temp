define([
  'ember',
  'packages/platform/data',

  '../am_view_data_store',
  '../am_view_data_source',
  '../specs/vpp_accounts_spec'
], function(
  Em,
  AbsData,

  AmViewDataStore,
  AmViewDataSource,
  VppAccountsSpec
) {
  'use strict';

  return AmViewDataStore.extend({
    Model: AbsData.get('Model').extend({
      Spec: VppAccountsSpec
    }),
    Spec: VppAccountsSpec,

    createDataSourceForQuery: function(query) {
      var endPoint;
      if (query.isSearch) {
        endPoint = '/api/views/allvppaccounts';
      } else throw ['unsupported query type', query];

      return AmViewDataSource.create({
        query: query,
        endPoint: endPoint
      });
    }
  });
});

