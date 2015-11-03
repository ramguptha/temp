define([
  'ember',
  'jquery',
  '../namespace',
  'desktop',
  'am-desktop',
  'am-data'
], function(
  Em,
  $,
  AmComputer,
  Desktop,
  AmDesktop,
  AmData
) {
  'use strict';

  return AmDesktop.ModalActionController.extend({
    computerListController: Em.inject.controller('AmComputerGroupsShowGroup'),

    confirmationView: Desktop.ModalActionConfirmView,
    headingIconClass: "icon-checkmark3",
    actionDescription: 'The selected device will be unfrozen. Click Unfreeze to continue.',
    actionButtonLabel: "Unfreeze",
    isActionBtnDisabled: true,
    inProgressMsg: "Device Unfreezing...",
    successMsg: 'Device Unfreeze command has been requested.',
    errorMsg: 'Device Unfreeze command could not be requested.',

    initProperties: function()  {
      var computers = this.get('model');

      var modalActionWindowClass = this.get('modalActionWindowClass');

      this.setProperties({
        computers: computers,
        modalActionWindowClass: modalActionWindowClass
      });
    },

    buildAction: function() {
      var computerListCtrl = this.get('computerListController');
      var computers = this.get('computers');

      return AmData.get('actions.AmComputerDeviceUnfreezeAction').create({
        serialNumbers: computers
          .filter(function(computer) {
            return computerListCtrl.supportsDeviceUnfreezeCommands(computer);
          })
          .mapBy('agentSerialNumber')
      });
    }
  });
});
