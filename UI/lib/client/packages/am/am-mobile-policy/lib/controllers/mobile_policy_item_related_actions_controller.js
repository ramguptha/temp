define([
  'ember',
  'desktop',
  'am-desktop',
  'am-data',
  'guid',

  '../namespace'
], function(
  Em,
  Desktop,
  AmDesktop,
  AmData,
  Guid,

  AmMobilePolicy
) {
  'use strict';

  return AmDesktop.ListControllerColumns.extend(Desktop.ChildController, {

    tEditActionAssignmentProperties: 'amMobilePolicies.mobilePolicyDetailsPage.body.actionsMenu.options.editActionAssignmentProperties'.tr(),
    tRemoveActionFromPolicy: 'amMobilePolicies.mobilePolicyDetailsPage.body.actionsMenu.options.removeActionFromPolicy'.tr(),
    tReapplyActionCommand: 'amMobilePolicies.mobilePolicyDetailsPage.body.actionsMenu.options.reexecuteActionOnPolicy'.tr(),
    tRemoveFromPolicy: 'amMobilePolicies.mobilePolicyDetailsPage.body.actionsMenu.options.removeFromPolicy'.tr(),
    relatedListTitle: 'amMobilePolicies.mobilePolicyDetailsPage.body.tabDescriptions.actions'.tr(),

    hasRowClick: true,
    selectionEnabled: true,

    userPrefsEndpointName: 'actionRelatedToPolicyListColumns',

    visibleColumnNames: 'name type osPlatform initialDelay repeatInterval repeatCount'.w(),

    dataStore: function() {
      return AmMobilePolicy.get('relatedActionStore');
    }.property(),

    dataStoreContext: function() {
      return { mobilePolicyId: this.get('parentController.id') };
    }.property('parentController.id'),

    listActions: [
      {
        labelResource: 'amMobilePolicies.mobilePolicyDetailsPage.body.buttons.addActionsToPolicy',
        iconClassNames: 'plus-content icon-plus',
        actionName: 'addActionsToPolicy'
      }
    ],

    selectionActions: function() {
      var selectedItems = this.get('selections'), actions = Em.A(), selectedActionData = Em.A(), context = {};

      // Prepare the context for editing policy assignment properties
      if (selectedItems) {
        selectedActionData = this.getSelectionActionContext(selectedItems, this.get('listRowData'));
      }

      if (selectedItems && selectedItems.length === 1) {
        var policyData = this.get('parentController.content.data');

        context = {
          policyData: policyData,
          actionData: selectedActionData[0].get('data'),

          isPolicySelected: false
        };
      }

      // Delete
      actions.push({
        name: this.get('tRemoveActionFromPolicy'),
        actionName: 'deleteActionPolicyAssignments',
        context: selectedActionData,
        disabled: selectedItems === null || selectedItems.length === 0,
        iconClassNames: 'icon-trashcan'
      });

      // Edit
      actions.push({
        name: this.get('tEditActionAssignmentProperties'),
        actionName: 'policyEditActionAssignments',
        context: context,
        disabled: selectedItems.length !== 1,
        iconClassNames: 'icon-edit2'
      });

      // Re-execute action
      actions.push({
        name: this.get('tReapplyActionCommand'),
        actionName: 'reexecuteAction',
        context: selectedActionData.mapBy('data'),
        disabled: selectedItems === null || selectedItems.length === 0,
        iconClassNames: 'icon-refresh'
      });

      return actions;
    }.property('selections.[]')
  });
});