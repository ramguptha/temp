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

    description: 'Clear passcodes for Mobile Devices',
    endPoint: 'commands/clearpasscode',

    iosDeviceIds: null,
    androidDeviceIds: null,
    newPasswordForAndroidDevices: null,

    toJSON: function() {
      var iosDeviceIds = this.get('iosDeviceIds');
      var androidDeviceIds = this.get('androidDeviceIds');
      var newPasswordForAndroidDevices = this.get('newPasswordForAndroidDevices');

      return {
        iOsIds: iosDeviceIds.map(function(id) { return Number(id); }),
        androidIds: androidDeviceIds.map(function(id) { return Number(id); }),
        passcode: Em.isEmpty(newPasswordForAndroidDevices) ? '' : newPasswordForAndroidDevices
      };
    }
  });
});
