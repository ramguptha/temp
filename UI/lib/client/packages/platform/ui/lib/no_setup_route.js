define([
  './route'
], function(
  Route
) {
  'use strict';

  // No Setup Route
  // ==============
  //
  // A route that does not load a model.
  return Route.extend({ setupController: Em.K });
});
