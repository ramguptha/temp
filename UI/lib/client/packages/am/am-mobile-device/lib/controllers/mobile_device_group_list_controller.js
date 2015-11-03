define([
  'ember',
  'am-desktop',
  'query',

  '../namespace'
], function(
  Em,
  AmDesktop,
  Query,

  AmMobileDevice
) {
  'use strict';

  return Em.Controller.extend({
    actions: {
      rowClick: function(row) {
        this.send('gotoNavItem', row.get('node.id'));
      }
    },

    navTitle: 'amMobileDevice.mobileDevicesListPage.navTitle'.tr(),

    NavController: AmDesktop.AmNavController.extend({
      content: Em.computed.oneWay('parentController'),

      dataStore: function () {
        return AmMobileDevice.get('groupStore');
      }.property(),

      searchQuery: function() {
        return Query.Search.create({
          sort: Em.A([{
            attr: 'endPointName',
            dir: 'asc'
          }])
        });
      }.property()
    }),

    amMobileDeviceGroupsShowGroupController: Em.inject.controller('amMobileDeviceGroupsShowGroup'),
    navSizeController: Em.inject.controller('amMobileDeviceNavSize'),
    selectedId: Em.computed.oneWay('amMobileDeviceGroupsShowGroupController.id'),

    navController: function() {
      return this.NavController.create({
        parentController: this,
        selectedIdBinding: 'parentController.selectedId'
      });
    }.property()
  });
});
