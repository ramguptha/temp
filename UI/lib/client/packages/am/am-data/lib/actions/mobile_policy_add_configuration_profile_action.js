define([
  'ember',
  '../am_action'
], function (
  Em,
  AmAction
) {
  'use strict';

  return AmAction.extend({
    dependentDataStoreNames: 'configurationProfileFromPolicyStore'.w(),
    refreshDelay: 3000,

    description: 'Add Configuration Profile on Mobile Policies',
    endPoint: 'policy_configurationprofile',

    toJSON: function () {
      return {
        configurationProfileIds: this.get('configurationProfileIds'),
        policyAssignments: this.get('policyAssignments')
      };
    }
  });
});
