define([
  'ember',
  'am-desktop',

  '../namespace',
  './assignable_list_base_controller'
], function (
  Em,
  AmDesktop,

  AmAssignableItem,
  AssignableListBaseController
) {
  'use strict';

  return AssignableListBaseController.extend({

    helpUri: 1038,

    tHeader: 'amAssignableItem.assignableInHouseApplicationsPage.title'.tr(),

    path: 'am_assignable_list.in_house_apps',
    titleResource: 'amAssignableItem.assignableInHouseApplicationsPage.breadcrumbsTitle',

    userPrefsEndpointName: 'assignableInHouseAppsListColumns',

    dataStore: function () {
      return AmAssignableItem.get('assignedInHouseAppsStore');
    }.property(),

    visibleColumnNames: 'name osPlatform version buildNumber size shortDescription bundleIdentifier packageName minOsVersion isUniversal supportedDevices provisioningProfile provProfileExpiryDate'.w(),

    // Perform specific formatting to specific columns
    createColumns: function(names) {
      var columns = this._super(names);

      columns.forEach(function(column) {
        // We need to show specific icons for specific Operating Systems(iOS, Android)
        if (column.get('name') === 'osPlatform') {
          column.set('valueComponent', 'am-formatted-os-platform');
        }
      });

      return columns;
    }
  });
});
