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
    userPrefsEndpointName: 'computerMissingPatchPcListColumns',
    visibleColumnNames: 'missingPatchName missingPatchVersion missingPatchSeverity missingPatchIsMandatory missingPatchReleaseDate missingPatchIsOsPatch missingPatchAction missingPatchInstallDeadline missingPatchLanguage'.w(),

    dataStore: function() {
      return AmData.get('stores.computerItemMissingPatchPcStore');
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