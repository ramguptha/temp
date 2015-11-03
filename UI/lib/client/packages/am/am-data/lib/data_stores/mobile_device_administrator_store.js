define([
  'ember',
  'query',
  '../am_view_data_store',
  '../am_view_data_source',
  '../specs/mobile_device_administrator_spec',
  '../models/mobile_device_administrator'
], function(
  Em,
  Query,
  AmViewDataStore,
  AmViewDataSource,
  MobileDeviceAdministratorSpec,
  MobileDeviceAdministrator
  ) {
  'use strict';

  return AmViewDataStore.extend({
    Model: MobileDeviceAdministrator,
    Spec: MobileDeviceAdministratorSpec,

    createDataSourceForQuery: function(query) {
      var endPoint;
      if (query.isSearch) {
        var context = query.get('context');
        if (context && context.mobileDeviceId) {
          endPoint = '/api/mobiledevices/' + context.mobileDeviceId + '/administrators';
        } else throw ['context required (mobileDeviceId)', query];
      } else throw ['unsupported query type', query];

      return AmViewDataSource.create({
        query: query,
        endPoint: endPoint
      });
    }
  });
});
