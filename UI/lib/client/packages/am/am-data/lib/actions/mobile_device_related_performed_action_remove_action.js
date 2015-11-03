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
    refreshDelay: 2500,

    description: 'Remove action from device',
    endPoint: 'commands/removeactionsfromdevices',

    deviceId: null,
    actionIds: null,

    toJSON: function() {
      return {
        actionHistoryIds: this.get('actionIds')
      };
    }
  });
});
