define([
  'ember',
  '../am_action'
], function(
  Em,
  AmAction
  ) {
  'use strict';

  return AmAction.extend({
    dependentDataStoreNames: ['mobileDeviceStore'],

    description: 'Set Activation Lock Options',
    endPoint: 'commands/setactivationlockoptions',

    mobileDeviceIds: null,
    activationLock: null,

    toJSON: function() {
      return {
        deviceIds : this.get('mobileDeviceIds').map(function (id) { return Number(id); }),
        activationLock   : this.get('activationLock')
      };
    }
  });
});
