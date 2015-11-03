define([
  'ember',
  '../am_action'
], function(
  Em,
  AmAction
) {
  'use strict';

  return AmAction.extend({
    dependentDataStoreNames: 'actionsStore mobilePolicyFromActionStore actionsFromPolicyStore mobileDevicePerformedActionsStore'.w(),
    refreshDelay: 2500,

    description: 'Delete an action',

    verb: 'post',
    actionIds: Em.A(),

    endPoint: 'actions/delete',

    toJSON: function() {
      return this.get('actionIds');
    }
  });
});
