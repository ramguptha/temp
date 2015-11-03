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

    description: 'Add Relations between Mobile Device and Mobile Policy',
    endPoint: 'policy_mobiledevice',

    mobileDeviceIds: null,
    mobilePolicyIds: null,

    toJSON: function() {
      return {
        deviceIds: this.get('mobileDeviceIds').map(function(id) { return Number(id); }),
        policyIds: this.get('mobilePolicyIds').map(function(id) { return Number(id); })
      };
    }
  });
});
