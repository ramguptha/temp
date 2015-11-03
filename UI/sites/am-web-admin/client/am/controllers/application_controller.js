define([
  'jquery',
  'ember',
  'packages/platform/ui',
  'packages/platform/desktop',

  'packages/am/am-mobile-device',
  'packages/am/am-mobile-policy',
  'packages/am/am-mobile-command',
  'packages/am/am-assignable-item',
  'packages/am/am-session',
  'packages/am/am-custom-field',
  'packages/am/am-computer',

  'logger'
], function(
  $,
  Em,
  UI,
  Desktop,

  AmMobileDevice,
  AmMobilePolicy,
  AmMobileCommand,
  AmAssignableItem,
  AmSession,
  AmCustomField,
  AmComputer,

  logger
) {
  return UI.ApplicationController.extend(UI.MenuController.HasMenus, {
    menus: UI.MenuController.HasMenus.registerMenuNames('globalNav trail user'.w()),

    showingGlobalNavMenu: UI.MenuController.HasMenus.monitorMenu('globalNav'),
    showingTrailMenu: UI.MenuController.HasMenus.monitorMenu('trail'),
    showingUserMenu: UI.MenuController.HasMenus.monitorMenu('user'),

    packagesForContent: [AmComputer, AmMobileDevice, AmMobilePolicy, AmAssignableItem, AmCustomField, AmMobileCommand],

    listItemView: Desktop.NavListItemView,

    serverVersion: function() {
      return AmSession.getServerVersion();
    }.property(),

    webAPIVersion: function() {
      return AmSession.getWebAPIVersion();
    }.property(),

    userName: function() {
      return AmSession.getUserName();
    }.property()
  });
});
