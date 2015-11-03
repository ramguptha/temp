define([
  'ember',
  'guid',
  'query',
  'desktop',
  'am-desktop',

  '../namespace'
], function(
  Em,
  Guid,
  Query,
  Desktop,
  AmDesktop,

  AmAction
) {
  'use strict';

  return AmDesktop.ListControllerColumns.extend(Desktop.ChildController, {
    relatedListTitle: 'amAssignableItem.assignableActionsDetailsPage.tabPolicies.header'.tr(),

    tEditPolicyAssignmentProperties: 'amAssignableItem.assignableActionsDetailsPage.tabPolicies.actionsMenu.options.editPolicyAssignmentProperties'.tr(),
    tRemoveActionFromPolicies: 'amAssignableItem.assignableActionsDetailsPage.tabPolicies.actionsMenu.options.removeActionFromPolicies'.tr(),

    visibleColumnNames: 'name initialDelay repeatInterval repeatCount'.w(),

    userPrefsEndpointName: 'policyRelatedToActionListColumns',

    selectionEnabled: true,
    hasRowClick: true,

    dataStore: function() {
      return AmAction.get('relatedMobilePoliciesFromActionStore');
    }.property(),

    dataStoreContext: function() {
      return { actionId: this.get('parentController.id') };
    }.property('parentController.id'),

    listActions: function() {
      return [{
        labelResource: 'amAssignableItem.assignableActionsDetailsPage.tabPolicies.buttons.addActionToPolicies',
        iconClassNames: 'plus-content icon-plus',
        actionName: 'gotoAddActionToPolicies'
      }];
    }.property(),

    selectionActions: function() {
      var selectedItems = this.get('selections'), actions = Em.A(), context = {};

      // Prepare the context for editing policy assignment properties
      if (!Em.isEmpty(selectedItems)) {
        var selectedPoliciesData = this.getSelectionActionContext(selectedItems, this.get('listRowData')).mapBy('data');
        this.set('selectedPoliciesData', selectedPoliciesData);

        if (selectedItems.length === 1) {
          var actionData = this.get('parentController.content.data');

          context = {
            policyData: selectedPoliciesData[0],
            actionData: actionData,

            isPolicySelected: true
          };
        }
      }

      // Delete
      actions.push({
        name: this.get('tRemoveActionFromPolicies'),
        actionName: 'deleteActionPolicyAssignments',
        context: selectedPoliciesData,
        disabled: selectedItems === null || selectedItems.length === 0,
        iconClassNames: 'icon-trashcan'
      });

      // Edit
      actions.push({
        name: this.get('tEditPolicyAssignmentProperties'),
        actionName: 'actionEditPolicyAssignments',
        context: context,
        disabled: selectedItems.length !== 1,
        iconClassNames: 'icon-edit2'
      });

      return actions;
    }.property('selections.[]')
  });
});
