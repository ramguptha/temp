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

    relatedListTitle: 'amComputer.installedSoftwareTab.title'.tr(),
    userPrefsEndpointName: 'computerInstalledSoftwarePcListColumns',
    visibleColumnNames: 'instSoftwareName instSoftwareCompany instSoftwareVersionString instSoftwareSize instSoftwareInstallationDate uninstallable isHotfix identificationType installLocation instSoftwareProductId registeredCompany registeredOwner installedBy'.w(),

    dataStore: function() {
      return AmData.get('stores.computerItemInstalledSoftwarePcStore');
    }.property(),

    loadInstalledSoftware: function(id)  {
      this.setProperties({
        'searchQuery.context': { computerId: id },
        'searchQuery.sort': Em.A([{ attr: 'instSoftwareName', dir: 'asc' }]),

        paused: false
      });
    }
  });
});
