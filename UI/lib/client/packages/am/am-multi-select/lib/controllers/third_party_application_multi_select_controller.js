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

  return AmDesktop.AmListController.extend({
    listHeaderView: Em.View.extend({ defaultTemplate: Em.Handlebars.compile('<th>Application Name</th>') }),
    listItemView: Em.View.extend({ defaultTemplate: Em.Handlebars.compile('<td>{{item.name}}</td>') }),

    hasRowClick: true,
    selectOnRowClick: true,
    selectionEnabled: true,

    dataStore: function () {
      return AmData.get('stores.thirdPartyApplicationStore');
    }.property(),

    apiBase: '/api/thirdpartyapps/',

    visibleColumnNames: function () {
      var columns = 'icon name osPlatform minOsVersion shortDescription appStoreURL';

      if (Em.isNone(this.targetOs) || this.targetOs == 10) {
        columns += " vppCodesRemaining";
      }

      if (!Em.isNone(this.targetOs) && this.targetOs == 10) {// iOS
        columns += ' supportedDevices hasRedemptionCode';
      }

      return columns.w();
    }.property('targetOs'),

    // Perform specific formatting to specific columns
    createColumns: function (names) {
      var columns = this._super(names);

      columns.forEach(function (column) {
        // We need to show specific icons for specific Operating Systems(iOS, Android)
        if (column.get('name') === 'osPlatform') {
          column.set('valueComponent', 'am-formatted-os-platform');
        }
      });

      return columns;
    }
  });
});