define([
  'ember',
  'query',
  'desktop',
  'am-desktop',

  '../namespace'
], function (Em,
             Query,
             Desktop,
             AmDesktop,
             AmMobilePolicy) {
  'use strict';

  return AmDesktop.ListControllerColumns.extend(Desktop.ChildController, {

    tEditPolicyAssignmentProperties: 'amMobilePolicies.mobilePolicyDetailsPage.body.actionsMenu.options.editPolicyAssignmentProperties'.tr(),
    tRemoveContentFromPolicy: 'amMobilePolicies.mobilePolicyDetailsPage.body.actionsMenu.options.removeContentFromPolicy'.tr(),

    userPrefsEndpointName: 'contentRelatedToPolicyListColumns',

    hasRowClick: true,
    selectionEnabled: true,

    visibleColumnNames: 'name mediaCategory mediaFileSize mediaFileAssignmentType mediaFileAssignmentAvailability mediaFileAssignmentStartTime mediaFileAssignmentEndTime'.w(),

    relatedListTitle: 'amMobilePolicies.mobilePolicyDetailsPage.body.tabDescriptions.content'.tr(),

    dataStore: function () {
      return AmMobilePolicy.get('relatedContentStore');
    }.property(),

    dataStoreContext: function () {
      return {mobilePolicyId: this.get('parentController.id')};
    }.property('parentController.id'),

    listActions: [
      {
        labelResource: 'amMobilePolicies.mobilePolicyDetailsPage.body.buttons.addContentToPolicy',
        iconClassNames: 'plus-content icon-plus',
        actionName: 'addContentToPolicy'
      }
    ],

    selectionActions: function () {
      var selectedItems = this.get('selections'), actions = Em.A(), context = {};

      if (selectedItems && selectedItems.length === 1) {
        context = this.getSelectionActionContext(selectedItems, this.get('listRowData'))[0].get('data');
      }

      // Delete
      actions.push({
        name: this.get('tRemoveContentFromPolicy'),
        actionName: 'removeContentFromPolicy',
        contextPath: 'selections',
        disabled: selectedItems === null || selectedItems.length === 0,
        iconClassNames: 'icon-trashcan'
      });

      // Edit
      actions.push({
        name: this.get('tEditPolicyAssignmentProperties'),
        actionName: 'editPolicyAssignments',
        context: context,
        disabled: selectedItems.length !== 1,
        iconClassNames: 'icon-edit2'
      });

      return actions;
    }.property('selections.[]')
  });
});
