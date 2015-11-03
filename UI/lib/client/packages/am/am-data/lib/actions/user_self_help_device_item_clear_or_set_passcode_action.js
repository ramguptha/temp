define([
  'ember',
  '../am_action'
], function(
  Em,
  AmAction
) {
  'use strict';

  return AmAction.extend({
    dependentDataStoreNames: 'userSelfHelpDeviceListStore'.w(),
    refreshDelay: 5000,

    description: 'User Self Help - Clear or Set passcode for Android and iOs only',
    endPoint: 'ssp/commands/clearpasscode',
    loginPathRedirectOnError: '../mylogin/?sessionTimedOut=true',

    mobileDeviceId: null,
    deviceType: null,
    passcode: null,

    toJSON: function() {
      var passcode = this.get('passcode');

      return {
        deviceIdentifier: this.get('mobileDeviceId'),
        deviceType: this.get('deviceType'),
        passcode: Em.isEmpty(passcode) ? '' : passcode
      };
    }
  });
});
