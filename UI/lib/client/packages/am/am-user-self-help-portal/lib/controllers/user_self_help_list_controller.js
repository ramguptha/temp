define([
  'ember',
  'desktop',
  'am-desktop',
  'packages/am/am-session',
  'packages/platform/nav-page-view',
  'am-data',
  'query',
  'guid',
  '../namespace'
], function (Em,
             Desktop,
             AmDesktop,
             AmSession,
             NavPageView,
             AmData,
             Query,
             Guid,
             AmUserSelfHelp) {
  'use strict';

  return Em.Controller.extend({
    actions: {
      rowClick: function(row) {
        this.send('gotoNavItem', row.get('node.id'));
      }
    },

    navSizeController: Em.inject.controller('amUserSelfHelpNavSize'),

    navTitle: 'amUserSelfServicePortal.deviceList.title'.tr(),

    searchableColumnSpecs: null,
    searchFilterSupported: false,

    countDelayInMilliseconds: 10,

    init: function () {
      this._super();

      // Get Privileges list and modify returning from endpoint data
      var privilegesList = Em.A(AmSession.getSelfServicePrivileges()).map(function (item) {
        var emberObj = Em.$.extend({
          id: item.CommandID
        }, item);

        return Em.Object.create(emberObj);
      });

      this.setProperties({
        selfServicePrivilegesStore: AmData.get('stores.userSelfHelpPrivilegesStore').createStaticDataStore(privilegesList),
        selectedItemId: null
      });
    },

    NavController: AmDesktop.AmNavController.extend({
      content: Em.computed.oneWay('parentController'),

      dataStore: function () {
        return AmUserSelfHelp.get('store');
      }.property(),

      searchQuery: function() {
        return Query.Search.create({
          sort: Em.A([{
            attr: 'name',
            dir: 'asc'
          }])
        });
      }.property(),

      itemComponent: 'am-user-self-menu-item'
    }),

    navController: function() {
      return this.NavController.create({
        parentController: this,
        selectedIdBinding: 'parentController.selectedItemId'
      });
    }.property(),

    // Enable, disable commands from list:
    // "C:\Program Files\Pole Position Software\LANrevServer\LANrev Server.exe" --ImportPreferences C:\Temp\SelfServicePortalPrivileges.plist
    isCommandEnabled: function (commandName, isComputer) {
      var isEnabled = false;

      if (commandName && typeof(isComputer) !== 'undefined') {
        // No localization is required
        var deviceTypeSearch = isComputer ? 'Desktop' : 'Mobile';

        this.get('selfServicePrivilegesStore.content').some(function (item) {
          if (item.DeviceType.trim() === deviceTypeSearch && item.Command.trim() === commandName) {
            isEnabled = item.Enabled;
            return true;
          }
        });
      }
      return isEnabled;
    },

    hasNoDevices: function () {
      return this.get('selfServicePrivilegesStore.content').length !== 0;
    }.property('selfServicePrivilegesStore.content')
  });
});
