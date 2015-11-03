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
      var endPoint;
      if (query.isSearch) {
        var context = query.get('context');
        if (context && context.contentId) {
          endPoint = '/api/content/' + context.contentId + '/devices';
        } else throw ['context required (contentId)', query];
      } else throw ['unsupported query type', query];

      return AmViewDataSource.create({
        query: query,
        endPoint: endPoint
      });
    }
  });
});
