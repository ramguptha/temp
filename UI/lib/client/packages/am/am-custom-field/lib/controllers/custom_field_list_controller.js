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
  AmCustomField
) {
  'use strict';

  return AmDesktop.AmListController.extend({

    tTitle: 'amCustomField.customFieldListPage.title'.tr(),
    tDeleteCommand: 'amCustomField.customFieldListPage.commands.deleteCustomField'.tr(),
    tEditCommand: 'amCustomField.customFieldListPage.commands.editCustomField'.tr(),
    tDuplicateCommand: 'amCustomField.customFieldListPage.commands.duplicateCustomField'.tr(),

    selectionEnabled: true,
    selectOnRowClick: true,
    hasRowClick: true,

    paused: false,

    urlForHelp: Help.uri(1075),

    name: function () {
      return this.get('tTitle').toString();
    }.property(),

    breadcrumb: UI.Breadcrumb.create({
      path: 'am_custom_field_list',
      titleResource: 'amCustomField.topNavSpec.customFieldTitle'
    }),

    visibleColumnNames: 'name description dataType'.w(),

    selectionActions: function () {
      var selectedItems = this.get('selections'), actions = Em.A();

      // Delete Command
      actions.push({
        name: this.get('tDeleteCommand'),
        actionName: 'deleteCustomFieldAction',
        contextPath: 'selections',
        disabled: selectedItems === null || selectedItems.length === 0,
        iconClassNames: 'icon-trashcan tooltip-sw'
      });

      // Edit
      actions.push({
        name: this.get('tEditCommand'),
        actionName: 'editCustomFieldAction',
        contextPath: 'selections',
        disabled: selectedItems.length !== 1,
        iconClassNames: 'icon-edit2'
      });

      // Duplicate
      actions.push({
        name: this.get('tDuplicateCommand'),
        actionName: 'duplicateCustomFieldAction',
        contextPath: 'selections',
        disabled: selectedItems.length !== 1,
        iconClassNames: 'icon-duplicate'
      });

      return actions;
    }.property('selections.[]'),

    listActions: function () {
      return [{
        labelResource: 'amCustomField.customFieldListPage.commands.addNewCustomField',
        actionName: 'addCustomFieldAction',
        contextPath: 'selections',
        iconClassNames: 'plus-content icon-plus'
      }];
    }.property(),

    dataStore: function () {
      return AmCustomField.get('customFieldStore');
    }.property()
  });
});

