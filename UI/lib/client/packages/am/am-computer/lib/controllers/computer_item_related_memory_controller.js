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

    relatedListTitle: 'amComputer.memoryTab.title'.tr(),
    userPrefsEndpointName: 'computerMemoryListColumns',
    visibleColumnNames: 'memorySlotName memorySize memorySpeed memoryType'.w(),

    dataStore: function() {
      return AmData.get('stores.computerItemMemoryStore');
    }.property(),

    loadMemory: function(id)  {
      this.setProperties({
        'searchQuery.context': { computerId: id },
        'searchQuery.sort': Em.A([{ attr: 'memorySlotName', dir: 'asc' }]),

        paused: false
      });
    }
  });
});
