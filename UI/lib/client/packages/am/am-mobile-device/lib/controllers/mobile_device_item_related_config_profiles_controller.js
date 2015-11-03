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

  return AmDesktop.ListControllerColumns.extend({
    dataStore: function() {
      return AmData.get('stores.mobileDeviceInstalledConfigProfileStore');
    }.property(),

    userPrefsEndpointName: 'configRelatedFromDeviceListColumns',

    visibleColumnNames: 'name description organization identifier profileType uuid isEncrypted isManaged allowRemoval'.w(),

    parentController: Em.inject.controller('amMobileDeviceItem'),

    hasRowClick: true,
    selectOnRowClick: true,
    selectionEnabled: true,

    relatedListTitle: 'amMobileDevice.devicePage.configurationProfilesTab.title'.tr(),

    tUninstallConfigurationProfile: 'amMobileDevice.devicePage.configurationProfilesTab.buttons.uninstallConfigurationProfile'.tr(),

    dataStoreContext: function () {
      return { mobileDeviceId: this.get('parentController.id') };
    }.property('parentController.id'),

    listActions: function () {
      var actions = null;
      if (AmSession.hasInstallConfigProfilePermission()) {
        if (!Em.isNone(this.get('parentController.model.data')) &&
          this.get('parentController.mobileDeviceListController').supportsIOSManagedCommands(this.get('parentController'))) {
          actions = [{
            labelResource: 'amMobileDevice.devicePage.configurationProfilesTab.buttons.installConfigurationProfile',
            iconClassNames: 'plus-content icon-plus',
            actionName: 'installConfigProfile'
          }];
        }
      }
      return actions;
    }.property('parentController.model.data.isManaged', 'parentController.model.data.osPlatformEnum',
      'parentController.model.data.absoluteAppsVersion'),

    selectionActions: function () {
      var selectedItems = this.get('selections');
      return [{
        name: this.get('tUninstallConfigurationProfile'),
        actionName: 'uninstallConfigProfile',
        contextPath: 'selections',
        disabled: selectedItems === null ||
        selectedItems.length === 0 || !AmSession.hasUninstallConfigProfilePermission() ||
        Em.isNone(this.get('parentController.model.data')) || !this.get('parentController.mobileDeviceListController').supportsIOSManagedCommands(this.get('parentController')),
        iconClassNames: 'icon-trashcan'
      }];
    }.property('selections.[]', 'parentController.model.data.isManaged', 'parentController.model.data.osPlatformEnum',
      'parentController.model.data.absoluteAppsVersion')
  });
});

