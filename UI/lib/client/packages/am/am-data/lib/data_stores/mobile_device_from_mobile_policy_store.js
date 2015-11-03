define([
  'ember',
  '../am_view_data_store',
  '../am_view_data_source',
  '../specs/mobile_device_from_mobile_policy_spec',
  '../models/mobile_device'
], function(
  Em,
  AmViewDataStore,
  AmViewDataSource,
  MobileDeviceFromMobilePolicySpec,
  MobileDevice
) {
  'use strict';

  return AmViewDataStore.extend({
    Model: MobileDevice,
    Spec: MobileDeviceFromMobilePolicySpec,

    createDataSourceForQuery: function(query) {
      var endPoint;
      if (query.isSearch) {
        var context = query.get('context');
        if (context && context.mobilePolicyId) {
          endPoint = '/api/policies/' + context.mobilePolicyId + '/devices';
        } else throw ['context required (mobilePolicyId)', query];
      } else throw ['unsupported query type', query];

      return AmViewDataSource.create({
        query: query,
        endPoint: endPoint
      });
    }
  });
});
