define([
  'ember',
  'help',
  'ui',
  'guid',
  'desktop',
  'am-desktop',
  'am-data',
  '../namespace'
], function (
  Em,
  Help,
  UI,
  Guid,
  Desktop,
  AmDesktop,
  AmData,
  AmMobileCommand
) {
  'use strict';

  return AmDesktop.ListControllerColumns.extend({

    tTitle: 'amMobileCommand.queuedCommandsListPage.title'.tr(),
    tDeleteCommand: 'amMobileCommand.queuedCommandsListPage.deleteCommand'.tr(),

    hasRowClick: true,
    selectionEnabled: true,

    urlForHelp: Help.uri(1018),

    userPrefsEndpointName: 'cmdQryListColumns',

    sort: Em.A([{
      attr: 'timeIssued',
      dir: 'desc'
    }]),

    name: function () {
      return this.get('tTitle').toString();
    }.property(),

    breadcrumb: UI.Breadcrumb.create({
      path: 'am_command_queue_mobile_devices',
      titleResource: 'amMobileCommand.queuedCommandsListPage.title'
    }),

    visibleColumnNames: 'command deviceName deviceModel osVersion status timeIssued'.w(),

    selectionActions: function () {
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
    }.property('selections.[]'),

    dataStore: function () {
      return AmMobileCommand.get('commandMobileQueueStore');
    }.property()

  });
});
