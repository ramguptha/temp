define([
  'ember',
  'packages/platform/modal'
], function(
  Em,
  Modal
) {
  'use strict';

  // Breadcrumbs
  // -----------
  //
  // Include this Mixin to manage breadcrumbs on activate / deactivate
  var HasBreadcrumbs = Em.Mixin.create({

    // Inject our breadcrumbs into the application controller
    activateBreadcrumbs: function(controller) {
      this.controllerFor('application').activateBreadcrumbs(controller.get('breadcrumb'));
    },

    // Clear our breadcrumbs from the application controller
    deactivateBreadcrumbs: function() {
      this.controllerFor('application').deactivateBreadcrumbs();
    },

    // Sub-classes that override this method (as many will do) are expected to invoke activateBreadcrumbs()
    // directly instead of calling _super().
    setupController: function(controller, model) {
      this.activateBreadcrumbs(controller);
    },

    // Sub-classes that override this method (as many will do) are expected to invoke deactivateBreadcrumbs()
    // directly instead of calling _super().
    deactivate: function() {
      this.deactivateBreadcrumbs();
    }
  });

  // Route
  // =====
  //
  // Base class for all non-application routes.
  var Route = Em.Route.extend(Modal.HasShowModal, {

    activateBreadcrumbs: function(controller) {
      this.controllerFor('application').set('breadcrumbs', controller.get('breadcrumbs'));
    },

    // In general, models are queried in setupController(), so pass parameters on through.
    model: function(params) {
      return params;
    },

    // When showing modal content, we need to render into the applicationRoute, otherwise our modal content
    // will be torn down when routes become inactive.
    getApplicationRoute: function() {
      return window.App.__container__.lookup('route:application');
    }
  });

  return Route.reopenClass({
    HasBreadcrumbs: HasBreadcrumbs
  });
});
