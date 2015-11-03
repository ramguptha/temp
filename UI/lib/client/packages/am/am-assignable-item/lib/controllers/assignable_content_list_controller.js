define([
  'ember',
  'am-desktop',

  '../namespace',
  './assignable_list_base_controller'
], function (
  Em,
  AmDesktop,

  AmContent,
  AssignableListBaseController
) {
  'use strict';

  return AssignableListBaseController.extend({

    helpUri: 1032,
    selectionEnabled: true,
    hasRowClick: true,

    tHeader: 'amAssignableItem.assignableContentsPage.title'.tr(),

    tEditContent: 'amAssignableItem.assignableContentsPage.body.actionsMenu.options.editContent'.tr(),
    tDeleteContent: 'amAssignableItem.assignableContentsPage.body.actionsMenu.options.deleteContent'.tr(),

    path: 'am_assignable_list.content',
    titleResource: 'amAssignableItem.assignableContentsPage.title',

    userPrefsEndpointName: 'contentListColumns',

    dataStore: function () {
      return AmContent.get('store');
    }.property(),

    visibleColumnNames: 'type name mediaCategory mediaFileName mediaFileSize modified wifiOnly canLeaveAbsSafe canBeEmailed canBePrinted'.w(),

    // Perform specific formatting to specific columns
    createColumns: function(names) {
      var columns = this._super(names);

      columns.forEach(function(column) {
        // We need to show specific icons for content type
        if (column.get('name') === 'type') {
          column.set('valueComponent', 'am-formatted-type');
        }
      });

      return columns;
    },

    listActions: function () {
      return [{
        labelResource: 'amAssignableItem.assignableContentsPage.header.buttons.addContent',
        iconClassNames: 'plus-content icon-plus',
        actionName: 'newContent'
      }];
    }.property(),

    selectionActions: function () {
      var selectedItems = this.get('selections'), actions = Em.A();

      // Delete
      actions.push({
        name: this.get('tDeleteContent'),
        actionName: 'deleteContent',
        contextPath: 'selections',
        disabled: selectedItems === null || selectedItems.length === 0,
        iconClassNames: 'icon-trashcan'
      });

      // Edit
      actions.push({
        name: this.get('tEditContent'),
        actionName: 'editContentProperties',
        contextPath: 'selections',
        disabled: selectedItems.length !== 1,
        iconClassNames: 'icon-edit2'
      });

      return actions;
    }.property('selections.[]')
  });
});
