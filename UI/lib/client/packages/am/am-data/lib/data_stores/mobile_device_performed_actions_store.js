define([
  'ember',
  'query',
  '../am_view_data_store',
  '../am_view_data_source',
  '../specs/mobile_device_performed_actions_spec',
  '../models/mobile_device_performed_actions'
], function (Em,
             Query,
             AmViewDataStore,
             AmViewDataSource,
             MobileDevicePerformedActionsSpec,
             MobileDevicePerformedActions) {
  'use strict';

  return AmViewDataStore.extend({
    Model: MobileDevicePerformedActions,
    Spec: MobileDevicePerformedActionsSpec,

    createDataSourceForQuery: function (query) {
      var endPoint;
      if (query.isSearch) {
        var context = query.get('context');
        if (context && context.mobileDeviceId) {
          endPoint = '/api/mobiledevices/' + context.mobileDeviceId + '/actions';
        } else throw ['context required (mobileDeviceId)', query];
      } else throw ['unsupported query type', query];

      return AmViewDataSource.create({
        query: query,
        endPoint: endPoint
      });
    }
  });
});

