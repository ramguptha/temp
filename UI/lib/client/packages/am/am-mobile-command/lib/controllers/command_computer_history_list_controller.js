define([
  'ember',
  'help',
  'ui',
  'query',
  'desktop',
  'am-desktop',
  'env',
  'am-computer-formatter',

  'guid',
  'am-data'
], function (Em,
             Help,
             UI,
             Query,
             Desktop,
             AmDesktop,
             Env,
             AmComputerFormatter,
             Guid,
             AmData) {
  'use strict';

  return AmDesktop.ListControllerColumns.extend(AmDesktop.SavesColumnWidth, {

    storageKey: 'am.Device.ComputerHistoryCommandList.',

    tNotAvailable: 'shared.baseline'.tr(),
    tTitle: 'amMobileCommand.commandHistoryListPage.title'.tr(),
    tDeleteCommand: 'amMobileCommand.commandHistoryListPage.deleteCommand'.tr(),

    selectionEnabled: true,

    // Mobile command group id (computers or mobile devices)
    id: null,
    lock: Guid.generate(),

    urlForHelp: Help.uri(1019),

    sort: Em.A([{
      attr: 'startTime',
      dir: 'desc'
    }]),

    userPrefsEndpointName: 'cmdComputerHistoryListColumns',

    visibleColumnNames: 'status commandName agentName administratorName startTime commandError commandErrorInfo'.w(),

    breadcrumb: UI.Breadcrumb.create({
      path: 'am_command_history_computers',
      titleResource: 'amMobileCommand.commandHistoryListPage.title'
    }),

    name: function () {
      return this.get('tTitle').toString();
    }.property(),

    dataStore: function () {
      return AmData.get('stores.commandComputerHistoryStore');
    }.property(),

    loadComputer: function (commandInfo, loadedCallback) {
      var valueArray = commandInfo.split('|');
      var commandId = parseInt(valueArray[0]);
      var computerId = parseInt(valueArray[1]);

      var lock = this.get('lock');

      var query = Query.Search.create({
        context: {computerId: computerId}
      });

      AmData.get('stores.computerItemAgentInfoStore').acquire(lock, query, function (data) {
        loadedCallback(data, commandId);
      });
    },

    // special formatting to display the type icon as an image
    createColumns: function (names) {
      var columns = this._super(names);

      columns.forEach(function (column) {
        if( column.get('name') === 'status') {
          column.setProperties({
            valueComponent: 'am-computer-command-status-icon',
            isSortable: false
          });
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
        actionName: 'deleteCommandHistory',
        contextPath: 'selections',
        disabled: selectedItems === null || selectedItems.length === 0,
        iconClassNames: 'icon-trashcan'
      });

      return actions;
    },

    isCommandTypeDeviceFreeze: function (commandType) {
      // Enumeration is in the database
      return commandType === 2888;
    },

    // Data Delete
    isCommandTypeDataDelete: function (commandType) {
      // Enumeration is in the database
      return commandType === 2889;
    }
  });
});

