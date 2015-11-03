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

  return AmDesktop.AmListController.extend({
    parentController: Em.inject.controller('amMobileDeviceItem'),

    visibleColumnNames: 'name'.w(),

    relatedListTitle: 'amMobileDevice.devicePage.administratorsTab.title'.tr(),

    dataStore: function() {
      return AmData.get('stores.mobileDeviceAdministratorStore');
    }.property(),

    dataStoreContext: function() {
      return { mobileDeviceId: this.get('parentController.id') };
    }.property('parentController.id')
  });
});

