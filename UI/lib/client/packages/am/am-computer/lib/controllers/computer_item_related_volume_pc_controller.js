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

    relatedListTitle: 'amComputer.volumeTab.title'.tr(),
    userPrefsEndpointName: 'computerVolumeMacListColumns',
    visibleColumnNames: 'volumeName size format displayName freeSpace freeSpacePercent driveLetter volumeSerialNumber bootVolume compressed'.w(),

    dataStore: function() {
      return AmData.get('stores.computerItemVolumePcStore');
    }.property(),

    loadVolume: function(id)  {
      this.setProperties({
        'searchQuery.context': { computerId: id },
        'searchQuery.sort': Em.A([{ attr: 'volumeName', dir: 'asc' }]),

        paused: false
      });
    }
  });
});