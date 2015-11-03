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

    relatedListTitle: 'amMobileDevice.devicePage.certificatesTab.title'.tr(),

    dataStore: function() {
      return AmData.get('stores.mobileDeviceCertificateStore');
    }.property(),

    visibleColumnNames: 'name isIdentity'.w(),

    dataStoreContext: function() {
      return { mobileDeviceId: this.get('parentController.id') };
    }.property('parentController.id')
  });
});
