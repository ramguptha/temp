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

    description: 'User Self Help - Lock Mobile Devices',
    endPoint: 'ssp/commands/lock',
    loginPathRedirectOnError: '../mylogin/?sessionTimedOut=true',

    mobileDeviceId: null,
    isComputer: null,
    deviceType: null,
    passcode: null,
    phoneNumber: null,
    message: null,

    toJSON: function() {
      var passcode = this.get('passcode');

      return {
        deviceIdentifier: !this.get('isComputer') ? this.get('mobileDeviceId') : null,
        agentSerial: this.get('isComputer') ? this.get('mobileDeviceId') : null,
        deviceType: this.get('deviceType'),
        passcode: Em.isEmpty(passcode) ? '' : passcode,
        phoneNumber: this.get('phoneNumber'),
        message: this.get('message')
      };
    }
  });
});

