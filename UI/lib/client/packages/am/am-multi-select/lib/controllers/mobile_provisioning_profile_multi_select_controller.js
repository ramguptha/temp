define([
  'ember',
  'am-data',
  'desktop',
  'am-desktop'
], function(
  Em,
  AmData,
  Desktop,
  AmDesktop
  ) {
  'use strict';

  return AmDesktop.AmListController.extend({
    selectionEnabled: true,

    dataStore: function() {
      return AmData.get('stores.provisioningProfileStore');
    }.property(),

    dataStoreContext: function() {
      return { mobileDeviceListName: AmData.get('specs.AmMobileDeviceGroupSpec.DEFAULT_NAME') };
    }.property(),

    visibleColumnNames: 'name expiry uuid'.w()
  });
});
