define([
  'ember',
  'help',
  '../namespace',
  'desktop',
  'am-desktop',
  'am-data',

  './mobile_device_summary_list_controller',
  '../views/mobile_device_clear_passcode_view'
], function (
  Em,
  Help,
  AmMobileDevice,
  Desktop,
  AmDesktop,
  AmData,

  MobileDeviceSummaryListController,
  MobileDeviceClearPasscodeView
) {
  'use strict';

  return AmDesktop.ModalActionController.extend({
    heading: null,
    headingIconClass: "icon-passcode",
    addModalClass: "device-command-window",
    actionButtonLabel: null,

    inProgressMsg: null,
    successMsg: null,
    errorMsg: null,

    // Clear & Set Passcode
    tActionButtonLabelSetPasscode: 'amMobileDevice.modals.clearAndSetPasscode.actionButtonLabel'.tr(),
    tActionButtonLabelSetNewPasscodeEnabled: 'amMobileDevice.modals.clearAndSetPasscode.actionButtonLabelSetNewPasscodeEnabled'.tr(),
    tHeadingSetPasscode: 'amMobileDevice.modals.clearAndSetPasscode.heading'.tr(),
    tInProgressMsgSetPasscode: 'amMobileDevice.modals.clearAndSetPasscode.inProgressMsg'.tr(),
    tSuccessMsgSetPasscode: 'amMobileDevice.modals.clearAndSetPasscode.successMsg'.tr(),
    tErrorMsgSetPasscode: 'amMobileDevice.modals.clearAndSetPasscode.errorMsg'.tr(),
    tSetNewPasscodeCheckBoxTitleMixed: 'amMobileDevice.modals.clearAndSetPasscode.setNewPasscodeCheckBoxTitleMixed'.tr(),
    tSetNewPasscodeCheckBoxTitleAndroid: 'amMobileDevice.modals.clearAndSetPasscode.setNewPasscodeCheckBoxTitleAndroid'.tr(),

    // ClearPasscode
    tActionButtonLabelClearPasscode: 'amMobileDevice.modals.clearPasscode.actionButtonLabel'.tr(),
    tHeadingClearPasscode: 'amMobileDevice.modals.clearPasscode.heading'.tr(),
    tInProgressMsgClearPasscode: 'amMobileDevice.modals.clearPasscode.inProgressMsg'.tr(),
    tSuccessMsgClearPasscode: 'amMobileDevice.modals.clearPasscode.successMsg'.tr(),
    tErrorMsgClearPasscode: 'amMobileDevice.modals.clearPasscode.errorMsg'.tr(),
    tActionWarningiOsDevice: 'amMobileDevice.modals.clearPasscode.actionWarningiOsDevice'.tr(),
    tActionWarningiOsDeviceSingular: 'amMobileDevice.modals.clearPasscode.actionWarningiOsDeviceSingular'.tr(),

    // Password verification
    tPasscodeErrorMessage: 'amMobileDevice.shared.passcodeErrorMessage'.tr(),
    tPasscodesDontMatchErrorMessage: 'amMobileDevice.shared.passcodesDontMatchErrorMessage'.tr(),

    tUnsupportedDevicesMessage: 'amMobileDevice.modals.clearPasscode.unsupportedDevicesMessage'.tr('unsupportedDeviceCount', 'deviceCount'),

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

    actionWarning: null,
    actionDescription: null,

    newPasscodeEnabled: null,
    newPasscodeDisabled: null,

    urlForHelp: null,

    confirmationView: MobileDeviceClearPasscodeView,

    unsupportedDeviceListController: null,

    mobileDeviceListController: Em.inject.controller('AmMobileDeviceGroupsShowGroup'),

    initProperties: function () {
      var devices = this.get('model');

      var androidIds = Em.A([]);
      var iOsIds = Em.A([]);

      var deviceListCtrl = this.get('mobileDeviceListController');
      var unsupportedDeviceList = Em.A([]);

      // 1=Mac OS X, 2=Windows, 10=iOS, 11=Android, 12=Windows Phone.
      for (var i = 0; i < devices.length; i++) {
        if (deviceListCtrl.supportsIOSManagedCommands(devices[i])) {
          // 11=Android
          if (devices[i].get('data.osPlatformEnum') == 11) {
            androidIds.pushObject(devices[i].get('id'));
          } else if (devices[i].get('data.osPlatformEnum') == 10) {
            // 10=iOS
            iOsIds.pushObject(devices[i].get('id'));
          }
        } else {
          unsupportedDeviceList.pushObject(devices[i]);
        }
      }

      this.setProperties({
        androidIds: androidIds,
        iOsIds: iOsIds,
        newPasscodeEnabled: false,
        actionWarning: null,
        actionDescription: null
      });

      // Default state is: Clear passcode
      var heading = this.get('tHeadingClearPasscode');
      var actionButtonLabel = this.get('tActionButtonLabelClearPasscode');
      var urlForHelp = Help.uri(1004);

      var promptForPasscode = androidIds.length > 0;

      // Display additional warning for iOS devices
      var actionWarning = iOsIds.length === 1 ? this.get('tActionWarningiOsDeviceSingular') : this.get('tActionWarningiOsDevice');

      // Display prompt for passcode in case of Android devices or Multiple OS selection
      if (promptForPasscode) {
        heading = this.get('tHeadingSetPasscode');
        urlForHelp = Help.uri(1005);

        // For Android and Multiple selection: no warning
        actionWarning = null;
      }

      var modalActionWindowClass = this.get('modalActionWindowClass');
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
        heading: heading,
        urlForHelp: urlForHelp,

        modalActionWindowClass: modalActionWindowClass,
        actionButtonLabel: actionButtonLabel,

        androidIds: androidIds,
        iOsIds: iOsIds,
        actionWarning: actionWarning,

        devices: devices,
        passcode: '',
        verifyPasscode: '',
        promptForPasscode: promptForPasscode,
        passcodeErrorMessage: null,

        unsupportedDevices: unsupportedDeviceList.length > 0,
        unsupportedDevicesMessage: unsupportedDevicesMessage,
        unsupportedDeviceListController: unsupportedDeviceListController,
        isActionBtnDisabled: false
      });
    },

    isNewPasscodeDisabled: function () {
      return this.get('newPasscodeDisabled') ? 'disabled' : null;
    }.property('newPasscodeDisabled'),

    checkBoxTitle: function() {
      var androidDeviceCount = this.get('androidDeviceCount');
      var iOsDeviceCount = this.get('iOsDeviceCount');
      return androidDeviceCount > 0 && iOsDeviceCount > 0 ? this.get('tSetNewPasscodeCheckBoxTitleMixed') : this.get('tSetNewPasscodeCheckBoxTitleAndroid');
    }.property('androidDeviceCount'),

    deviceCountDetails: function() {
      return this.get('devices.length');
    }.property('devices.[]'),

    androidDeviceCount: function() {
      return this.get('androidIds.length');
    }.property('androidIds'),

    iOsDeviceCount: function() {
      return this.get('iOsIds.length');
    }.property('iOsIds'),

    buildAction: function () {
      return AmData.get('actions.AmMobileDeviceClearPasscodeAction').create({
        iosDeviceIds: this.get('iOsIds'),
        androidDeviceIds: this.get('androidIds'),
        newPasswordForAndroidDevices: this.get('passcode')
      });
    },

    onPasscodeChanged: function () {
      // If password fields are visible (In case of Android or Multi platform select)
      if(this.get('promptForPasscode')) {

        if(this.get('newPasscodeEnabled')) {
          // Verify password logic
          var passcode = this.get('passcode');
          var passCodeMatches = passcode === this.get('verifyPasscode'),
            passCodeLength = passcode.length >= 4 && passcode.length <= 16,
            passCodeEmpty = passcode === null || passcode.length === 0;

          if(passCodeEmpty) {
            this.set('isActionBtnDisabled', true);
            this.set('passcodeErrorMessage', null);
          } else {
            if (!passCodeLength) {
              this.set('passcodeErrorMessage', this.get('tPasscodeErrorMessage'));
              this.set('isActionBtnDisabled', true);
            } else if (!passCodeMatches) {
              this.set('passcodeErrorMessage', this.get('tPasscodesDontMatchErrorMessage'));
              this.set('isActionBtnDisabled', true);
            } else {
              this.set('isActionBtnDisabled', false);
              this.set('passcodeErrorMessage', null);
            }
          }
        } else {
          this.set('isActionBtnDisabled', false);
          this.set('passcodeErrorMessage', null);
        }
      }
    }.observes('passcode', 'verifyPasscode', 'newPasscodeEnabled'),

    onSetNewPasscodeEnabledChanged: function() {
      var isNewPasscodeEnabled = this.get('newPasscodeEnabled');

      this.set('newPasscodeDisabled', !isNewPasscodeEnabled)

      if(!isNewPasscodeEnabled) {
        this.setProperties({
          passcode: '',
          verifyPasscode: ''
        });
      }

      this.setProperties({
        actionButtonLabel: isNewPasscodeEnabled ? this.get('tActionButtonLabelSetNewPasscodeEnabled') : this.get('tActionButtonLabelSetPasscode'),
        inProgressMsg: isNewPasscodeEnabled ? this.get('tInProgressMsgSetPasscode') : this.get('tInProgressMsgClearPasscode'),
        successMsg: isNewPasscodeEnabled ? this.get('tSuccessMsgSetPasscode') : this.get('tSuccessMsgClearPasscode'),
        errorMsg: isNewPasscodeEnabled ? this.get('tErrorMsgSetPasscode') : this.get('tErrorMsgClearPasscode')
      });

    }.observes('newPasscodeEnabled'),

    onVerifyPasscodeChanged: function (router, event) {
      this.onPasscodeChanged(router, event);
    }.observes('verifyPasscode'),

    actions: {
      clearFields: function () {
        this.setProperties({
          passcode: '',
          verifyPasscode: ''
        });
      }
    }
  });
});
