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

    helpUri: 1041,

    tHeader: 'amAssignableItem.assignableConfigurationProfilesPage.title'.tr(),

    path: 'am_assignable_list.config_profiles',
    titleResource: 'amAssignableItem.assignableConfigurationProfilesPage.title',

    userPrefsEndpointName: 'assignableConfigProfilesListColumns',

    dataStore: function () {
      return AmAssignableItem.get('assignedConfigProfilesStore');
    }.property(),

    visibleColumnNames: 'name description organization profileType identifier uuid allowRemoval variablesUsed'.w()
  });
});
