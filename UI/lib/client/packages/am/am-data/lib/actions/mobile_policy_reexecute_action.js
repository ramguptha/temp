define([
  'ember',
  '../am_action'
], function(
  Em,
  AmAction
  ) {
  'use strict';

  return AmAction.extend({
    dependentDataStoreNames: ['actionsFromPolicyStore', 'mobileDevicePerformedActionsStore'],
    verb: 'post',

    description: 'Reexecute an action on policy devices',
    endPoint: 'commands/executeactionsonpolicies',

    actionUuids: null,
    policyUuid: null,

    toJSON: function() {
      var mapping = [], policyUuid = this.get('policyUuid');

      this.get('actionUuids').forEach(function(uuid) {
        mapping.push({actionUuid: uuid, policyUuid: policyUuid});
      });

      return {
        policyUuidActionUuidMappings: mapping,
        executeImmediately: true
      }
    }
  });
});