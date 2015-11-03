define([
  'ember',
  '../am_action'
], function(
  Em,
  AmAction
) {
  'use strict';

  return AmAction.extend({
    dependentDataStoreNames: 'mobilePolicyFromContentStore contentFromMobilePolicyStore contentFromMobileDeviceStore'.w(),
    refreshDelay: 3000,

    description: 'Add Content to Mobile Policies',
    endPoint: 'policy_content',

    contentIds: Em.A(),
    policyAssignments: Em.A(),

    toJSON: function() {
      return {
        contentIds: this.get('contentIds').map(function(id) { return Number(id); }),
        policyAssignments: this.get('policyAssignments')
      };
    }
  });
});
