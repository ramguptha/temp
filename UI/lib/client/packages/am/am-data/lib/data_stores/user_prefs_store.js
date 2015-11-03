define([
  'ember',
  'query',
  '../am_view_data_store',
  '../am_view_data_source',
  '../specs/user_prefs_spec',
  '../models/user_prefs',
  'packages/platform/data/lib/single_result_proxy'
], function (
  Em,
  Query,
  AmViewDataStore,
  AmViewDataSource,
  UserPrefsSpec,
  UserPrefs,
  SingleResultProxy
) {
  'use strict';

  return AmViewDataStore.extend({
    Model: UserPrefs,
    Spec: UserPrefsSpec,

    createDataSourceForQuery: function (query) {
      return AmViewDataSource.create({
        query: query,
        endPoint: '/api/user/prefs'
      });
    }
  });
});
