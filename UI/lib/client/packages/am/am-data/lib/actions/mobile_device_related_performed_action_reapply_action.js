define([
  'ember',
  '../am_action'
], function(
  Em,
  AmAction
) {
  'use strict';

  return AmAction.extend({
    dependentDataStoreNames: 'mobileDevicePerformedActionsStore'.w(),

    description: 'Remove action from device',
    endPoint: 'commands/executeactionsondevices',

    deviceId: null,
    actionIds: null,

    toJSON: function() {
      return {
        deviceIds: [this.get('deviceId')],
        actionUuids: this.get('actionIds'),
        executeImmediately: true
      };
    }
  });
});
