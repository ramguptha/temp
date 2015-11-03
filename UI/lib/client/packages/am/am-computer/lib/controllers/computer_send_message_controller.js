define([
  'ember',
  'help',
  'jquery',
  '../namespace',
  'desktop',
  'am-desktop',

  'am-data',
  './computer_summary_list_controller',
  '../views/computer_send_message_view'
], function(
  Em,
  Help,
  $,
  AmComputer,
  Desktop,
  AmDesktop,

  AmData,
  AmComputerSummaryListController,
  AmComputerSendMessageView
) {
  'use strict';

  return AmDesktop.ModalActionController.extend({
    computerListController: Em.inject.controller('AmComputerGroupsShowGroup'),

    tHeaderOneDevice: 'amComputer.modals.sendMessage.headingOneDevice'.tr('deviceName'),
    tHeaderManyDevices: 'amComputer.modals.sendMessage.headingManyDevices'.tr(),
    tUnsupportedDevicesMessage: 'amComputer.modals.sendMessage.unsupportedDevicesMessage'.tr('unsupportedDeviceCount', 'deviceCount'),
    tMaxMessageSize: 'amComputer.modals.sendMessage.messageSizeNote'.tr('maxMessageSize'),

    headingIconClass: "icon-message",
    actionButtonLabel: 'amComputer.modals.sendMessage.buttons.actionButtonLabel'.tr(),

    inProgressMsg: 'amComputer.modals.sendMessage.inProgressMsg'.tr(),
    successMsg: 'amComputer.modals.sendMessage.successMsg'.tr(),
    errorMsg: 'amComputer.modals.sendMessage.errorMsg'.tr(),

    message: null,

    confirmationView: AmComputerSendMessageView,

    isActionBtnDisabled: true,

    computers: null,
    deviceName: null,

    urlForHelp: null,

    heading: function () {
      return this.get('computers').length === 1 ? this.get('tHeaderOneDevice') : this.get('tHeaderManyDevices');
    }.property('computers.[]', 'deviceName'),

    deviceCountDetails: function() {
      var count = this.get('computers.length');
      return (count > 1) ? count : null;
    }.property('computers.[]'),

    initProperties: function()  {
      var computers = this.get('model');

      var computerListCtrl = this.get('computerListController');
      var unsupportedComputers = Em.A([]);
      var ids = '';
      for (var i=0; i < computers.length; i++) {
        ids += computers[i].get('id') + ', ';
        if (!computerListCtrl.supportsSendMessageCommands(computers[i])) {
          unsupportedComputers.pushObject(computers[i]);
        }
      }

      var modalActionWindowClass = this.get('modalActionWindowClass');

      var unsupportedComputersMessage = null;
      var unsupportedComputerListController = null;

      if (unsupportedComputers.length > 0) {
        this.setProperties({
          unsupportedDeviceCount: unsupportedComputers.length,
          deviceCount: computers.length
        });

        unsupportedComputersMessage = this.get('tUnsupportedDevicesMessage');

        unsupportedComputerListController = AmComputerSummaryListController.create({
          dataStore: AmData.get('stores.computerStore').createStaticDataStore(unsupportedComputers)
        });

        modalActionWindowClass += ' summary-list';
      }

      var deviceName = '';
      if (computers.length === 1) {
        // use jQuery to decode the name in case it has special characters. Do not change 'deviceName' to computerName
        var computer = computers[0];
        var agentName = computer.get('data.agentName');

        deviceName = $('<textarea />').html(agentName).val();
      }

      this.setProperties({
        deviceName: deviceName,
        computers: computers,

        message: null,
        urlForHelp: Help.uri(1052),

        modalActionWindowClass: modalActionWindowClass,

        unsupportedComputers: unsupportedComputers.length > 0,
        unsupportedComputersMessage: unsupportedComputersMessage,
        unsupportedComputerListController: unsupportedComputerListController
      });
    },

    onMessageChanged: function() {
      // The send Message Action Button is only enabled if a message is entered
      var message = this.get('message');
      this.set('isActionBtnDisabled', Em.isEmpty(message));

      var MAX_MESSAGE_SIZE = 30000;

      if (!Em.isEmpty(message) && message.length > MAX_MESSAGE_SIZE) {
        this.set('actionWarning', this.get('tMaxMessageSize'));
        this.set('message', message.substring(0, MAX_MESSAGE_SIZE));
      }

    }.observes('message'),

    buildAction: function() {
      this.set('urlForHelp', null);

      var computerListCtrl = this.get('computerListController');
      var computers = this.get('computers');

      return AmData.get('actions.AmComputerSendMessageAction').create({
        serialNumbers: computers
          .filter(function(computer) {
            return computerListCtrl.supportsSendMessageCommands(computer);
          })
          .mapBy('data.agentSerialNumber'),
        message: this.get('message')
      });
    }
  });
});
