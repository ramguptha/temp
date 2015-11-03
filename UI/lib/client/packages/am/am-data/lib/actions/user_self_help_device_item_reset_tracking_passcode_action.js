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
    refreshDelay: 3000,

    description: 'User Self Help - Reset Tracking Passphrase',
    endPoint: 'ssp/commands/resettrackingpasscode',
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
