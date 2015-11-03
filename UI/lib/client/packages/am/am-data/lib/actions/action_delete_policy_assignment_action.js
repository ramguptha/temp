define([
  'ember',
  '../am_action'
], function(
  Em,
  AmAction
) {
  'use strict';

  return AmAction.extend({
    dependentDataStoreNames: 'mobilePolicyFromActionStore actionsFromPolicyStore mobileDevicePerformedActionsStore'.w(),

    description: 'Remove a policy assignment from an action',

    verb: 'post',
    content: null,

    endPoint: 'policy_actions/delete',

    toJSON: function() {
      return this.get('content');
    }
  });
});
