define([
  'ember',
  'query',
  '../am_view_data_store',
  '../am_view_data_source',
  '../specs/mobile_device_assigned_in_house_apps_spec',
  '../models/mobile_device_assigned_in_house_apps'
], function (
  Em,
  Query,
  AmViewDataStore,
  AmViewDataSource,
  MobileAssInHouseAppsSpec,
  MobileDeviceAssInHouseApps
  ) {
  'use strict';

  return AmViewDataStore.extend({
    Model: MobileDeviceAssInHouseApps,
    Spec: MobileAssInHouseAppsSpec,

    createDataSourceForQuery: function (query) {
      var endPoint;
      if (query.isSearch) {
        var context = query.get('context');
        if (context && context.mobileDeviceId) {
          endPoint = '/api/mobiledevices/' + context.mobileDeviceId + '/assigned/inhouseapplications';
        } else throw ['context required (mobileDeviceId)', query];
      } else throw ['unsupported query type', query];

      return AmViewDataSource.create({
        query: query,
        endPoint: endPoint
      });
    }
  });
});
