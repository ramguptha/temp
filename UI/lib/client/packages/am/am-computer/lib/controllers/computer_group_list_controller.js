define([
  'ember',
  'am-desktop',
  '../namespace'
], function(
  Em,
  AmDesktop,
  AmComputer
) {
  'use strict';

  return Em.Controller.extend({
    actions: {
      rowClick: function(row) {
        this.send('gotoNavItem', row.get('node.id'));
      }
    },

    navTitle: 'amComputer.computerListPage.groupNavTitle'.tr(),

    amComputerGroupsShowGroupController:Em.inject.controller('amComputerGroupsShowGroup'),

    selectedId: Em.computed.oneWay('amComputerGroupsShowGroupController.id'),

    NavController: AmDesktop.AmNavController.extend({
      content: Em.computed.oneWay('parentController'),

      dataStore: function () {
        return AmComputer.get('AmData.stores.computerGroupStore');
      }.property()
    }),

    navSizeController: Em.inject.controller('amComputerNavSize'),
    navController: function() {
      return this.NavController.create({
        parentController: this,
        selectedIdBinding: 'parentController.selectedId'
      });
    }.property()
  });
});
