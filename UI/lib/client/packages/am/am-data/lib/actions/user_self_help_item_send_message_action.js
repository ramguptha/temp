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

    description: 'User Self Help - Send message to Devices',
    endPoint: 'ssp/commands/sendmessage',
    loginPathRedirectOnError: '../mylogin/?sessionTimedOut=true',

    deviceIdentifier: null,
    agentSerial: null,
    deviceType: null,
    withCancel: null,
    timeout: null,
    message: null,

    toJSON: function() {
      return {
        deviceIdentifier: this.get('deviceIdentifier'),
        agentSerial: this.get('agentSerial'),
        deviceType: this.get('deviceType'),
        withCancel: this.get('withCancel'),
        timeout: this.get('timeout'),
        message: this.get('message')
      };
    }
  });
});
