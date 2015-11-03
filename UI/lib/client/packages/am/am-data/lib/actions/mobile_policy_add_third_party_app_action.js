define([
  'ember',
  '../am_action'
], function (
  Em,
  AmAction
) {
  'use strict';

  return AmAction.extend({
    dependentDataStoreNames: 'thirdPartyApplicationFromPolicyStore'.w(),

    description: 'Add In-House App to Mobile Policies',
    endPoint: 'policy_thirdpartyapp',

    toJSON: function () {
      return {
        thirdPartyAppIds: this.get('thirdPartyAppIds'),
        policyAssignments: this.get('policyAssignments')
      };
    }
  });
});
