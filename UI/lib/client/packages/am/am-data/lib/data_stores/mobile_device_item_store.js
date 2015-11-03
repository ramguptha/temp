define([
  'ember',
  '../am_view_data_store',
  '../am_view_data_source',
  '../specs/mobile_device_spec',
  '../models/mobile_device'
], function(
  Em,
  AmViewDataStore,
  AmViewDataSource,
  MobileDeviceSpec,
  MobileDevice
) {
  'use strict';

  return AmViewDataStore.extend({
    Model: MobileDevice,
    Spec: MobileDeviceSpec,

    createDataSourceForQuery: function(query) {
      var endPoint = '/api/mobiledevices';

      return AmViewDataSource.create({
        query: query,
        endPoint: endPoint
      });
    }
  });
});
