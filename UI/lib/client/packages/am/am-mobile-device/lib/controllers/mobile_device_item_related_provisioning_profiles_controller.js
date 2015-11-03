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
             AmMobileDevice,
             AmSession,
             AmData) {
  'use strict';

  return AmDesktop.AmListController.extend({
    parentController: Em.inject.controller('amMobileDeviceItem'),

    visibleColumnNames: 'name expiry uuid'.w(),

    hasRowClick: true,
    selectOnRowClick: true,
    selectionEnabled: true,

    relatedListTitle: 'amMobileDevice.devicePage.provisioningProfilesTab.title'.tr(),

    tUninstallProvisioningProfile: 'amMobileDevice.devicePage.provisioningProfilesTab.buttons.uninstallProvisioningProfile'.tr(),

    dataStore: function () {
      return AmData.get('stores.mobileDeviceInstalledProvisioningProfileStore');
    }.property(),

    dataStoreContext: function () {
      return {mobileDeviceId: this.get('parentController.id')};
    }.property('parentController.id'),

    listActions: function () {
      var actions = null;
      if (AmSession.hasInstallProvisioningProfilePermission()) {
        if (!Em.isNone(this.get('parentController.model.data')) &&
          this.get('parentController.mobileDeviceListController').supportsIOSManagedCommands(this.get('parentController'))) {
          actions = [{
            labelResource: 'amMobileDevice.devicePage.provisioningProfilesTab.buttons.installProvisioningProfile',
            iconClassNames: 'plus-content icon-plus',
            actionName: 'installProvisioningProfile'
          }];
        }
      }
      return actions;
    }.property('parentController.model.data.isManaged', 'parentController.model.data.osPlatformEnum',
      'parentController.model.data.absoluteAppsVersion'),

    selectionActions: function () {
      var selectedItems = this.get('selections');

      return [{
        name: this.get('tUninstallProvisioningProfile'),
        actionName: 'uninstallProvisioningProfile',
        contextPath: 'selections',
        disabled: selectedItems === null ||
        selectedItems.length === 0 || !AmSession.hasUninstallProvisioningProfilePermission() ||
        Em.isNone(this.get('parentController.model.data')) || !this.get('parentController.mobileDeviceListController').supportsIOSManagedCommands(this.get('parentController')),
        iconClassNames: 'icon-trashcan'
      }];
    }.property('selections.[]', 'parentController.model.data.isManaged', 'parentController.model.data.osPlatformEnum',
      'parentController.model.data.absoluteAppsVersion')
  });
});