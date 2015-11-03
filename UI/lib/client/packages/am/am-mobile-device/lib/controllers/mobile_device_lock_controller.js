define([
  'ember',
  'help',
  '../namespace',
  'desktop',
  'am-desktop',
  'am-data',

  './mobile_device_summary_list_controller',
  '../views/mobile_device_lock_view'
], function (
  Em,
  Help,
  AmMobileDevice,
  Desktop,
  AmDesktop,
  AmData,

  MobileDeviceSummaryListController,
  MobileDeviceLockView
  ) {
  'use strict';

  return AmDesktop.ModalActionController.extend({

    tHeaderOneDevice: 'amMobileDevice.modals.deviceLock.headingOneDevice'.tr(),
    tHeaderManyDevices: 'amMobileDevice.modals.deviceLock.headingManyDevices'.tr(),
    tActionButtonLabel: 'amMobileDevice.modals.deviceLock.buttons.actionButtonLabel'.tr(),
    tProgressMsgOneDevice: 'amMobileDevice.modals.deviceLock.lockingOneDeviceProgressMsg'.tr(),
    tProgressMsgManyDevices: 'amMobileDevice.modals.deviceLock.lockingManyDevicesProgressMsg'.tr(),

    tActionMsg1: 'amMobileDevice.modals.deviceLock.actionMsg1'.tr(),
    tActionMsg1Singular: 'amMobileDevice.modals.deviceLock.actionMsg1Singular'.tr(),
    tActionMsg2: 'amMobileDevice.modals.deviceLock.actionMsg2'.tr(),
    tActionMsg2Singular: 'amMobileDevice.modals.deviceLock.actionMsg2Singular'.tr(),
    tActionMsg3: 'amMobileDevice.modals.deviceLock.actionMsg3'.tr(),
    tActionMsg4: 'amMobileDevice.modals.deviceLock.actionMsg4'.tr(),
    tActionMsg5: 'amMobileDevice.modals.deviceLock.actionMsg5'.tr(),
    tActionAllOtherMsg: 'amMobileDevice.modals.deviceLock.actionAllOtherMsg'.tr(),

    tPasscodeErrorMessage: 'amMobileDevice.shared.passcodeErrorMessage'.tr(),
    tPasscodesDontMatchErrorMessage: 'amMobileDevice.shared.passcodesDontMatchErrorMessage'.tr(),

    tUnsupportedDevicesMessage: 'amMobileDevice.modals.deviceLock.unsupportedDevicesMessage'.tr('unsupportedDeviceCount', 'deviceCount'),

    headingIconClass: "icon-passcode",
    addModalClass: "device-command-window",

    actionDescription: null,

    successMsg: 'amMobileDevice.modals.deviceLock.successMsg'.tr(),
    errorMsg: 'amMobileDevice.modals.deviceLock.errorMsg'.tr(),

    devices: Em.computed.oneWay('model'),

    promptForPasscode: null,
    passcodeErrorMessage: null,
    unsupportedDevices: null,
    unsupportedDevicesMessage: null,
    passcode: '',
    verifyPasscode: null,

    unsupportedDeviceCount: null,
    deviceCount: null,

    confirmationView: MobileDeviceLockView,

    unsupportedDeviceListController: null,

    mobileDeviceListController: Em.inject.controller('AmMobileDeviceGroupsShowGroup'),

    urlForHelp: null,

    initProperties: function(devices) {
      var deviceListCtrl = this.get('mobileDeviceListController');
      var unsupportedDeviceList = Em.A([]);
      var deviceIdsIOs = Em.A([]);
      var deviceIdsAndroidWithPasscode = Em.A([]);
      var deviceIdsAndroidNoPasscode = Em.A([]);
      var deviceIds = '';
      var supportedDevices = 0;

      for (var i = 0; i < devices.length; i++) {
        if (i > 0) { deviceIds += ','; }
        deviceIds += devices[i].get('id');

        if (deviceListCtrl.supportsIOSManagedCommands(devices[i])) {
          var id = devices[i].get('id');
          var isPasscodePresent = devices[i].get('data.isPasscodePresent');

          // 1=Mac OS X, 2=Windows, 10=iOS, 11=Android, 12=Windows Phone.
          if (devices[i].get('data.osPlatformEnum') == 10) {
            deviceIdsIOs.pushObject(id);
          }

          if (devices[i].get('data.osPlatformEnum') == 11 && isPasscodePresent ) {
            deviceIdsAndroidWithPasscode.pushObject(id);
          }

          if (devices[i].get('data.osPlatformEnum') == 11 && !isPasscodePresent ) {
            deviceIdsAndroidNoPasscode.pushObject(id);
          }
          supportedDevices++;
        } else {
          unsupportedDeviceList.pushObject(devices[i]);
        }
      }

      var promptForPasscode = false;

      var actionDescription = null;

      // - iOs devices are selected AND Android devices that have a passcode are selected
      // - iOs only OR Android devices that have a passcode are selected only
      if (deviceIdsIOs.length > 0 && deviceIdsAndroidWithPasscode.length > 0 && deviceIdsAndroidNoPasscode.length === 0 ||
          deviceIdsIOs.length > 0 && deviceIdsAndroidWithPasscode.length === 0 && deviceIdsAndroidNoPasscode.length === 0 ||
          deviceIdsAndroidWithPasscode.length > 0 && deviceIdsAndroidNoPasscode.length === 0 && deviceIdsIOs.length === 0) {
        if( supportedDevices === 1 ) {
          actionDescription = this.get('tActionMsg1Singular');
        } else {
          actionDescription = this.get('tActionMsg1');
        }

      // - Android devices that do not have a passcode are selected
      } else if (deviceIdsAndroidNoPasscode.length > 0 && deviceIdsAndroidWithPasscode.length === 0 && deviceIdsIOs.length === 0) {
        if( supportedDevices === 1 ) {
          actionDescription = this.get('tActionMsg2Singular');
        } else {
          actionDescription = this.get('tActionMsg2');
        }
        promptForPasscode = true;

      // - Android devices that do not have a passcode are selected AND
      //   Android devices with passcodes are selected
      } else if (deviceIdsAndroidNoPasscode.length > 0 && deviceIdsAndroidWithPasscode.length > 0 && deviceIdsIOs.length === 0) {
        actionDescription = this.get('tActionMsg3');
        promptForPasscode = true;

      // - Android devices that do not have passcodes are selected AND
      //   Android devices with passcodes are selected AND
      //   iOs devices that are selected
      } else if (deviceIdsAndroidNoPasscode.length > 0 && deviceIdsAndroidWithPasscode.length > 0 && deviceIdsIOs.length > 0) {
        actionDescription = Em.String.htmlSafe(this.get('tActionMsg4'));
        promptForPasscode = true;

      // - Android devices that do not have a passcode are selected AND iOs devices that are selected
      } else if (deviceIdsAndroidNoPasscode.length > 0 && deviceIdsAndroidWithPasscode.length === 0 && deviceIdsIOs.length > 0) {
        actionDescription = Em.String.htmlSafe(this.get('tActionMsg5'));
        promptForPasscode = true;

      // All other cases just in case of something unpredictable
      } else {
        actionDescription = this.get('tActionAllOtherMsg');
      }

      var modalActionWindowClass = 'modal-action-window';
      var unsupportedDevicesMessage = null;
      var unsupportedDeviceListController = null;

      if (unsupportedDeviceList.length > 0) {
        this.setProperties({
          unsupportedDeviceCount: unsupportedDeviceList.length,
          deviceCount: devices.length
        });

        unsupportedDevicesMessage = this.get('tUnsupportedDevicesMessage');

        unsupportedDeviceListController = MobileDeviceSummaryListController.create({
          dataStore: AmData.get('stores.mobileDeviceStore').createStaticDataStore(unsupportedDeviceList)
        });

        modalActionWindowClass += " summary-list";
      }

      this.setProperties({
        passcode: '',
        verifyPasscode: null,

        urlForHelp: Help.uri(1003),
        actionDescription: actionDescription,
        promptForPasscode: promptForPasscode,
        passcodeErrorMessage: null,
        unsupportedDevices: unsupportedDeviceList.length > 0,
        unsupportedDevicesMessage: unsupportedDevicesMessage,
        unsupportedDeviceListController: unsupportedDeviceListController,
        modalActionWindowClass: modalActionWindowClass
      });
    },

    heading: function () {
      var template;
      this.get('devices').length > 1 ? template = this.get('tHeaderManyDevices') : template = this.get('tHeaderOneDevice');
      return template;
    }.property('devices.[]'),

    actionButtonLabel: function() {
      return this.get('tActionButtonLabel');
    }.property('devices.[]'),

    inProgressMsg: function() {
      var template;
      this.get('devices').length > 1 ? template = this.get('tProgressMsgManyDevices') : template = this.get('tProgressMsgOneDevice');
      return template;
    }.property('devices.[]'),

    deviceCountDetails: function() {
      var count = this.get('devices.length');
      return count;
    }.property('devices.[]'),

    buildAction: function () {
      this.set('urlForHelp', null);
      var deviceListCtrl = this.get('mobileDeviceListController');

      return AmData.get('actions.AmMobileDeviceLockAction').create({
        mobileDeviceIds: this.get('devices')
          .filter(function (device) { return deviceListCtrl.supportsIOSManagedCommands(device); })
          .mapBy('id'),
        passcode: this.get('passcode')
      });
    },

    onPasscodeChanged: function (router, event) {
      var passcode = this.get('passcode'), verifyPasscode = this.get('verifyPasscode');
      var passCodeEmpty = passcode === null || passcode.length === 0,
        passCodeMatches = passcode === verifyPasscode,
        passCodeLength = this.get('passcode').length >= 4 && this.get('passcode').length <= 16;

      if(!passCodeEmpty) {
        if (!passCodeLength) {
          this.set('passcodeErrorMessage', this.get('tPasscodeErrorMessage'));
          this.set('isActionBtnDisabled', true);
        } else if (!passCodeMatches) {
          this.set('passcodeErrorMessage',  this.get('tPasscodesDontMatchErrorMessage'));
          this.set('isActionBtnDisabled', true);
        } else {
          this.set('isActionBtnDisabled', false);
          this.set('passcodeErrorMessage', null);
        }
      } else {
        this.set('isActionBtnDisabled', false);
        this.set('passcodeErrorMessage', null);
      }
    }.observes('passcode'),

    onVerifyPasscodeChanged: function (router, event) {
      this.onPasscodeChanged(router, event);
    }.observes('verifyPasscode')
  });
});
