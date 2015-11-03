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

  return Em.Controller.extend({
    actions: {
      rowClick: function(row) {
        this.send('gotoNavItem', row.get('node.id'));
      }
    },

    navTitle: 'amMobileCommand.shared.groupNavTitle'.tr(),

    navSelectedItemId: 'computers',

    NavController: AmDesktop.AmNavController.extend({
      content: Em.computed.oneWay('parentController'),

      dataStore: function () {
        return AmData.get('stores.commandGroupStore');
      }.property()
    }),

    navSizeController: Em.inject.controller('amCommandNavSize'),
    navController: function() {
      return this.NavController.create({
        parentController: this,
        selectedIdBinding: 'parentController.navSelectedItemId'
      });
    }.property(),

    selectedId: null
  });
});
