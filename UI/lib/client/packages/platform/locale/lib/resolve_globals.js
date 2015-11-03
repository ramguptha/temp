define([
  './resolve'
], function(
  resolve
) {
  'use strict';

  // resolveGlobals(path)
  // --------------------
  //
  // Wrapper function for resolve() that passes in App.translations and App.isLocalizing. If there is no App,
  // assume we are invoked from unit tests and behave as if App.isLocalizing === true.
  return function(path) {
    var app = window.App;
    return resolve(app ? app.get('translations') : null, path, app ? app.get('isLocalizing') : true);
  };
});
