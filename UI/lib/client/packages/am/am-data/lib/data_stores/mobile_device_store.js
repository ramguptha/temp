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
      var endPoint, pushEndpoint;

      if (query.isSearch) {
        var context = query.get('context');
        if (context && context.mobileDeviceListName) {
          endPoint = '/api/mobiledevices/views/' + context.mobileDeviceListName;

          if (context.mobileDeviceListName === 'all') {
            pushEndpoint = 'allmobiledevices';
          } else {
            pushEndpoint = context.mobileDeviceListName;
          }

        } else throw ['context required (mobileDeviceListName)', query];
      } else if (query.isSingleton) endPoint = '/api/mobiledevices';
      else throw ['unknown query type', query];

      return AmViewDataSource.create({
        query: query,
        endPoint: endPoint,
        pushEndpoint: pushEndpoint
      });
    }
  });
});
