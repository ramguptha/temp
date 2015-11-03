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

    description: 'Retry All Failed Profiles',
    endPoint: 'commands/retryallfailedprofiles',

    mobileDeviceIds: null,

    toJSON: function() {
      return {
        deviceIds: this.get('mobileDeviceIds').map(function(id) { return Number(id); })
      };
    }
  });
});
