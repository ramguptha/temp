define([
  'ember',
  '../namespace',
  'am-desktop'
], function (Em,
             AmMobileDevice,
             AmDesktop) {
  'use strict';

  return AmDesktop.ListControllerColumns.extend({
    actions: {
      // the store in this controller has a composite id thus we must extract the content id first
      rowClick: function(row) {
        this.send('gotoListItem', row.get('node.id').split(':')[1]);
      }
    },
    hasRowClick: true,

    userPrefsEndpointName: 'contentRelatedListColumns',

    visibleColumnNames: 'name mobilePolicyName isSmartPolicy mediaFileAssignmentType mediaFileAssignmentAvailability mediaFileAssignmentStartTime mediaFileAssignmentEndTime'.w(),

    parentController: Em.inject.controller('amMobileDeviceItem'),

    relatedListTitle: 'amMobileDevice.devicePage.assignedContentTab.title'.tr(),

    dataStore: function () {
      return AmMobileDevice.get('relatedContentStore');
    }.property(),

    dataStoreContext: function () {
      return {mobileDeviceId: this.get('parentController.id')};
    }.property('parentController.id')
  });
});

