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

  AmMobileDevice,
  AmSession,
  AmData
) {
  'use strict';

  return AmDesktop.ListControllerColumns.extend({
    userPrefsEndpointName: 'appRelatedListColumns',

    visibleColumnNames: 'name versionString buildNumber'.w(),

    dataStore: function () {
      return AmData.get('stores.mobileDeviceInstalledAppStore');
    }.property(),

    tUninstallApplication: 'amMobileDevice.devicePage.applicationsTab.buttons.uninstallApplication'.tr(),

    parentController: Em.inject.controller('amMobileDeviceItem'),

    relatedListTitle: 'amMobileDevice.devicePage.applicationsTab.title'.tr(),

    hasRowClick: true,
    selectOnRowClick: true,

    selectionEnabled: function() {
      return !this.get('parentController.hasWarning')
    }.property('parentController.hasWarning'),

    osPlatformEnum: function () {
      return this.get('parentController.model.data.osPlatformEnum');
    }.property('parentController.model.data.osPlatformEnum'),

    dataStoreContext: function () {
      return { mobileDeviceId: this.get('parentController.id') };
    }.property('parentController.id'),

    listActions: function () {
      var actions = null;
      if (AmSession.hasInstallApplicationPermission()) {
        if (!Em.isNone(this.get('parentController.model.data')) &&
            this.get('parentController.mobileDeviceListController').supportsIOSManagedCommands(this.get('parentController'))) {
          actions = [{
            labelResource: 'amMobileDevice.devicePage.applicationsTab.buttons.installApplication',
            iconClassNames: 'plus-content icon-plus',
            actionName: 'installApplication'
          }];
        }
      }
      return actions;
    }.property('parentController.model.data.isManaged', 'parentController.model.data.osPlatformEnum',
      'parentController.model.data.absoluteAppsVersion'),

    selectionActions: function () {
      var selectedItems = this.get('selections');

      return [{ name: this.get('tUninstallApplication'),
        actionName: 'uninstallApplication',
        contextPath: 'selections',
        disabled: selectedItems === null ||
        selectedItems.length === 0 ||
        !AmSession.hasUninstallApplicationPermission() ||
        Em.isNone(this.get('parentController.model.data')) ||
        !this.get('parentController.mobileDeviceListController').supportsIOSManagedCommands(this.get('parentController')),
        iconClassNames: 'icon-trashcan'
      }];
    }.property('selections.[]', 'parentController.model.data.isManaged', 'parentController.model.data.osPlatformEnum',
      'parentController.model.data.absoluteAppsVersion')
  });
});