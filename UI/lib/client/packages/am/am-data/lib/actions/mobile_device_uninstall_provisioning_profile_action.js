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
    //dependentDataStoreNames: 'mobileDeviceInstalledProvisioningProfileStore'.w(),

    description: 'Uninstall Provisioning Profile on Mobile Devices',
    endPoint: 'commands/removeprovisioningprofile',

    mobileDeviceIds: null,
    provisioningProfileIds: null,

    toJSON: function() {
      return {
        // WARNING: this encapsulates a single value with an array
        deviceIds: [Number(this.get('mobileDeviceId'))],
        installedProvisioningProfileIds: this.get('provisioningProfileIds').map(function (id) { return Number(id); })
      };
    }
  });
});
