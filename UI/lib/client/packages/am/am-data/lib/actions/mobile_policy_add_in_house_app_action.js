define([
  'ember',
  '../am_action'
], function (
  Em,
  AmAction
) {
  'use strict';

  return AmAction.extend({
    dependentDataStoreNames: 'inHouseApplicationFromPolicyStore'.w(),

    description: 'Add In-House App to Mobile Policies',
    endPoint: 'policy_inhouseapp',

    toJSON: function () {
      return {
        inHouseAppIds: this.get('inHouseAppIds'),
        policyAssignments: this.get('policyAssignments')
      };
    }
  });
});
