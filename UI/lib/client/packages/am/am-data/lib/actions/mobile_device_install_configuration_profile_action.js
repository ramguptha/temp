define([
  'ember',
  '../am_action'
], function(
  Em,
  AmAction
) {
  'use strict';

  return AmAction.extend({
    // This is commented out because it can takes an indeterminate time for the profile to actually be installed,
    // so there is no point auto-refreshing
    //dependentDataStoreNames: 'mobileDeviceInstalledConfigProfileStore'.w(),

    description: 'Install Configuration Profile on Mobile Devices',
    endPoint: 'commands/installconfigurationprofile',

    mobileDeviceIds: null,
    configurationProfileIds: null,

    toJSON: function() {
      return {
        deviceIds: this.get('mobileDeviceIds').map(function(id) { return Number(id); }),
        configurationProfileIds: this.get('configurationProfileIds').map(function(id) { return Number(id); })
      };
    }
  });
});
