define([
  'ember',
  '../am_action'
], function (
  Em,
  AmAction
) {
  'use strict';

  return AmAction.extend({
    dependentDataStoreNames: ['mobileDeviceStore'],

    description: 'Set Ownership of Mobile Devices',
    endPoint: 'commands/setorganizationinfo',
    deviceIds: null,
    name: "",
    phone: "",
    email: "",
    address: "",
    custom: "",

    toJSON: function () {
      var devs = this.deviceIds.map(function (id) { return Number(id); });

      return {
        deviceIds: devs,
        name: this.get('name'),
        phone: this.get('phone'),
        email: this.get('email'),
        address: this.get('address'),
        custom: this.get('custom')

      };
    }
  });
});
