define([
  'ember',
  '../am_action'
], function(
  Em,
  AmAction
) {
  'use strict';

  return AmAction.extend({
    dependentDataStoreNames: 'mobileDeviceFromMobilePolicyStore mobilePolicyFromMobileDeviceStore'.w(),

    description: 'Remove Relations between Mobile Device and Mobile Policy',
    endPoint: 'policy_mobiledevice/delete',

    mobileDeviceIds: null,
    mobilePolicyIds: null,

    toJSON: function() {
      var mobileDeviceIds = this.get('mobileDeviceIds');
      var mobilePolicyIds = this.get('mobilePolicyIds');

      var associations = new Array(mobileDeviceIds.length * mobilePolicyIds.length);
      for (var i = 0; i < mobileDeviceIds.length; i++) {
        for (var j = 0; j < mobilePolicyIds.length; j++) {
          associations[i * mobilePolicyIds.length + j] = {
            deviceId: Number(mobileDeviceIds[i]),
            policyId: Number(mobilePolicyIds[j])
          };
        }
      }
      return { associations: associations };
    }
  });
});
