define([
  'ember',
  'ui',

  '../namespace',
  './assignable_list_base_controller'
], function (
  Em,
  UI,

  AmAssignableItem,
  AssignableListBaseController
) {
  'use strict';

  return AssignableListBaseController.extend({

    helpUri: 1042,

    tHeader: 'amAssignableItem.assignableProvisioningProfilesPage.title'.tr(),

    path: 'am_assignable_list.provisioning_profiles',
    titleResource: 'amAssignableItem.assignableProvisioningProfilesPage.title',

    userPrefsEndpointName: 'assignableProvisioningProfilesListColumns',

    dataStore: function () {
      return AmAssignableItem.get('assignedProvisioningProfilesStore');
    }.property(),

    visibleColumnNames: 'name expiry uuid'.w()
  });
});
