define([
  'ember',
  '../namespace',
  'desktop',
  'am-desktop',
  'am-data',

  './user_self_help_item_summary_list_controller',
  '../views/user_self_help_item_reset_passcode_view'
], function (
  Em,
  AmMobileDevice,
  Desktop,
  AmDesktop,
  AmData,

  UserSelfHelpSummaryListController,
  UserSelfHelpResetPasscodeView
) {
  'use strict';

  return AmDesktop.ModalActionController.extend({
    mobileDeviceListController: Em.inject.controller('AmUserSelfHelpDevice'),
    device: Em.computed.oneWay('model'),

    heading: 'amUserSelfServicePortal.modals.resetTrackingPasscode.heading'.tr(),
    headingIconClass: "icon-passcode",
    addModalClass: "device-command-window",
    actionButtonLabel:'amUserSelfServicePortal.modals.resetTrackingPasscode.buttons.actionButtonLabel'.tr(),

    inProgressMsg: 'amUserSelfServicePortal.modals.resetTrackingPasscode.inProgressMsg'.tr(),
    successMsg: 'amUserSelfServicePortal.modals.resetTrackingPasscode.successMsg'.tr(),
    errorMsg: 'amUserSelfServicePortal.modals.resetTrackingPasscode.errorMsg'.tr(),

    tUnsupportedDevicesMessage: 'amUserSelfServicePortal.modals.resetTrackingPasscode.unsupportedDevicesMessage'.tr('unsupportedDeviceCount', 'deviceCount'),

    devices: null,
    androidIds: null,
    iOsIds: null,
    promptForPasscode: null,
    passcodeErrorMessage: null,

    unsupportedDevices: null,
    unsupportedDevicesMessage: null,

    passcode: '',
    verifyPasscode: null,

    unsupportedDeviceCount: null,
    deviceCount: null,

    confirmationView: UserSelfHelpResetPasscodeView,

    initProperties: function () {
      this.setProperties({
        isActionBtnDisabled: false
      });
    },

    deviceCountDetails: function() {
      return this.get('devices.length');
    }.property('devices.[]'),

    buildAction: function () {
      var device = this.get('devices');

      return AmData.get('actions.AmUserSelfHelpDeviceResetPasscodeAction').create({
        mobileDeviceId: device.get('id'),
        deviceType: device.get('data.deviceType'),
        passcode: this.get('passcode')
      });
    }

  });
});
