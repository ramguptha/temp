define([
  'ember',
  'packages/platform/desktop',
  'packages/platform/ajax',

  './namespace',

  'packages/am/am-assignable-item',
  'packages/am/am-mobile-device',
  'packages/am/am-mobile-policy',
  'packages/am/am-mobile-command',
  'packages/am/am-custom-field',
  'packages/am/am-computer'
], function(
  Em,
  Desktop,
  Ajax,

  App,

  AmAssignableItem,
  AmMobileDevice,
  AmMobilePolicy,
  AmMobileCommand,
  AmCustomField,
  AmComputer
) {
  'use strict';

  App.Router.map(function() {
    this.route('landing', { path: '/' });

    AmMobileDevice.buildRoutes(this);
    AmMobilePolicy.buildRoutes(this);
    AmAssignableItem.buildRoutes(this);
    AmMobileCommand.buildRoutes(this);
    AmCustomField.buildRoutes(this);
    AmComputer.buildRoutes(this);
  });
});
