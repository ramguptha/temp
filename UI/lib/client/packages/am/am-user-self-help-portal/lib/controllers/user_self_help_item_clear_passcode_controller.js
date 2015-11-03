define([
  'ember',
  '../namespace',
  'desktop',
  'am-desktop',
  'am-data',
  'packages/am/am-user-formatter',

  '../views/user_self_help_clear_passcode_view'
], function (
  Em,
  AmMobileDevice,
  Desktop,
  AmDesktop,
  AmData,
  AmUserFormatter,

  UserSelfHelpClearPasscodeView
) {
  'use strict';

  return AmDesktop.ModalActionController.extend({
    heading: null,
    headingIconClass: "icon-passcode",
    addModalClass: "device-command-window",
    actionButtonLabel: null,
    device: Em.computed.oneWay('model'),

    inProgressMsg: 'amUserSelfServicePortal.modals.clearPasscode.inProgressMsg'.tr(),
    successMsg: 'amUserSelfServicePortal.modals.clearPasscode.successMsg'.tr(),

    errorMsg: 'amUserSelfServicePortal.modals.clearPasscode.errorMsg'.tr(),
    errorDetailsMsg: 'amUserSelfServicePortal.modals.clearPasscode.errorDetailsMsg'.tr(),

    // SetPasscode
    tActionButtonLabelSetPasscode: 'amUserSelfServicePortal.modals.setPasscode.actionButtonLabelSetPasscode'.tr(),
    tHeadingSetPasscode: 'amUserSelfServicePortal.modals.setPasscode.headingSetPasscode'.tr(),
    tInProgressMsgSetPasscode: 'amUserSelfServicePortal.modals.setPasscode.inProgressMsg'.tr(),
    tSuccessMsgSetPasscode: 'amUserSelfServicePortal.modals.setPasscode.successMsg'.tr(),
    tErrorMsgSetPasscode: 'amUserSelfServicePortal.modals.setPasscode.errorMsg'.tr(),
    tErrorDetailsMsgSetPasscode: 'amUserSelfServicePortal.modals.setPasscode.errorDetailsMsg'.tr(),

    // ClearPasscode
    tActionButtonLabelClearPasscode: 'amUserSelfServicePortal.modals.clearPasscode.actionButtonLabelClearPasscode'.tr(),
    tHeadingClearPasscode: 'amUserSelfServicePortal.modals.clearPasscode.headingClearPasscode'.tr(),
    tInProgressMsgClearPasscode: 'amUserSelfServicePortal.modals.clearPasscode.inProgressMsg'.tr(),
    tSuccessMsgClearPasscode: 'amUserSelfServicePortal.modals.clearPasscode.successMsg'.tr(),
    tErrorMsgClearPasscode: 'amUserSelfServicePortal.modals.clearPasscode.errorMsg'.tr(),
    tErrorDetailsMsgClearPasscode: 'amUserSelfServicePortal.modals.clearPasscode.errorDetailsMsg'.tr(),

    tPasscodeErrorMessage: 'amUserSelfServicePortal.shared.passcodeErrorMessageMobile'.tr(),
    tPasscodesDontMatchErrorMessage: 'amUserSelfServicePortal.shared.passcodesDontMatchErrorMessageMobile'.tr(),

    promptForPasscode: null,
    passcodeErrorMessage: null,

    passcode: '',
    verifyPasscode: null,

    confirmationView: UserSelfHelpClearPasscodeView,

    initProperties: function () {
      var device = this.get('device');
      var deviceType = device.get('data.deviceType');

      var actionButtonLabel = null;
      var heading = null;

      var promptForPasscode = false;

      // - Android devices
      if (AmUserFormatter.isAndroidDevice(deviceType)) {
        promptForPasscode = true;
      }

      if (promptForPasscode) {
        heading = this.get('tHeadingSetPasscode');
        actionButtonLabel = this.get('tActionButtonLabelSetPasscode');

        this.setProperties({
          inProgressMsg: this.get('tInProgressMsgSetPasscode'),
          successMsg: this.get('tSuccessMsgSetPasscode'),
          errorMsg: this.get('tErrorMsgSetPasscode'),
          errorDetailsMsg: this.get('tErrorDetailsMsgSetPasscode')
        });

      } else {
        heading = this.get('tHeadingClearPasscode');
        actionButtonLabel = this.get('tActionButtonLabelClearPasscode');

        this.setProperties({
          inProgressMsg: this.get('tInProgressMsgClearPasscode'),
          successMsg: this.get('tSuccessMsgClearPasscode'),
          errorMsg: this.get('tErrorMsgClearPasscode'),
          errorDetailsMsg: this.get('tErrorDetailsMsgClearPasscode')
        });

      }

      this.setProperties({
        heading: heading,

        actionButtonLabel: actionButtonLabel,

        passcode: '',
        verifyPasscode: '',
        promptForPasscode: promptForPasscode,
        passcodeErrorMessage: null,

        isActionBtnDisabled: false
      });
    },

    onPasscodeChanged: function (router, event) {
      if(this.get('promptForPasscode')) {
        var passcode = this.get('passcode');
        var passCodeMatches = passcode === this.get('verifyPasscode'),
          passCodeLength = passcode.length >= 4 && passcode.length <= 16,
          passCodeEmpty = passcode === null || passcode.length === 0;

        if(!passCodeEmpty) {
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
        } else {
          this.set('isActionBtnDisabled', false);
          this.set('passcodeErrorMessage', null);
        }
      }
    }.observes('passcode', 'verifyPasscode'),

    onVerifyPasscodeChanged: function (router, event) {
      this.onPasscodeChanged(router, event);
    }.observes('verifyPasscode'),

    buildAction: function () {
      var device = this.get('device');
      return AmData.get('actions.AmUserSelfHelpDeviceClearOrSetTrackingPasscodeAction').create({
        mobileDeviceId: device.get('id'),
        deviceType: device.get('data.deviceType'),
        passcode: this.get('passcode')
      });
    },

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
