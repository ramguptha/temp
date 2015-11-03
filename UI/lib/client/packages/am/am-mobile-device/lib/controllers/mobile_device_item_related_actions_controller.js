define([
  'ember',
  'desktop',
  'guid',
  'am-desktop',
  '../namespace'
], function (
  Em,
  Desktop,
  Guid,
  AmDesktop,
  AmMobileDevice
) {
  'use strict';

  return AmDesktop.ListControllerColumns.extend({
    sort: Em.A([{
      attr: 'executionTime',
      dir: 'desc'
    }]),

    parentController: Em.inject.controller('amMobileDeviceItem'),

    tRemoveActionCommand: 'amMobileDevice.devicePage.performedActionsTab.commands.removeActionCommand'.tr(),
    tReapplyActionCommand: 'amMobileDevice.devicePage.performedActionsTab.commands.reapplyActionCommand'.tr(),

    relatedListTitle: 'amMobileDevice.devicePage.performedActionsTab.title'.tr(),

    selectionEnabled: true,
    hasRowClick: true,

    userPrefsEndpointName: 'actionRelatedListColumns',

    visibleColumnNames: 'name type executionTime policyName'.w(),

    selectionActions: function () {
      var selectedItems = this.get('selections'), actions = [], context = {};

      if (selectedItems) {
        context = this.getSelectionActionContext(selectedItems, this.get('listRowData'));
      }

      // Remove action
      actions.push({
        name: this.get('tRemoveActionCommand'),
        actionName: 'removeAction',
        contextPath: 'selections',
        disabled: selectedItems === null || selectedItems.length === 0,
        iconClassNames: 'icon-trashcan'
      });

      // Re-execute action
      actions.push({
        name: this.get('tReapplyActionCommand'),
        actionName: 'reapplyAction',
        context: context,
        disabled: selectedItems === null || selectedItems.length === 0,
        iconClassNames: 'icon-refresh'
      });

      return actions;
    }.property('selections.[]'),

    dataStore: function () {
      return AmMobileDevice.get('relatedActionsStore');
    }.property(),

    dataStoreContext: function () {
      return { mobileDeviceId: this.get('parentController.id') };
    }.property('parentController.id')
  });
});