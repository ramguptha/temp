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
    //dependentDataStoreNames: 'mobileDeviceInstalledProvisioningProfileStore'.w(),

    description: 'Install Provisioning Profile on Mobile Devices',
    endPoint: 'commands/installprovisioningprofile',

    mobileDeviceIds: null,
    provisioningProfileIds: null,

    toJSON: function() {
      return {
        deviceIds: this.get('mobileDeviceIds').map(function(id) { return Number(id); }),
        provisioningProfileIds: this.get('provisioningProfileIds').map(function (id) { return Number(id); })
      };
    }
  });
});
