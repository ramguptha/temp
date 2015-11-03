define([
  'ember',
  'query',
  '../am_view_data_store',
  '../am_view_data_source',
  '../specs/mobile_device_installed_provisioning_profile_spec',
  '../models/mobile_device_installed_provisioning_profile'
], function(
  Em,
  Query,
  AmViewDataStore,
  AmViewDataSource,
  MobileDeviceInstalledProvProfileSpec,
  MobileDeviceInstalledProvProfile
  ) {
  'use strict';

  return AmViewDataStore.extend({
      Model: MobileDeviceInstalledProvProfile,
      Spec: MobileDeviceInstalledProvProfileSpec,

    createDataSourceForQuery: function(query) {
      var endPoint;
      if (query.isSearch) {
        var context = query.get('context');
        if (context && context.mobileDeviceId) {
          endPoint = '/api/mobiledevices/' + context.mobileDeviceId + '/provisioningprofiles';
        } else throw ['context required (mobileDeviceId)', query];
      } else throw ['unsupported query type', query];

      return AmViewDataSource.create({
        query: query,
        endPoint: endPoint
      });
    }
  });
});

