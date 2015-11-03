define([
  'ember',
  'am-desktop',
  'am-data'
], function(
  Em,
  AmDesktop,
  AmData
) {
  'use strict';

  return AmDesktop.ListControllerColumns.extend({
    validApps: [],

    sort: Em.A([{
      attr: 'appName',
      dir: 'asc'
    }]),

    getFilteredData: function(data) {
      var self = this,
        minTargetOsVersion = this.get('parentController.model.data.osVersion'),
        filteredData = data.filter(function (data) {
          return self.get('parentController.model.data.osPlatform') === data.get('data.platformType');
        });

      this.set('validApps', filteredData.filter(function (app) {
        return minTargetOsVersion >= app.get('data.minOsVersion');
      }));

      return this.get('validApps');
    },

    userPrefsEndpointName: 'assignedThirdPartyAppListColumns',

    visibleColumnNames: 'icon appName policyName rule'.w(),

    dataStore: function () {
      return AmData.get('stores.mobileDeviceAssignedAppsStore');
    }.property(),

    parentController: Em.inject.controller('amMobileDeviceItem'),

    relatedListTitle: 'amMobileDevice.devicePage.assignedThirdPartyApplicationsTab.title'.tr(),

    dataStoreContext: function() {
      return { mobileDeviceId: this.get('parentController.id') };
    }.property('parentController.id'),

    apiBase: '/api/thirdpartyapps/'
  });
});
