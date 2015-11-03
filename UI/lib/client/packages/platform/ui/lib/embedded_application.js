define([
  './application_base'
], function(
  ApplicationBase
) {
  'use strict';

  // EmbeddedApplication
  // ===================
  //
  // Application class for apps to be embedded within a region in a web page, such as Customer Center
  return ApplicationBase.extend({
    rootElement: '#ngApplication'
  });
});
