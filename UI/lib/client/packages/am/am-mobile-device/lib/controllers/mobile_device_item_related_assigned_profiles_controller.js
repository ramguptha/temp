define([
  'ember',
  'am-desktop',
  'am-data'
], function (Em,
             AmDesktop,
             AmData) {
  'use strict';

  return AmDesktop.ListControllerColumns.extend({

    validProfiles: [],

    sort: Em.A([{
      attr: 'profileName',
      dir: 'asc'
    }]),

    getFilteredData: function(data) {
      var self = this, minTargetOsVersion = this.get('parentController.model.data.osPlatform'),
        filteredData = data.filter(function (data) {
          return self.get('parentController.model.data.osPlatform') === data.get('data.platformType');
        });

      this.set('validProfiles', filteredData.filter(function (app) {
        return minTargetOsVersion >= app.get('data.minOsVersion');
      }));

      return this.get('validProfiles');
    },

    dataStore: function () {
      return AmData.get('stores.mobileDeviceAssignedProfilesStore');
    }.property(),

    userPrefsEndpointName: 'assignedConfigRelatedListColumns',

    visibleColumnNames: 'profileName policyName availabilitySelector profileStartTime profileEndTime'.w(),

    parentController: Em.inject.controller('amMobileDeviceItem'),

    relatedListTitle: 'amMobileDevice.devicePage.assignedConfigurationProfilesTab.title'.tr(),

    dataStoreContext: function () {
      return {mobileDeviceId: this.get('parentController.id')};
    }.property('parentController.id')
  });
});
