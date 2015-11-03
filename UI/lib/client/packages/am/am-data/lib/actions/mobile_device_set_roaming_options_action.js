define([
  'ember',
  '../am_action'
], function(
  Em,
  AmAction
) {
  'use strict';

  return AmAction.extend({
    dependentDataStoreNames: [],

    description: 'Set roaming options for Mobile Devices',
    endPoint: 'commands/setroamingoptions',

    mobileDeviceIds: null,
    voiceRoamingEnabled: null,
    dataRoamingEnabled: null,

    toJSON: function() {
      return {
        deviceIds: this.get('mobileDeviceIds').map(function(id) { return Number(id); }),
        voice: !!this.get('voiceRoamingEnabled'),
        data: !!this.get('dataRoamingEnabled')
      };
    }
  });
});
