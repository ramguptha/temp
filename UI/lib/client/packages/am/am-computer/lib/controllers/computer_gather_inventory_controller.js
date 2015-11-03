define([
  'ember',
  'help',
  'jquery',
  '../namespace',
  'desktop',
  'am-desktop',
  'am-data',

  './computer_summary_list_controller',
  '../views/computer_gather_inventory_view'
], function(
  Em,
  Help,
  $,
  AmComputer,
  Desktop,
  AmDesktop,

  AmData,
  AmComputerSummaryListController,
  AmComputerGatherInventoryView
) {
  'use strict';

  return AmDesktop.ModalActionController.extend({
    computerListController: Em.inject.controller('AmComputerGroupsShowGroup'),

    tHeaderOneDevice: 'amComputer.modals.gatherInventory.headingOneDevice'.tr('deviceName'),
    tHeaderManyDevices: 'amComputer.modals.gatherInventory.headingManyDevices'.tr(),
    tUnsupportedDevicesMessage: 'amComputer.modals.gatherInventory.unsupportedDevicesMessage'.tr('unsupportedDeviceCount', 'deviceCount'),

    tIncludeStartupItemInformationCheckbox: 'amComputer.modals.gatherInventory.includeStartupItemInformationCheckbox'.tr(),
    tIncludeServiceInformationCheckbox: 'amComputer.modals.gatherInventory.includeServiceInformationCheckbox'.tr(),

    tIncludeStartupItemInformationCheckboxOsxOnly: 'amComputer.modals.gatherInventory.includeStartupItemInformationCheckboxOsxOnly'.tr(),
    tIncludeServiceInformationCheckboxWindowsOnly: 'amComputer.modals.gatherInventory.includeServiceInformationCheckboxWindowsOnly'.tr(),

    actionDescription: 'amComputer.modals.gatherInventory.warningMessage'.tr(),

    headingIconClass: "icon-message",
    actionButtonLabel: 'amComputer.modals.gatherInventory.buttons.actionButtonLabel'.tr(),

    inProgressMsg: 'amComputer.modals.gatherInventory.inProgressMsg'.tr(),
    successMsg: 'amComputer.modals.gatherInventory.successMsg'.tr(),
    errorMsg: 'amComputer.modals.gatherInventory.errorMsg'.tr(),

    confirmationView: AmComputerGatherInventoryView,

    isActionBtnDisabled: false,
    forceFullInventoryEnabled: true,
    includeFontInformationEnabled: false,
    includePrinterInformationEnabled: false,
    includeStratupItemInformationEnabled: false,
    includeServiceInformationEnabled: false,

    computers: null,
    deviceName: null,

    macIds: null,
    winIds: null,

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

      var macIds = Em.A([]);
      var winIds = Em.A([]);

      var computerListCtrl = this.get('computerListController');
      var unsupportedComputers = Em.A([]);
      var ids = '';

      // 1=Mac OS X, 2=Windows, 10=iOS, 11=Android, 12=Windows Phone.
      for (var i=0; i < computers.length; i++) {
        if (computerListCtrl.supportsGatherInventoryCommands(computers[i])) {
          if (computerListCtrl.isMacPlatform(computers[i])) {
            macIds.pushObject(computers[i].get('id'));
          } else if (computerListCtrl.isWinPlatform(computers[i])) {
            winIds.pushObject(computers[i].get('id'));
          }
        } else {
          unsupportedComputers.pushObject(computers[i]);
        }

        ids += computers[i].get('id') + ', ';
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
        macIds: macIds,
        winIds: winIds,
        urlForHelp: Help.uri(1053),

        modalActionWindowClass: modalActionWindowClass,

        // Reset to default every time
        forceFullInventoryEnabled: true,
        includeFontInformationEnabled: false,
        includePrinterInformationEnabled: false,
        includeStratupItemInformationEnabled: false,
        includeServiceInformationEnabled: false,

        unsupportedComputers: unsupportedComputers.length > 0,
        unsupportedComputersMessage: unsupportedComputersMessage,
        unsupportedComputerListController: unsupportedComputerListController
      });
    },

    macDeviceCount: function() {
      var count = this.get('macIds.length');
      return count;
    }.property('macIds'),

    winDeviceCount: function() {
      var count = this.get('winIds.length');
      return count;
    }.property('winIds'),

    startupItemInformationCheckboxLabel: function() {
      var macCount = this.get('macIds.length');
      var winCount = this.get('winIds.length');
      return macCount > 0 && winCount > 0 ? this.get('tIncludeStartupItemInformationCheckboxOsxOnly') : this.get('tIncludeStartupItemInformationCheckbox');
    }.property('macIds', 'winIds'),

    serviceInformationCheckboxLabel: function() {
      var macCount = this.get('macIds.length');
      var winCount = this.get('winIds.length');
      return macCount > 0 && winCount > 0 ? this.get('tIncludeServiceInformationCheckboxWindowsOnly') : this.get('tIncludeServiceInformationCheckbox');
    }.property('macIds', 'winIds'),

    buildAction: function() {
      var computerListCtrl = this.get('computerListController');
      var computers = this.get('computers');
      this.set('urlForHelp', null);

      return AmData.get('actions.AmComputerGatherInventoryCommandsAction').create({
        serialNumbers: computers
          .filter(function(computer) {
            return computerListCtrl.supportsGatherInventoryCommands(computer);
          })
          .mapBy('data.agentSerialNumber'),
        forceFullInventoryEnabled: this.get('forceFullInventoryEnabled'),
        includeFontInformationEnabled: this.get('includeFontInformationEnabled'),
        includePrinterInformationEnabled: this.get('includePrinterInformationEnabled'),
        includeStratupItemInformationEnabled: this.get('includeStratupItemInformationEnabled'),
        includeServiceInformationEnabled: this.get('includeServiceInformationEnabled')
      });
    }
  });
});
