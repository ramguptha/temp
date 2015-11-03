define([
  'ember',
  '../am_action'
], function(
  Em,
  AmAction
) {
  'use strict';

  return AmAction.extend({
    // This is commented out because it can takes an indeterminate time for the app to actually be uninstalled,
    // so there is no point auto-refreshing
    //dependentDataStoreNames: 'mobileDeviceInstalledAppStore'.w(),

    description: 'Uninstall Application on Mobile Devices',
    endPoint: 'commands/deleteapplication',

    mobileDeviceIds: null,
    applicationIds: null,

    toJSON: function() {
      return {
        deviceId: this.get('mobileDeviceId'),
        applicationIds: this.get('applicationIds').map(function(id) { return Number(id); })
      };
    }
  });
});
