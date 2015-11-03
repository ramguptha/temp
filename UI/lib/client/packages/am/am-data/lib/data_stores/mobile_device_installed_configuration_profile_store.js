define([
  'ember',
  'query',
  '../am_view_data_store',
  '../am_view_data_source',
  '../specs/mobile_device_installed_config_profile_spec',
  '../models/mobile_device_installed_configuration_profile'
], function(
  Em,
  Query,
  AmViewDataStore,
  AmViewDataSource,
  MobileDeviceInstalledConfigProfileSpec,
  MobileDeviceInstalledConfigProfile
  ) {
  'use strict';

  return AmViewDataStore.extend({
    Model: MobileDeviceInstalledConfigProfile,
    Spec: MobileDeviceInstalledConfigProfileSpec,

    createDataSourceForQuery: function(query) {
      var endPoint;
      if (query.isSearch) {
        var context = query.get('context');
        if (context && context.mobileDeviceId) {
          endPoint = '/api/mobiledevices/' + context.mobileDeviceId + '/configurationprofiles';
        } else throw ['context required (mobileDeviceId)', query];
      } else throw ['unsupported query type', query];

      return AmViewDataSource.create({
        query: query,
        endPoint: endPoint
      });
    }
  });
});

