define([
  'ember',
  '../am_action'
], function(
  Em,
  AmAction
) {
  'use strict';

  return AmAction.extend({
    dependentDataStoreNames: 'contentStore contentFromMobilePolicyStore contentFromMobileDeviceStore'.w(),

    description: 'Batch Create Content',
    endPoint: 'content/batch',

    content: Em.A(),
    policyAssignments: Em.A(),

    toJSON: function() {
      return {
        newFiles: this.get('content'),
        assignToPolicies: this.get('policyAssignments')
      };
    }
  });
});
