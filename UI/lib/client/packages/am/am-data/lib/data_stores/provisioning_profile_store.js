define([
  'ember',
  'query',
  '../am_view_data_store',
  '../am_view_data_source',
  '../specs/provisioning_profile_spec',
  '../models/mobile_device_provisioning_profile'
], function(
  Em,
  Query,
  AmViewDataStore,
  AmViewDataSource,
  MobileDeviceProvProfileSpec,
  MobileDeviceProvProfile
) {
  'use strict';

  return AmViewDataStore.extend({
    Model: MobileDeviceProvProfile,
    Spec: MobileDeviceProvProfileSpec,

    createDataSourceForQuery: function(query) {
      var endPoint, pushEndpoint;
      if (query.isSearch) {
        endPoint = '/api/provisioningprofiles/views/All';
        pushEndpoint = 'allprovisioningprofiles';
      } else throw ['unsupported query type', query];

      return AmViewDataSource.create({
        query: query,
        endPoint: endPoint,
        pushEndpoint: pushEndpoint
      });
    }
  });
});

