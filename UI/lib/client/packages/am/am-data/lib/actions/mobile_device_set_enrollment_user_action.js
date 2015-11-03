define([
  'ember',
  '../am_action'
], function (
  Em,
  AmAction
) {
  'use strict';

  return AmAction.extend({
    dependentDataStoreNames: ['mobileDeviceItemStore', 'mobileDeviceStore'],

    description: 'Set Enrollment User of Mobile Devices',
    endPoint: 'commands/setenrollmentuser',
    deviceIds: null,
    domain: null,
    username: null,

    toJSON: function () {
      var devs = this.deviceIds.map(function (id) { return Number(id); });
      var user = this.get('username');
      var domain = this.get('domain');

      return { deviceIds: devs, username: user, domain: domain };
    }
  });
});
