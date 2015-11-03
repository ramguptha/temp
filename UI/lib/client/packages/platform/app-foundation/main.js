define([
  './lib/views/application_view',
  './lib/views/embedded_application_view'
], function(
  ApplicationView,
  EmbeddedApplicationView
) {
  'use strict';

  // App Foundation
  // ==============
  //
  // Core classes for building applications.

  return {
    ApplicationView: ApplicationView,
    EmbeddedApplicationView: EmbeddedApplicationView
  };
});
