define([
  'ember',
  'query',
  '../am_view_data_store',
  '../am_view_data_source',
  '../specs/mobile_device_assigned_profiles_spec',
  '../models/mobile_device_assigned_profiles'
], function(
  Em,
  Query,
  AmViewDataStore,
  AmViewDataSource,
  MobileDeviceAssProfSpec,
  MobileDeviceAssProf
  ) {
  'use strict';

  return AmViewDataStore.extend({
      Model: MobileDeviceAssProf,
      Spec: MobileDeviceAssProfSpec,

    createDataSourceForQuery: function(query) {
      var endPoint;
      if (query.isSearch) {
        var context = query.get('context');
        if (context && context.mobileDeviceId) {
            endPoint = '/api/mobiledevices/' + context.mobileDeviceId + '/assigned/configurationprofiles';
        } else throw ['context required (mobileDeviceId)', query];
      } else throw ['unsupported query type', query];

      return AmViewDataSource.create({
        query: query,
        endPoint: endPoint
      });
    }
  });
});
