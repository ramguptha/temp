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

    description: 'Send message to Mobile Devices',
    endPoint: 'commands/sendmessage',

    mobileDeviceIds: null,
    message: null,

    toJSON: function() {
      return {
        deviceIds: this.get('mobileDeviceIds').map(function(id) { return Number(id); }),
        message: this.get('message')
      };
    }
  });
});
