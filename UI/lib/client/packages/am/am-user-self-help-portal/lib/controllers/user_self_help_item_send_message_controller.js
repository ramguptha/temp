define([
  'ember',
  'jquery',
  '../namespace',
  'desktop',
  'am-desktop',
  'am-data',
  'packages/am/am-user-formatter',

  './user_self_help_item_summary_list_controller',
  '../views/user_self_help_item_send_message_view'
], function(
  Em,
  $,
  AmMobileDevice,
  Desktop,
  AmDesktop,
  AmData,
  AmUserFormatter,

  UserSelfHelpSummaryListController,
  UserSelfHelpSendMessageView
) {
  'use strict';

  return AmDesktop.ModalActionController.extend({
    mobileDeviceListController: Em.inject.controller('AmUserSelfHelpDevice'),
    device: Em.computed.oneWay('model'),

    tHeader: 'amUserSelfServicePortal.modals.sendMessage.heading'.tr(),
    tMaxMessageSize: 'amUserSelfServicePortal.modals.sendMessage.messageSizeNote'.tr('maxMessageSize'),
    tMessageWrongTime: 'amUserSelfServicePortal.modals.sendMessage.messageWrongTime'.tr(),

    headingIconClass: 'icon-message',
    addModalClass: "device-command-window",

    actionButtonLabel: 'amUserSelfServicePortal.modals.sendMessage.buttons.actionButtonLabel'.tr(),

    inProgressMsg: 'amUserSelfServicePortal.modals.sendMessage.inProgressMsg'.tr(),
    successMsg: 'amUserSelfServicePortal.modals.sendMessage.successMsg'.tr(),
    errorMsg: 'amUserSelfServicePortal.modals.sendMessage.errorMsg'.tr(),
    errorDetailsMsg: 'amUserSelfServicePortal.modals.sendMessage.errorDetailsMsg'.tr(),

    isActionBtnDisabled: true,

    confirmationView: UserSelfHelpSendMessageView,

    initProperties: function()  {
      var isComputer = this.get('device.data.isComputer');

      this.setProperties({
        modalActionWindowClass: 'modal-action-window',
        isActionBtnDisabled: true,
        message: null,
        timeOutEnabled: false,
        timeOutBoxDisabled: true,
        timeOut: '00:00',
        timeOutInteger: 0,
        addCancelEnabled: false,
        actionWarning: null,
        isComputer: isComputer,
        isMacDevice: AmUserFormatter.isMacDevice(this.get('device.data.deviceType'), isComputer),
        displayOptions: false
      });
    },

    heading: function () {
      return this.get('tHeader');
    }.property('deviceCountDetails'),

    maxMessageSize: function() {
      return 30000;
    }.property(),

    onMessageChanged: function(router, event) {
      this.validateParams();
    }.observes('message'),

    onTimeOutEnabledChanged: function () {
      // enabledBinding does not work from a template
      // custom property (timeOutBoxDisabled) does not work as well
      // So, use binding to variable timeOutBoxDisabled from a template (Ember bug or feature)
      this.set('timeOutBoxDisabled', !this.get('timeOutEnabled'))

      if(!this.get('timeOutEnabled')) {
        this.set('timeOut', '00:00')
        this.validateParams();
      }
    }.observes('timeOutEnabled'),

    onTimeOutChanged: function () {
      this.validateParams();
    }.observes('timeOut'),

    validateParams: function() {
      this.set('actionWarning', null);
      this.set('isActionBtnDisabled', true);
      this.validateTimeOut();
      this.validateMessage();
    },

    validateTimeOut: function() {
      var timeOut = this.get('timeOut');
      if(timeOut && timeOut !== '00:00') {
        this.tryConvertTime(timeOut);
      }
    },

    validateMessage: function() {
      // The send Message Action Button is only enabled if a message is entered
      var message = this.get('message');
      this.set('isActionBtnDisabled', Em.isEmpty(message));

      var maxMessageSize = this.get('maxMessageSize');

      if (!Em.isEmpty(message) && message.length > maxMessageSize) {
        this.set('actionWarning', this.get('tMaxMessageSize'));
        this.set('message', message.substring(0, maxMessageSize));
      }
    },

    // TODO will be removed with TimePicker control later
    tryConvertTime: function(timeString) {
      var intVal = 0;
      try {
        var splitArray = timeString.split(':');
        var minutes = parseInt(splitArray[0]);
        var seconds = parseInt(splitArray[1]);

        intVal = 60 * minutes + seconds;

        if(isNaN(intVal) || splitArray.length !== 2 || intVal === 0) {
          this.set('actionWarning', this.get('tMessageWrongTime'));
          this.set('isActionBtnDisabled', true);
        } else {
          // Clear warning
          this.set('actionWarning', null);
          this.set('isActionBtnDisabled', false);
        }
      } catch (e) {
        this.set('actionWarning', this.get('tMessageWrongTime'));
        this.set('isActionBtnDisabled', true);
      }
      this.set('timeOutInteger', intVal);
    },

    buildAction: function() {
      var isComputer = this.get('device.data.isComputer');
      return AmData.get('actions.AmUserSelfHelpDeviceSendMessageAction').create({
        deviceIdentifier: !isComputer ? this.get('device.id') :  null,
        agentSerial: isComputer ? this.get('device.id') :  null,
        deviceType: this.get('device.data.deviceType'),
        withCancel: this.get('addCancelEnabled'),
        timeout: this.get('timeOutEnabled') ?  this.get('timeOutInteger') : null,
        message: this.get('message')
      });
    }
  });
});
