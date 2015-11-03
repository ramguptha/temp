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

  AmContent
) {
  'use strict';

  return AmDesktop.ListControllerColumns.extend(Desktop.ChildController, {
    relatedListTitle: 'amAssignableItem.assignableContentDetailsPage.tabPolicies.header'.tr(),

    tEditPolicyAssignmentProperties: 'amAssignableItem.assignableContentDetailsPage.tabPolicies.actionsMenu.options.editPolicyAssignmentProperties'.tr(),
    tRemoveContentFromPolicies: 'amAssignableItem.assignableContentDetailsPage.tabPolicies.actionsMenu.options.removeContentFromPolicies'.tr(),

    userPrefsEndpointName: 'policyRelatedToContentListColumns',

    visibleColumnNames: 'name isSmartPolicy'.w(),

    selectionEnabled: true,
    hasRowClick: true,

    dataStore: function() {
      return AmContent.get('relatedMobilePoliciesStore');
    }.property(),
    
    dataStoreContext: function() {
      return { contentId: this.get('parentController.id') };
    }.property('parentController.id'),

    listActions: function() {
      return [{
        labelResource: 'amAssignableItem.assignableContentDetailsPage.tabPolicies.buttons.addContentToPolicies',
        iconClassNames: 'plus-content icon-plus',
        actionName: 'assignPoliciesToContent'
      }];
    }.property(),

    selectionActions: function() {
      var selectedItems = this.get('selections'),
        actions = Em.A(),
        context = {};

      if (selectedItems && selectedItems.length > 0) {
        context = this.getSelectionActionContext(selectedItems, this.get('listRowData')).mapBy('data');
      }

      // Delete
      actions.push({
        name: this.get('tRemoveContentFromPolicies'),
        actionName: 'removeFromPolicies',
        context: context,
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
