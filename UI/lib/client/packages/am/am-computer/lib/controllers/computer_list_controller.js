define([
  'ember',
  'help',
  'ui',
  'desktop',
  'am-desktop',
  'env',
  'guid',
  '../namespace',
  'am-session',
  'am-computer-formatter',
  'am-data'
], function (Em,
             Help,
             UI,
             Desktop,
             AmDesktop,
             Env,
             Guid,
             AmComputer,
             AmSession,
             AmComputerFormatter,
             AmData) {
  'use strict';

  return AmDesktop.ListControllerColumns.extend(AmDesktop.SavesColumnWidth, {

    storageKey: 'am.Computer.ComputerList.',

    tNotAvailable: 'shared.baseline'.tr(),

    hasRowClick: true,
    selectionEnabled: true,
    isButtonGroupHorizontal: false,

    // Computer group
    id: null,
    name: Em.computed.oneWay('computerGroup.data.name'),

    computerGroupLock: Guid.generate(),
    computerGroup: null,

    urlForHelp: Help.uri(1051),

    visibleColumnNames: 'agentAvailability agentName machineModel osPlatform osVersion activeIpAddress currentUserName'.w(),

    userPrefsEndpointName: 'computerListColumns',

    dataStore: function () {
      return AmComputer.get('AmData.stores.computerStore');
    }.property(),

    sort: Em.A([{
      attr: 'agentName',
      dir: 'asc'
    }]),

    loadComputerList: function (id) {
      this.set('id', id);

      var groupStore = AmComputer.get('AmData.stores.computerGroupStore');

      this.set('searchQuery.context',
        { computerListName: groupStore.materializedObjects[id - 1].data.endPointName });

      // Load Computer Group info
      this.set('computerGroup', groupStore.acquireOne(this.get('computerGroupLock'), id, null, null, false, false));
    },

    breadcrumb: function () {
      return UI.Breadcrumb.create({
        path: 'am_computer_groups.show_group',

        titleResource: 'amComputer.computerListPage.title',
        controller: this,
        contextBinding: 'controller.id'
      });
    }.property(),

    // Perform specific formatting to specific columns
    createColumns: function (names) {
      var columns = this._super(names);

      columns.forEach(function (column) {
        var valueComponent;

        switch (column.get('name')) {
          case 'osPlatform':
            // We need to show specific icons for specific Operating Systems + their version in String
            valueComponent = 'am-computer-formatted-os-platform';
            break;
          case 'agentAvailability':
            valueComponent = 'am-agent-availability-icon-formatter';
            column.set('isSortable', false);
            break;
        }

        if (valueComponent) {
          column.set('valueComponent', valueComponent);
        }
      });

      return columns;
    },

    selectionActions: function () {
      var actions = Em.A(), context = {};

      var selectedItems = this.get('selections');
      if (selectedItems) {
        context = this.getSelectionActionContext(selectedItems, this.get('listRowData'));
      }

      /*    // TODO For next releases
       var length = this.get('selectionsList.length');
       for (var i = 0; i < length ; i++) {
       var device = this.get('selectionsList')[i];

       // Verify only for one item selected. If many selected, it will be an unsupported list in the dialog box
       if(length == 1) {
       deviceFreezeCommandSupported = this.supportsDeviceFreezeCommands(device);
       deviceUnfreezeCommandSupported = this.supportsDeviceUnfreezeCommands(device);
       dataDeleteCommandSupported = this.supportsDataDeleteCommands(device);
       }

       // NEEDS VERIFICATION TODO
       var freezeStatus = device.get('data.deviceFreezeStatusNumber');

       // Freeze command is enabled only if the current status is either idle(0), unfrozen by user(4),
       // unfrozen by admin(5), freeze error(6)
       if ( $.inArray(freezeStatus,[0,4,5,6]) !== -1) {
       deviceFreezeCommandSupported = false;
       dataDeleteCommandSupported = false;
       }
       // Unfreeze command is enabled only if the current status is either idle(0), frozen successfully(2),
       // unfreeze error(7)
       if ( $.inArray(freezeStatus,[0,2,7]) !== -1) {
       deviceUnfreezeCommandSupported = false;
       dataDeleteCommandSupported = false;
       }
       }*/

      // TODO add logic for multi-select
      var sendMessageCommandSupported = true,
        gatherInventoryCommandSupported = true,
        dataDeleteCommandSupported = false,
        deviceFreezeCommandSupported = false,
        deviceUnfreezeCommandSupported = false;

      if (sendMessageCommandSupported) {
        actions.pushObject(Em.Object.create({
          labelResource: 'amComputer.computerListPage.commands.sendMessage',
          actionName: 'sendMessageCommand',
          context: context
        }));
      }

      if (gatherInventoryCommandSupported) {
        actions.pushObject(Em.Object.create({
          labelResource: 'amComputer.computerListPage.commands.gatherInventory',
          actionName: 'gatherInventoryCommand',
          context: context
        }));
      }

      if (deviceFreezeCommandSupported) {
        actions.pushObject(Em.Object.create({
          labelResource: 'amComputer.computerListPage.commands.deviceFreeze',
          actionName: 'deviceFreezeCommand',
          context: context
        }));
      }

      if (deviceUnfreezeCommandSupported) {
        actions.pushObject(Em.Object.create({
          labelResource: 'amComputer.computerListPage.commands.deviceUnfreeze',
          actionName: 'deviceUnfreezeCommand',
          context: context
        }));
      }

      if (dataDeleteCommandSupported) {
        actions.pushObject(Em.Object.create({
          labelResource: 'amComputer.computerListPage.commands.dataDelete',
          actionName: 'dataDeleteCommand',
          context: context
        }));
      }

      return actions;
    }.property('selections.[]'),


    //Is command supported to send in final step, if not supported, display a summary list. TODO not implemented yet, next releases
    supportsSendMessageCommands: function (computer) {
      return true;
    },

    supportsGatherInventoryCommands: function (computer) {
      // TODO verify with unsupported computer
      return true;
    },

    supportsDeviceFreezeCommands: function (computer) {
      return !this.isMacPlatform(computer);
    },

    supportsDeviceUnfreezeCommands: function (computer) {
      return !this.isMacPlatform(computer);
    },

    supportsDataDeleteCommands: function (computer) {
      return !this.isMacPlatform(computer);
    },

    isMacPlatform: function (computer) {
      var osPlatform = computer.get('data.osPlatformNumber');
      return AmComputerFormatter.isMacPlatform(osPlatform);
    },

    isWinPlatform: function (computer) {
      var osPlatform = computer.get('data.osPlatformNumber');
      return AmComputerFormatter.isWinPlatform(osPlatform);
    }
  });
});
