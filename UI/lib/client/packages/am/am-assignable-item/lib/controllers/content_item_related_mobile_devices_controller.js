define([
  'ember',
  'desktop',
  '../namespace',
  'am-desktop'
], function(
  Em,
  Desktop,
  AmContent,
  AmDesktop
  ) {
  'use strict';

  return AmDesktop.ListControllerColumns.extend(Desktop.ChildController, {
    relatedListTitle: 'amAssignableItem.assignableContentDetailsPage.tabMobileDevices.header'.tr(),

    userPrefsEndpointName: 'deviceRelatedToContentListColumns',

    visibleColumnNames: 'name model osPlatform osVersion serialNumber phoneNumber lastContact isPasscodePresent'.w(),

    hasRowClick: true,
    
    dataStore: function() {
      return AmContent.get('relatedMobileDevicesStore');
    }.property(),

    dataStoreContext: function() {
      return { contentId: this.get('parentController.id') };
    }.property('parentController.id')
  });
});
