define([
  'ember',
  '../am_action'
], function(
  Em,
  AmAction
) {
  'use strict';

  return AmAction.extend({
    dependentDataStoreNames: [],

    description: 'Force Mobile Devices to send latest device data to the server',
    endPoint: 'commands/updatedeviceinfo',

    mobileDeviceIds: null,

    toJSON: function() {
      return {
        deviceIds: this.get('mobileDeviceIds').map(function(id) { return Number(id); })
      };
    }
  });
});
