define([
  'ember',
  'packages/platform/desktop',
  'packages/platform/ajax',

  './namespace',

  'packages/am/am-user-self-help-portal',

  'packages/platform/logger'
], function(
  Em,
  Desktop,
  Ajax,

  App,

  AmUserSelfHelpDevice,

  logger
) {
  'use strict';

  App.Router.map(function() {
    this.route('mydevices');

    AmUserSelfHelpDevice.buildRoutes(this);
  });

  return;
});
