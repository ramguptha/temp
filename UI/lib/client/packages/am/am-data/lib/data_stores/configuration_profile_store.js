define([
  'ember',
  'query',
  '../am_view_data_store',
  '../am_view_data_source',
  '../specs/configuration_profile_spec',
  '../models/mobile_device_configuration_profile'
], function(
  Em,
  Query,
  AmViewDataStore,
  AmViewDataSource,
  MobileDeviceConfigProfileSpec,
  MobileDeviceConfigProfile
  ) {
  'use strict';

  return AmViewDataStore.extend({
    Model: MobileDeviceConfigProfile,
    Spec: MobileDeviceConfigProfileSpec,

     createDataSourceForQuery: function(query) {
      var endPoint;
      if (query.isSearch) {
          endPoint = '/api/configurationprofiles/views/All';
      } else throw ['unsupported query type', query];

      return AmViewDataSource.create({
        query: query,
        endPoint: endPoint,
        pushEndpoint: 'allconfigurationprofiles'
      });
    }
  });
});

