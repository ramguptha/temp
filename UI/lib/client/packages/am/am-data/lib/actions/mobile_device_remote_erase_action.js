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

    description: 'Remotely erase Mobile Devices',
    endPoint: 'commands/remoteerase',

    iosDeviceIds: null,
    androidDeviceIds: null,
    includeSDCard: null,

    toJSON: function() {
      var iosDeviceIds = this.get('iosDeviceIds');
      var androidDeviceIds = this.get('androidDeviceIds');
      var includeSDCard = this.get('includeSDCard');

      return {
        iOsIds: iosDeviceIds.map(function(id) { return Number(id); }),
        androidIds: androidDeviceIds.map(function(id) { return Number(id); }),
        includeSDCard: !!includeSDCard
      };
    }
  });
});
