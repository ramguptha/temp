define([
  'ember',
  'desktop',
  'am-desktop',
  'am-data'
], function(
  Em,
  Desktop,
  AmDesktop,
  AmData
) {
  'use strict';
  
  return AmDesktop.AmListController.extend({
    selectionEnabled: true,

    dataStore: function() {
      return AmData.get('stores.mobilePolicyStore');
    }.property(),

    dataStoreContext: function() {
      return { mobileDeviceListName: AmData.get('specs.AmMobileDeviceGroupSpec.DEFAULT_NAME') };
    }.property(),

    visibleColumnNames: 'name isSmartPolicy'.w()
  });
});
