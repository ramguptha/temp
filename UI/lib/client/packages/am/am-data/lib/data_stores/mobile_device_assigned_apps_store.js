define([
  'ember',
  'query',
  '../am_view_data_store',
  '../am_view_data_source',
  '../specs/mobile_device_assigned_apps_spec',
  '../models/mobile_device_assigned_apps'
], function (
  Em,
  Query,
  AmViewDataStore,
  AmViewDataSource,
  MobileAssAppsSpec,
  MobileDeviceAssApps
  ) {
  'use strict';

  return AmViewDataStore.extend({
    Model: MobileDeviceAssApps,
    Spec: MobileAssAppsSpec,

    createDataSourceForQuery: function (query) {
      var endPoint;
      if (query.isSearch) {
        var context = query.get('context');
        if (context && context.mobileDeviceId) {
          endPoint = '/api/mobiledevices/' + context.mobileDeviceId + '/assigned/thirdpartyapplications';
        } else throw ['context required (mobileDeviceId)', query];
      } else throw ['unsupported query type', query];

      return AmViewDataSource.create({
        query: query,
        endPoint: endPoint
      });
    }
  });
});
