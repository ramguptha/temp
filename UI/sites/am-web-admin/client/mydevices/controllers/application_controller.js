define([
  'jquery',
  'ember',
  'packages/platform/ui',
  'packages/platform/desktop',

  'packages/am/am-session',

  'logger'
], function(
  $,
  Em,
  UI,
  Desktop,

  AmSession,

  logger
) {
  return UI.ApplicationController.extend(UI.MenuController.HasMenus, {
    menus: UI.MenuController.HasMenus.registerMenuNames('user'.w()),

    visibleGlobalNavMenu: false,

    showingGlobalNavMenu: null,
    showingUserMenu: UI.MenuController.HasMenus.monitorMenu('user'),

    serverVersion: function() {
      return AmSession.getServerVersion();
    }.property(),

    webAPIVersion: function() {
      return AmSession.getWebAPIVersion();
    }.property(),

    userName: function() {
      return AmSession.getUserName();
    }.property(),

    isSelfServicePortal: function() {
      return AmSession.isSelfServicePortal();
    }.property()

  });
});
