define([
  './application_base'
], function(
  ApplicationBase
) {
  'use strict';

  // Application
  // ===========
  //
  // Application class for apps which will completely control the DOM, such as Absolute Manager Web Admin.
  return ApplicationBase.extend({
    rootElement: 'body'
  });
});
