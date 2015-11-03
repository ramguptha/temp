define([
  'ember',
  '../am_action'
], function(
  Em,
  AmAction
) {
  'use strict';

  return AmAction.extend({
    dependentDataStoreNames: ['mobilePolicyStore', 'mobilePolicyFromMobileDeviceStore', 'mobilePolicyFromContentStore', 'mobilePolicyFromActionStore'],

    description: 'Remove Mobile Policies',
    endPoint: 'policies/delete',

    policyIds: null,

    toJSON: function() {
      return {
          policyIds: this.get('policyIds').map(function (item) { return Number(item.id); })
      };
    }
  });
});
