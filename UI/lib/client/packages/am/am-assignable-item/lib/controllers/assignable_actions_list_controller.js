define([
  'ember',
  'am-desktop',
  'locale',
  'help',

  '../namespace',
  'packages/am/am-assignable-item-foundation',

  './assignable_list_base_controller',
  './action_list_mixin'
], function (
  Em,
  AmDesktop,
  Locale,
  Help,

  AmAssignableItem,
  AmAssignableItemFoundation,

  AssignableListBaseController,
  getActionListMixin
) {
  'use strict';

  return AssignableListBaseController.extend(getActionListMixin, {

    selectionEnabled: true,
    hasRowClick: true,

    tHeader: 'amAssignableItem.assignableActionsPage.title'.tr(),

    tEditAction: 'amAssignableItem.assignableActionsPage.body.actionsMenu.options.editAction'.tr(),
    tDeleteAction: 'amAssignableItem.assignableActionsPage.body.actionsMenu.options.deleteAction'.tr(),
    tDuplicateAction: 'amAssignableItem.assignableActionsPage.body.actionsMenu.options.duplicateAction'.tr(),

    path: 'am_assignable_list.actions',
    titleResource: 'amAssignableItem.assignableActionsPage.title',
    urlForHelp: Help.uri(1055),

    userPrefsEndpointName: 'assignableActionsListColumns',

    dataStore: function () {
      return AmAssignableItem.get('assignedActionsStore');
    }.property(),

    visibleColumnNames: 'name lastModified type osPlatform description'.w(),

    listActions: function () {
      var children = this.getActionsList().map(function(action) {
        var isSeparator = action.type === 0;

        if (isSeparator) {
          return  {
            labelResource: action.labelResource,
            disabled: true
          };
        }

        return {
          labelResource: action.labelResource,
          actionName: action.actionName,
          context: {
            isEditMode: false,
            isDuplicateMode: false,

            typeEnum: action.type,
            osPlatformEnum: action.osPlatformEnum,
            view: action.view,
            label: Locale.renderGlobals(action.labelResource)
          }
        };
      });

      return [{
        labelResource: 'amAssignableItem.assignableActionsPage.header.buttons.addActionMenu.label',
        iconClassNames: 'plus-content icon-plus',
        children: children
      }];
    }.property(),

    selectionActions: function () {
      var selectedItems = this.get('selections'),
        data = null, action = null,
        actions = Em.A(),
        model = {};

      if (selectedItems && selectedItems.length === 1) {
        data = this.getSelectionActionContext(selectedItems, this.get('listRowData'))[0].get('data');
        action = this.getActionsList().findBy('type', data.typeEnum);

        model = {
          actionId: data.get('id'),
          name: data.get('name'),
          label: Locale.renderGlobals(action.labelResource),
          typeEnum: data.typeEnum,
          osPlatformEnum: data.osPlatformEnum,
          data: data,
          view: action.view
        };
      }

      var actionName = action ? action.actionName : '';

      // Delete
      actions.push({
        name: this.get('tDeleteAction'),
        actionName: 'deleteAction',
        contextPath: 'selections',
        disabled: selectedItems === null || selectedItems.length === 0,
        iconClassNames: 'icon-trashcan'
      });

      // Edit - Duplicate
      actions.push({
        name: this.get('tEditAction'),
        actionName: actionName,
        context: { model: model, isEditMode: true },
        disabled: selectedItems.length !== 1,
        iconClassNames: 'icon-edit2'
      },{
        name: this.get('tDuplicateAction'),
        actionName: actionName,
        context: { model: model, isEditMode: true, isDuplicateMode: true },
        disabled: selectedItems.length !== 1,
        iconClassNames: 'icon-duplicate'
      });

      return actions;
    }.property('selections.[]')
  });
});
