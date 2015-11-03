define([
  'ember',
  '../am_view_data_store',
  '../am_view_data_source',
  '../specs/mobile_policy_from_mobile_device_spec',
  '../models/mobile_policy'
], function(
  Em,
  AmViewDataStore,
  AmViewDataSource,
  MobilePolicyFromMobileDeviceSpec,
  MobilePolicy
) {
  'use strict';

  return AmViewDataStore.extend({
    Model: MobilePolicy,
    Spec: MobilePolicyFromMobileDeviceSpec,

    createDataSourceForQuery: function(query) {
      var endPoint;
      if (query.isSearch) {
        var context = query.get('context');
        if (context && context.mobileDeviceId) {
          endPoint = '/api/mobiledevices/' + context.mobileDeviceId + '/policies';
        } else throw ['context required (mobileDeviceId)', query];
      } else throw ['unsupported query type', query];

      return AmViewDataSource.create({
        query: query,
        endPoint: endPoint
      });
    }
  });
});
