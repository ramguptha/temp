define([
  'ember',
  '../am_view_data_store',
  '../am_view_data_source',
  '../specs/user_self_help_device_list_spec',
  '../models/user_self_help_device_list'
], function(
  Em,
  AmViewDataStore,
  AmViewDataSource,
  UserSelfHelpSpec,
  UserSelfHelpModel
) {
  'use strict';

  return AmViewDataStore.extend({
    Model: UserSelfHelpModel,
    Spec: UserSelfHelpSpec,

    createDataSourceForQuery: function(query) {
      var endPoint = '/api/ssp/userdevices';

      query.setProperties({
        isSearch: false
      });

      return AmViewDataSource.create({
        query: query,
        endPoint: endPoint,
        loginPathRedirectOnError: '../mylogin/?sessionTimedOut=true'
      });
    }
  });
});
