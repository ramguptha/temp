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

    description: 'Add policy/action assignment',
    endPoint: 'policy_actions',

    verb: 'post',

    content: null,

    toJSON: function() {
      return this.get('content');
    }
  });
});
