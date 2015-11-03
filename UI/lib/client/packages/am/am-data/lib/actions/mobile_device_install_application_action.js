define([
  'ember',
  '../am_action'
], function(
  Em,
  AmAction
) {
  'use strict';

  return AmAction.extend({
    // This is commented out because it can takes an indeterminate time for the app to actually be installed,
    // so there is no point auto-refreshing
    //dependentDataStoreNames: 'mobileDeviceInstalledAppStore'.w(),

    description: 'Install Application on Mobile Devices',
    endPoint: 'commands/installapplication',

    mobileDeviceIds: null,
    inHouseAppIds: null,
    thirdPartyAppIds: null,

    toJSON: function() {
      return {
        deviceIds: this.get('mobileDeviceIds').map(function(id) { return Number(id); }),
        inHouseAppIds: this.get('inHouseAppIds').map(function(id) { return Number(id); }),
        thirdPartyAppIds: this.get('thirdPartyAppIds').map(function(id) { return Number(id); })
      };
    }
  });
});
