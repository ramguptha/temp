define([
  'ember',
  'desktop',
  'am-desktop',

  '../namespace',
  'packages/am/am-session',
  'am-data'
], function (Em,
             Desktop,
             AmDesktop,
             AmMobilePolicy,
             AmSession,
             AmData) {
  'use strict';

  return AmDesktop.ListControllerColumns.extend(Desktop.ChildController, {

    tEditPolicyAssignmentProperties: 'amMobilePolicies.mobilePolicyDetailsPage.body.actionsMenu.options.editPolicyAssignmentProperties'.tr(),
    tRemoveConfigurationProfile: 'amMobilePolicies.mobilePolicyDetailsPage.body.actionsMenu.options.removeConfigurationProfile'.tr(),

    selectionEnabled: true,
    hasRowClick: true,
    selectOnRowClick: true,

    dataStore: function() {
      return AmData.get('stores.configurationProfileFromPolicyStore');
    }.property(),

    userPrefsEndpointName: 'configRelatedFromPolicyListColumns',

    visibleColumnNames: 'name assignmentRule description organization identifier profileType uuid allowRemoval availabilitySelector profileStartTime profileEndTime'.w(),

    relatedListTitle: 'amMobilePolicies.mobilePolicyDetailsPage.body.tabDescriptions.configurationProfiles'.tr(),

    dataStoreContext: function () {
      return {mobilePolicyId: this.get('parentController.id')};
    }.property('parentController.id'),

    listActions: function () {
      var actions = null;
      if (AmSession.hasInstallConfigProfilePermission()) {
        if (!Em.isNone(this.get('parentController.model.data'))) {
          actions = [{
            labelResource: 'amMobilePolicies.mobilePolicyDetailsPage.body.buttons.addConfigurationProfile',
            iconClassNames: 'plus-content icon-plus', actionName: 'addConfigProfile'
          }];
        }
      }
      return actions;
    }.property('parentController.model.data.isManaged', 'parentController.model.data.osPlatformEnum',
      'parentController.model.data.absoluteAppsVersion'),

    selectionActions: function () {
      var selectedItems = this.get('selections'), actions = Em.A();

      // Delete
      actions.push({
        name: this.get('tRemoveConfigurationProfile'),
        actionName: 'removeConfigProfile',
        contextPath: 'selections',
        disabled: selectedItems === null ||
        selectedItems.length === 0 || !AmSession.hasUninstallConfigProfilePermission() ||
        Em.isNone(this.get('parentController.model.data')),
        iconClassNames: 'icon-trashcan'
      });

      var context = Em.A();
      if (!Em.isEmpty(selectedItems)) {
        context = this.getSelectionActionContext(selectedItems, this.get('listRowData'));
      }

      // Edit
      actions.push({
        name: this.get('tEditPolicyAssignmentProperties'),
        actionName: 'editPolicyAssignmentConfigProfile',
        context: context,
        disabled: selectedItems.length !== 1 || !AmSession.hasUninstallConfigProfilePermission() || Em.isNone(this.get('parentController.model.data')),
        iconClassNames: 'icon-edit2'
      });

      return actions;
    }.property('selections.[]', 'parentController.model.data.isManaged', 'parentController.model.data.osPlatformEnum',
      'parentController.model.data.absoluteAppsVersion')
  });
});
