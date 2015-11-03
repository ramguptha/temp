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
    relatedListTitle: 'amComputer.missingPatchTab.title'.tr(),
    userPrefsEndpointName: 'computerMissingPatchMacListColumns',
    visibleColumnNames: 'missingPatchName missingPatchVersion missingPatchSeverity missingPatchReleaseDate missingPatchIsOsPatch'.w(),

    dataStore: function() {
      return AmData.get('stores.computerItemMissingPatchMacStore');
    }.property(),

    loadMissigPatch: function(id)  {
      this.setProperties({
        'searchQuery.context': { computerId: id },
        'searchQuery.sort': Em.A([{ attr: 'missingPatchName', dir: 'asc' }]),

        paused: false
      });
    }
  });
});
