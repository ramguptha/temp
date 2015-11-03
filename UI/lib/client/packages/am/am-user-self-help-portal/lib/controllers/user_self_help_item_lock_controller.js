define([
  'ember',
  '../namespace',
  'desktop',
  'am-desktop',
  'am-data',
  'packages/am/am-user-formatter',

  './user_self_help_item_summary_list_controller',
  '../views/user_self_help_item_lock_view'
], function (
  Em,
  AmMobileDevice,
  Desktop,
  AmDesktop,
  AmData,
  AmUserFormatter,

  UserSelfHelpSummaryListController,
  UserSelfHelpLockView
  ) {
  'use strict';

  return AmDesktop.ModalActionController.extend({
    mobileDeviceListController: Em.inject.controller('AmUserSelfHelpDevice'),
    mobileDeviceItemController: Em.inject.controller('AmUserSelfHelpDeviceItem'),
    device: Em.computed.oneWay('model'),

    tHeaderMobile: 'amUserSelfServicePortal.modals.deviceLock.headingMobile'.tr(),
    tHeaderComputer: 'amUserSelfServicePortal.modals.deviceLock.headingComputer'.tr(),

    tActionButtonLabel: 'amUserSelfServicePortal.modals.deviceLock.buttons.actionButtonLabel'.tr(),

    tProgressMsgMobileDevice: 'amUserSelfServicePortal.modals.deviceLock.progressMsgMobileDevice'.tr(),
    tProgressMsgComputer: 'amUserSelfServicePortal.modals.deviceLock.progressMsgComputer'.tr(),

    tActionMsg1MobileDevice: 'amUserSelfServicePortal.modals.deviceLock.actionMsg1MobileDevice'.tr(),
    tActionMsg2MobileDevice: 'amUserSelfServicePortal.modals.deviceLock.actionMsg2MobileDevice'.tr(),
    tActionMsgOsxComputer: 'amUserSelfServicePortal.modals.deviceLock.actionMsgOsxComputer'.tr(),
    tActionAllOtherMsg: 'amUserSelfServicePortal.modals.deviceLock.actionAllOtherMsg'.tr(),

    tMaxMessageSize: 'amUserSelfServicePortal.modals.deviceLock.messageSizeNote'.tr('maxMessageSize'),

    tPasscodeErrorMessageMobile: 'amUserSelfServicePortal.shared.passcodeErrorMessageMobile'.tr(),
    tPasscodeErrorMessageComputer: 'amUserSelfServicePortal.shared.passcodeErrorMessageComputer'.tr(),
    tPasscodesDontMatchErrorMessageMobile: 'amUserSelfServicePortal.shared.passcodesDontMatchErrorMessageMobile'.tr(),
    tPasscodesDontMatchErrorMessageComputer: 'amUserSelfServicePortal.shared.passcodesDontMatchErrorMessageComputer'.tr(),

    successMsg: 'amUserSelfServicePortal.modals.deviceLock.successMsg'.tr(),
    errorMsg: 'amUserSelfServicePortal.modals.deviceLock.errorMsg'.tr(),

    promptForPasscode: null,
    passcodeErrorMessage: null,
    passcode: '',
    verifyPasscode: null,
    isActionBtnDisabled: false,
    isComputer: false,

    headingIconClass: "icon-passcode",
    addModalClass: "device-command-window",
    modalActionWindowClass: 'modal-action-window',

    actionDescription: null,
    loadInProgress: false,

    confirmationView: UserSelfHelpLockView,

    initProperties: function() {
      var self = this;
      var deviceId = this.get('device.id');

      this.setProperties({
        passcode: '',
        verifyPasscode: null,
        actionDescription: null,
        passcodeErrorMessage: null,
        promptForPasscode: null,
        message: null,
        phoneNumberIos: null
      });

      this.set('loadInProgress', true);

      this.get('mobileDeviceItemController').loadOneDevice(deviceId,
        // Complete loading
        function() {
          self.initAllProperties();
          self.set('loadInProgress', false);
        });
    },

    initAllProperties: function() {
      var device = this.get('device');
      var deviceType = device.get('data.deviceType');
      var deviceId = device.get('id');
      var isPasscodePresent = device.get('data.passcodePresent');
      var isComputer = device.get('data.isComputer');

      var promptForPasscode = false;
      var actionDescription = this.get('tActionAllOtherMsg');

      // 1=Mac OS X, 2=Windows, 10=iOS, 11=Android, 12=Windows Phone.

      // - iOS
      if (AmUserFormatter.isIOsDevice(deviceType)) {
        actionDescription = this.get('tActionMsg1MobileDevice');
      }

      // - Android devices with passcode
      if (AmUserFormatter.isAndroidDevice(deviceType) && isPasscodePresent ) {
        actionDescription = this.get('tActionMsg1MobileDevice');
      }

      // - Android devices that do not have a passcode are selected
      if (AmUserFormatter.isAndroidDevice(deviceType) && !isPasscodePresent ) {
        promptForPasscode = true;
        actionDescription = this.get('tActionMsg2MobileDevice');
      }

      // - Mac OS X
      if (AmUserFormatter.isMacDevice(deviceType, isComputer)) {
        actionDescription = this.get('tActionMsgOsxComputer');
        promptForPasscode = true;
      }

      this.setProperties({
        passcode: '',
        verifyPasscode: null,
        actionDescription: actionDescription,
        promptForPasscode: promptForPasscode,
        passcodeErrorMessage: null,
        message: null,
        phoneNumberIos: null,
        isComputer: isComputer,
        // In case of computer, button is disabled by default, but enabled for mobile devices
        isActionBtnDisabled: isComputer
      });
    },

    heading: function () {
      return this.get('isComputer') ?  this.get('tHeaderComputer') : this.get('tHeaderMobile');
    }.property('isComputer'),

    actionButtonLabel: function() {
      return this.get('tActionButtonLabel');
    }.property('device'),

    inProgressMsg: function() {
      return this.get('isComputer') ?  this.get('tProgressMsgComputer'): this.get('tProgressMsgMobileDevice');
    }.property('isComputer'),

    isIOsDevice: function () {
      var osPlatform = this.get('model.data.deviceType');
      return AmUserFormatter.isIOsDevice(osPlatform);
    }.property('model.data.deviceType'),

    onPasscodeChanged: function (router, event) {
      this.validateParams();
    }.observes('passcode'),

    onVerifyPasscodeChanged: function (router, event) {
      this.validateParams();
    }.observes('verifyPasscode'),

    maxMessageSize: function() {
      return 30000;
    }.property(),

    onMessageChanged: function(router, event) {
      this.validateParams();
    }.observes('message'),

    validateParams: function() {
      this.set('actionWarning', null);
      this.validateMessage();
      this.validatePassword();
    },

    validateMessage: function() {
      var message = this.get('message');
      var maxMessageSize = this.get('maxMessageSize');

      if (!Em.isEmpty(message) && message.length > maxMessageSize) {
        this.set('actionWarning', this.get('tMaxMessageSize'));
        this.set('message', message.substring(0, maxMessageSize));
      }
    },

    validatePassword: function() {
      var passcode = this.get('passcode'), verifyPasscode = this.get('verifyPasscode');
      var passCodeEmpty = passcode === null || passcode.length === 0;
      var passCodeMatches = passcode === verifyPasscode;
      var isComputer = this.get('isComputer');
      var passCodeLength = isComputer ? this.get('passcode').length === 6 : (this.get('passcode').length >= 4 && this.get('passcode').length <= 16);

      if(!passCodeEmpty) {
        if (!passCodeLength) {
          this.set('passcodeErrorMessage', isComputer ? this.get('tPasscodeErrorMessageComputer') : this.get('tPasscodeErrorMessageMobile'));
          this.set('isActionBtnDisabled', true);
        } else if (!passCodeMatches) {
          this.set('passcodeErrorMessage',  isComputer ? this.get('tPasscodesDontMatchErrorMessageComputer') : this.get('tPasscodesDontMatchErrorMessageMobile'));
          this.set('isActionBtnDisabled', true);
        } else {
          this.set('isActionBtnDisabled', false);
          this.set('passcodeErrorMessage', null);
        }
      } else {
        // In case of computer, passcode field is mandatory, can not be empty
        this.set('isActionBtnDisabled', isComputer);
        this.set('passcodeErrorMessage', null);
      }
    },

    buildAction: function () {
      var device = this.get('device');
      var isIOsDevice = device.get('data.deviceType');

      return AmData.get('actions.AmUserSelfHelpDeviceLockAction').create({
        mobileDeviceId: device.get('id'),
        isComputer: this.get('isComputer'),
        deviceType: device.get('data.deviceType'),
        passcode: this.get('passcode'),
        // Only for iOS
        phoneNumber:  AmUserFormatter.isIOsDevice(isIOsDevice) ? this.get('phoneNumberIos') : null,
        message: AmUserFormatter.isIOsDevice(isIOsDevice) ? this.get('message') : null
      });
    }

  });
});
