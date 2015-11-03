define([
  'ember',
  'am-desktop',
  'am-data'

], function(
  Em,
  AmDesktop,
  AmData

) {
  'use strict';
  
  return AmDesktop.AmListController.extend({
    hasRowClick: true,
    selectOnRowClick: true,
    selectionEnabled: true,

    dataStore: function() {
      return AmData.get('stores.inHouseApplicationStore');
    }.property(),

    visibleColumnNames: function() {
      var columns = 'name';
      if (!Em.isNone(this.targetOs) && this.targetOs == 11) {// Android
        columns += ' packageName';
      } else {
        columns += ' bundleIdentifier';
      }

      columns += ' osPlatform version buildNumber minOsVersion';
      if (!Em.isNone(this.targetOs) && this.targetOs == 10) {// iOS
        columns += ' supportedDevices';
      }
      columns += ' shortDescription';

      return columns.w();
    }.property('targetOs'),

    createColumns: function (names) {
      var columns = this._super(names);

      columns.forEach(function (column) {
        // We need to show specific icons for content type
        if (column.get('name') === 'osPlatform') {
          column.set('valueComponent', 'am-formatted-os-platform');
        }
      });

      return columns;
    }
  });
});

