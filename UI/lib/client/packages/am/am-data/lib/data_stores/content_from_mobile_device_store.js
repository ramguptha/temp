define([
  'ember',
  '../am_view_data_store',
  '../am_view_data_source',
  '../specs/content_from_mobile_device_spec',
  '../models/content_from_mobile_device'
], function(
  Em,
  AmViewDataStore,
  AmViewDataSource,
  ContentFromMobileDeviceSpec,
  ContentFromMobileDevice
) {
  'use strict';

  return AmViewDataStore.extend({
    Model: ContentFromMobileDevice,
    Spec: ContentFromMobileDeviceSpec,

    createDataSourceForQuery: function(query) {
      var endPoint;
      if (query.isSearch) {
        var context = query.get('context');
        if (context && context.mobileDeviceId) {
          endPoint = '/api/mobiledevices/' + context.mobileDeviceId + '/assigned/content';
        } else throw ['context required (mobileDeviceId)', query];
      } else throw ['unsupported query type', query];

      return AmViewDataSource.create({
        query: query,
        endPoint: endPoint
      });
    }
  });
});
