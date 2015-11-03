define([
  'radioButtonGroup',
  '../namespace',
  'desktop',
  'am-desktop',
  'am-data',

  './user_self_help_item_summary_list_controller',
  '../views/user_self_help_item_remote_erase_view'
], function(
  Em,
  AmMobileDevice,
  Desktop,
  AmDesktop,
  AmData,

  UserSelfHelpSummaryListController,
  UserSelfHelpRemoteEraseView
) {
  'use strict';

  return AmDesktop.ModalActionController.extend({
    mobileDeviceListController: Em.inject.controller('AmUserSelfHelpDevice'),
    device: Em.computed.oneWay('model'),

    heading: 'amUserSelfServicePortal.modals.remoteDataDelete.heading'.tr(),
    actionWarning: 'amUserSelfServicePortal.modals.remoteDataDelete.actionWarning'.tr(),
    actionButtonLabel: 'amUserSelfServicePortal.modals.remoteDataDelete.buttons.actionButtonLabel'.tr(),

    tActionMsgOsxComputer: 'amUserSelfServicePortal.modals.remoteDataDelete.actionMsgOsxComputer'.tr(),
    tActionMsgMobileDevices: 'amUserSelfServicePortal.modals.remoteDataDelete.actionMsgMobileDevices'.tr(),

    tPasscodeErrorMessageComputer: 'amUserSelfServicePortal.shared.passcodeErrorMessageComputer'.tr(),
    tPasscodesDontMatchErrorMessageComputer: 'amUserSelfServicePortal.shared.passcodesDontMatchErrorMessageComputer'.tr(),

    inProgressMsg: 'amUserSelfServicePortal.modals.remoteDataDelete.inProgressMsg'.tr(),
    successMsg: 'amUserSelfServicePortal.modals.remoteDataDelete.successMsg'.tr(),
    errorMsg: 'amUserSelfServicePortal.modals.remoteDataDelete.errorMsg'.tr(),
    errorDetailsMsg: 'amUserSelfServicePortal.modals.remoteDataDelete.errorDetailsMsg'.tr(),

    headingIconClass: 'icon-square-attention1',

    confirmationView: UserSelfHelpRemoteEraseView,

    passcode: '',
    verifyPasscode: null,
    includeSDCard: false,
    isActionBtnDisabled: false,
    isComputer: false,
    promptForEraseSDCard: null,

    initProperties: function()  {

      var actionDescription = this.get('device.data.isComputer') ? this.get('tActionMsgOsxComputer') : this.get('tActionMsgMobileDevices');

      var device = this.get('device');
      var isComputer = device.get('data.isComputer');

      this.setProperties({
        actionDescription: actionDescription,
        modalActionWindowClass: 'modal-action-window',
        includeSSDCard: false,
        passcode: '',
        verifyPasscode: null,
        isComputer: this.get('device.data.isComputer'),
        actionWarning: this.get('actionWarning'),
        promptForEraseSDCard: this.get('device.data.deviceType') === 11,
        eraseInternalBtnSelected: true,
        eraseSDCardBtnSelected: false,
        includeSDCard: false,
        // In case of computer, button is disabled by default, but enabled for mobile devices
        isActionBtnDisabled: isComputer
      });
    },

    tEraseInternal: 'amUserSelfServicePortal.modals.remoteDataDelete.deleteInternalStorageOnly'.tr(),
    tEraseInternalSDCard: 'amUserSelfServicePortal.modals.remoteDataDelete.deleteInternalStorageSDCard'.tr(),

    eraseOption: '0',
    eraseOptions: function() {
      return Em.A([
        {
          value: '0',
          label: this.get('tEraseInternal'),
          class: 'is-radio-checked-erase-internal'
        }, {
          value: '1',
          label: this.get('tEraseInternalSDCard'),
          class: 'is-radio-checked-erase-interal-sd'
        }
      ]);
    }.property(),

    onPasscodeChanged: function (router, event) {
      var passcode = this.get('passcode'), verifyPasscode = this.get('verifyPasscode');
      var passCodeEmpty = passcode === null || passcode.length === 0;
      var passCodeMatches = passcode === verifyPasscode;
      var isComputer = this.get('isComputer');
      var passCodeLength = isComputer ? this.get('passcode').length === 6 : (this.get('passcode').length >= 4 && this.get('passcode').length <= 16);

      if(!passCodeEmpty) {
        if (!passCodeLength) {
          this.set('passcodeErrorMessage', this.get('tPasscodeErrorMessageComputer'));
          this.set('isActionBtnDisabled', true);
        } else if (!passCodeMatches) {
          this.set('passcodeErrorMessage', this.get('tPasscodesDontMatchErrorMessageComputer'));
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
    }.observes('passcode'),

    onVerifyPasscodeChanged: function (router, event) {
      this.onPasscodeChanged(router, event);
    }.observes('verifyPasscode'),

    buildAction: function() {
      var isComputer = this.get('device.data.isComputer'), self = this;

      return AmData.get('actions.AmUserSelfHelpDeviceRemoteEraseAction').create({
        deviceIdentifier: !isComputer ? self.get('device.id') :  null,
        agentSerial: isComputer ? self.get('device.id') :  null,
        deviceType: self.get('device.data.deviceType'),
        passcode: self.get('passcode'),
        includeSDCard: self.get('eraseOption') === '1'
      });
    }
  });
});
