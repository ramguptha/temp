define([
  'ember'
], function(
  Em
) {
  'use strict';

  // Child Controller
  // ================
  //
  // A child controller is a controller that is an aggregate of a parent controller. It has access to the 
  // parents target and controllers properties.

  return Em.Mixin.create({
    parentController: function() { throw 'required'; }.property(),

    target: Em.computed.oneWay('parentController.target'),
    controllers: Em.computed.oneWay('parentController.controllers')
  });
});
