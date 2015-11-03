define([
  'ember',
  'ui',
  'am-desktop',

  '../namespace',
  './assignable_list_base_controller'
], function (
  Em,
  UI,
  AmDesktop,

  AmAssignableItem,
  AssignableListBaseController
) {
  'use strict';

  return AssignableListBaseController.extend({

    helpUri: 1039,

    tHeader: 'amAssignableItem.assignableThirdPartyApplicationsPage.title'.tr(),

    apiBase: '/api/thirdpartyapps/',
    path: 'am_assignable_list.third_party_apps',
    titleResource: 'amAssignableItem.assignableThirdPartyApplicationsPage.breadcrumbsTitle',
    userPrefsEndpointName: 'assignableThirdPartyAppsListColumns',

    dataStore: function () {
      return AmAssignableItem.get('assignedThirdPartyAppsStore');
    }.property(),

    visibleColumnNames: 'icon name osPlatform category minOsVersion isUniversal supportedDevices shortDescription preventDataBackup removeWhenMDMIsRemoved vppCodesPurchased vppCodesRedeemed vppCodesRemaining'.w(),

    // Perform specific formatting to specific columns
    createColumns: function(names) {
      var columns = this._super(names);

      columns.forEach(function(column) {
        // We need to show specific icons for specific Operating Systems(iOS, Android)
        if (column.get('name') === 'osPlatform') {
          column.set('valueComponent','am-formatted-os-platform');
        }
      });

      return columns;
    }
  });
});
