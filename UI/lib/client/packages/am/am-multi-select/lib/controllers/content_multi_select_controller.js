define([
  'ember',
  'desktop',
  'am-desktop',
  'am-data'
], function(
  Em,
  Desktop,
  AmDesktop,
  AmData
) {
  'use strict';
  
  return AmDesktop.AmListController.extend({
    selectionEnabled: true,

    dataStore: function() {
      return AmData.get('stores.contentStore');
    }.property(),

    visibleColumnNames: 'name mediaCategory mediaFileSize'.w()
  });
});

