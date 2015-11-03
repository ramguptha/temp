define([
  'ember',
  'desktop',
  'am-desktop',

  '../namespace',
  'packages/am/am-session',
  'am-data'
], function (
  Em,
  Desktop,
  AmDesktop,

  AmMobilePolicy,
  AmSession,
  AmData
) {
  'use strict';

  return AmDesktop.ListControllerColumns.extend(Desktop.ChildController, {

    tRemoveInHouseApplication: 'amMobilePolicies.mobilePolicyDetailsPage.body.actionsMenu.options.removeInHouseApplication'.tr(),

    hasRowClick: true,
    selectOnRowClick: true,
    selectionEnabled: true,

    userPrefsEndpointName: 'inHouseAppRelatedFromPolicyListColumns',

    visibleColumnNames: 'name assignmentRule osPlatform version buildNumber size shortDescription bundleIdentifier minOsVersion isUniversal supportedDevices provisioningProfile provProfileExpiryDate'.w(),

    dataStore: function () {
      return AmData.get('stores.inHouseApplicationFromPolicyStore');
    }.property(),

    relatedListTitle: 'amMobilePolicies.mobilePolicyDetailsPage.body.tabDescriptions.inHouseApplications'.tr(),

    dataStoreContext: function () {
      return { mobilePolicyId: this.get('parentController.id') };
    }.property('parentController.id'),

    listActions: function () {
      var actions = null;
      if (AmSession.hasInstallApplicationPermission()) {
        if (!Em.isNone(this.get('parentController.model.data'))) {
          actions = [{ labelResource: 'amMobilePolicies.mobilePolicyDetailsPage.body.buttons.addInHouseApplication',
            iconClassNames: 'plus-content icon-plus', actionName: 'addInHouseApp' }];
        }
      }
      return actions;
    }.property('parentController.model.data.isManaged', 'parentController.model.data.osPlatformEnum',
      'parentController.model.data.absoluteAppsVersion'),

    createColumns: function(names) {
      var columns = this._super(names);

      columns.forEach(function(column) {
        // We need to show specific icons for content type
        if (column.get('name') === 'osPlatform') {
          column.set('valueComponent', 'am-formatted-os-platform');
        }
      });

      return columns;
    },

    selectionActions: function () {
      var selectedItems = this.get('selections'), actions = Em.A();

      // Delete
      actions.push({
        name: this.get('tRemoveInHouseApplication'),
        actionName: 'removeInHouseApp',
        contextPath: 'selections',
        disabled: selectedItems === null ||
                  selectedItems.length === 0 ||
                  !AmSession.hasUninstallApplicationPermission() ||
                  Em.isNone(this.get('parentController.model.data')),
        iconClassNames: 'icon-trashcan'
      });

      return actions;
    }.property('selections.[]', 'parentController.model.data.isManaged', 'parentController.model.data.osPlatformEnum',
      'parentController.model.data.absoluteAppsVersion')
  });
});
