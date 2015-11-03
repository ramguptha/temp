define([
  'ember'
], function(
  Em
) {
  'use strict';

  // Has Show Modal
  // --------------
  //
  // A mixin for routes.

  return Em.Mixin.create({
    modals: Em.inject.service(),

    // Options for showModal:
    //
    // - name: the controller for the modal content, and, if viewName is not specified, the view as well.
    // - viewName: name of the view class
    // - model: model instance to hand to controller
    showModal: function(options) {
      options.controller = this.controllerFor(options.name);
      options.viewName = options.viewName || options.name;

      this.get('modals').showModal(options);
    },

    // Options for ensureModal:
    //
    // - name: the controller for the modal content, and, if viewName is not specified, the view as well.
    // - viewName: name of the view class
    // - model: model instance to hand to controller
    ensureModal: function(options) {
      options.controller = this.controllerFor(options.name);
      options.viewName = options.viewName || options.name;

      var modals = this.get('modals');
      if (!modals.isShowingModal(options)) {
        modals.showModal(options);
      }
    }
  });
});
