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
    userPrefsEndpointName: 'computerInstalledSoftwareMacListColumns',
    visibleColumnNames: 'instSoftwareName instSoftwareInfo instSoftwareVersionString instSoftwareSize instSoftwareInstallationDate identificationType instSoftwareProductId'.w(),

    dataStore: function() {
      return AmData.get('stores.computerItemInstalledSoftwareMacStore');
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
