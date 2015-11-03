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

    description: 'User Self Help - Track Device',
    endPoint: 'ssp/commands/lock',
    loginPathRedirectOnError: '../mylogin/?sessionTimedOut=true',

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

