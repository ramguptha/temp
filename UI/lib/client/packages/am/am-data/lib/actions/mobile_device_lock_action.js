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

    description: 'Lock Mobile Devices',
    endPoint: 'commands/lock',

    mobileDeviceIds: null,
    passcode: null,

    toJSON: function() {
      var passcode = this.get('passcode');

      return {
        deviceIds: this.get('mobileDeviceIds').map(function(id) { return Number(id); }),
        passcode: Em.isEmpty(passcode) ? '' : passcode
      };
    }
  });
});

