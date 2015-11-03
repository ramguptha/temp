define([
  'ember',
  'help',
  'ui',
  'desktop',
  'am-desktop',
  'env',
  'am-computer-formatter',

  'guid',
  'am-data',
  'packages/platform/storage',
  'query'
], function (Em,
             Help,
             UI,
             Desktop,
             AmDesktop,
             Env,
             AmComputerFormatter,
             Guid,
             AmData,
             Storage,
             Query) {
  'use strict';

  return AmDesktop.ListControllerColumns.extend(AmDesktop.SavesColumnWidth, {

    storageKey: 'am.Device.ComputerQueueCommandList.',

    tNotAvailable: 'shared.baseline'.tr(),
    tTitle: 'amMobileCommand.queuedCommandsListPage.title'.tr(),
    tDeleteCommand: 'amMobileCommand.queuedCommandsListPage.deleteCommand'.tr(),

    selectionEnabled: true,

    // Mobile command group id (computers or mobile devices)
    id: null,
    lock: Guid.generate(),

    urlForHelp: Help.uri(1018),

    sort: Em.A([{
      attr: 'scheduledTime',
      dir: 'desc'
    }]),

    breadcrumb: UI.Breadcrumb.create({
      path: 'am_command_queue_computers',
      titleResource: 'amMobileCommand.queuedCommandsListPage.title'
    }),

    name: function () {
      return this.get('tTitle').toString();
    }.property(),

    userPrefsEndpointName: 'cmdComputerQueueListColumns',

    visibleColumnNames: 'status commandName agentName administratorName scheduledTime'.w(),

    dataStore: function () {
      return AmData.get('stores.commandComputerQueueStore');
    }.property(),

    loadComputer: function (commandInfo, loadedCallback) {
      var valueArray = commandInfo.split('|');
      var commandId = parseInt(valueArray[0]);
      var computerId = parseInt(valueArray[1]);

      var lock = this.get('lock');

      var query = Query.Search.create({
        context: { computerId: computerId }
      });

      AmData.get('stores.computerItemAgentInfoStore').acquire(lock, query, function (data) {
        loadedCallback(data, commandId);
      });
    },

    // special formatting to display the type icon as an image
    createColumns: function (names) {
      var columns = this._super(names);

      columns.forEach(function (column) {
        if(column.get('name') === 'status') {
          column.set('valueComponent', 'am-computer-command-status-icon');
        }
      });

      return columns;
    },

    // Actions
    selectionActions: function () {
      return this.getActionList();
    }.property('selections.[]'),

    getActionList: function () {
      var selectedItems = this.get('selections'), actions = [];

      // Delete Command
      actions.push({
        name: this.get('tDeleteCommand'),
        actionName: 'deleteCommandQueue',
        contextPath: 'selections',
        disabled: selectedItems === null || selectedItems.length === 0,
        iconClassNames: 'icon-trashcan'
      });

      return actions;
    },

    // Helper
    isCommandTypeDeviceFreeze: function (commandType) {
      // Enumeration is in the database
      return commandType === 2888;
    },

    // Data Delete
    isCommandTypeDataDelete: function (commandType) {
      // Enumeration is in the database
      // Normal Data delete from History: 2889
      // In the Queue:
      // Transfer File/Folder: 2005
      // Delete File: 2007
      return commandType === 2889 ||
        commandType === 2005 ||
        commandType === 2007;
    }
  });
});
