define([
  'packages/platform/ajax',
  'ui',
  'packages/am/am-data'
], function(
  Ajax,
  UI,
  AmData
) {
  'use strict';

  // Turn the debugging off for this application
  var app = UI.Application.create({
    LOG_TRANSITIONS: false,
    LOG_TRANSITIONS_INTERNAL: false,
    LOG_VIEW_LOOKUPS: false,
    LOG_ACTIVE_GENERATION: false,
    LOG_BINDINGS: false
  });

  // Defer startup until later (presumably, when the routing is ready).
  app.deferReadiness();

  return app;
});
