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

    description: 'User Self Help - Remotely erase Mobile Devices',
    endPoint: 'ssp/commands/remoteerase',
    loginPathRedirectOnError: '../mylogin/?sessionTimedOut=true',

    mobileDeviceId: null,
    deviceType: null,
    passcode: null,
    includeSDCard: null,
    agentSerial: null,

    toJSON: function() {
      return {
        deviceIdentifier: this.get('deviceIdentifier'),
        agentSerial: this.get('agentSerial'),
        deviceType: this.get('deviceType'),
        passcode: this.get('passcode'),
        includeSDCard: this.get('includeSDCard')
      };
    }
  });
});
