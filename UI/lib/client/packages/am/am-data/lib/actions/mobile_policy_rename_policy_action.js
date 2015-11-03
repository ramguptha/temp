define([
  'ember',
  '../am_action'
], function(
  Em,
  AmAction
  ) {
  'use strict';

  return AmAction.extend({
    dependentDataStoreNames: ['mobilePolicyStore', 'mobilePolicyFromMobileDeviceStore', 'mobilePolicyFromContentStore'],

    description: 'Remove Mobile Policies',
    endPoint: null,

    policyId: null,
    newname: null,
    seed: null,

    toJSON: function () {
      var seed = this.get('seed');

      if (null == seed) {
        seed = 1;
      }

      return {
        name: this.get('newname'), seed: seed, id: this.get('policyId')
      };
    }
  });
});
