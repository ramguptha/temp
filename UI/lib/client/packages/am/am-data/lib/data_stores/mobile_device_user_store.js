define([
  'ember',
  'query',
  '../am_view_data_store',
  '../am_view_data_source',
  '../specs/mobile_device_user_spec',
  '../models/mobile_device_user'
], function (
  Em,
  Query,
  AmViewDataStore,
  AmViewDataSource,
  MobileDeviceUserSpec,
  MobileDeviceUser
  ) {
  'use strict';

  return AmViewDataStore.extend({
    Model: MobileDeviceUser,
    Spec: MobileDeviceUserSpec,

    createDataSourceForQuery: function (query) {
      var endPoint = '/api/mobiledevices/' + query.id + '/user';

      return AmViewDataSource.create({
        query: query,
        endPoint: endPoint,
        pathForUri: function (endPoint, query) {
          return endPoint;
        }
      });
    }
  });
});
