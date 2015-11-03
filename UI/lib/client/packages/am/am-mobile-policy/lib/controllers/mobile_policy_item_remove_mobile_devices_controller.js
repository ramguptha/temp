define([
  'ember',
  '../namespace',
  'desktop',
  'am-desktop',
  'am-data'
], function (Em,
             AmMobileDevice,
             Desktop,
             AmDesktop,
             AmData) {
  'use strict';

  return AmDesktop.ModalActionController.extend({
    tHeadingDevices: 'amMobilePolicies.modals.removeDevices.headingDevices'.tr(),

    heading: function () {
      return this.get('tHeadingDevices');
    }.property('devices.[]'),

    headingIconClass: "icon-square-attention1",

    actionWarning: 'amMobilePolicies.modals.removeDevices.actionWarning'.tr(),
    actionButtonLabel: 'amMobilePolicies.modals.removeDevices.buttons.removeDevices'.tr(),

    inProgressMsg: 'shared.modals.inProgressMessage'.tr(),
    successMsg: 'shared.modals.successMessage'.tr(),
    errorMsg: 'shared.modals.errorMessage'.tr(),

    confirmationView: Desktop.ModalActionConfirmView,

    initProperties: function () {
      var model = this.get('model');
      var devices = model.devices;
      var policyId = model.policyId;

      var deviceIds = '';
      for (var i = 0; i < devices.length; i++) {
        deviceIds += devices[i] + ', ';
      }

      this.setProperties({
        policyId: policyId,
        devices: devices
      });
    },

    buildAction: function () {
      var policyId = this.get('policyId'), devices = this.get('devices');

      return AmData.get('actions.AmMobilePolicyToMobileDeviceMapDeleteAction').create({
        mobileDeviceIds: devices,
        mobilePolicyIds: Em.A([policyId])
      });
    }
  });
});
