define([
  'ember',
  'am-desktop',
  'am-data'
], function (
  Em,
  AmDesktop,
  AmData
  ) {
  'use strict';

  return AmDesktop.ListControllerColumns.extend({
    relatedListTitle: 'amComputer.installedProfileTab.title'.tr(),
    visibleColumnNames: 'profileDisplayName profileType profileIdentifier profileInstallDate profileOrganization profileUninstallPolicy profileUser profileVerificationState profileDescription'.w(),
    userPrefsEndpointName: 'computerInstalledProfileListColumns',

    dataStore: function() {
      return AmData.get('stores.computerItemInstalledProfileStore');
    }.property(),

    loadInstalledProfile: function(id)  {
      this.setProperties({
        'searchQuery.context': { computerId: id },
        'searchQuery.sort': Em.A([{ attr: 'profileDisplayName', dir: 'asc' }]),

        paused: false
      });
    }
  });
});
