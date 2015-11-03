define([
  'jquery',
  'ember',

  './lib/application',
  './lib/embedded_application',
  './lib/application_route',

  './lib/route',
  './lib/no_setup_route',

  './lib/controllers/application_controller',
  './lib/controllers/menu_controller',

  './lib/views/features_error_view',

  './lib/breadcrumb'
], function(
  $,
  Em,

  Application,
  EmbeddedApplication,
  ApplicationRoute,

  Route,
  NoSetupRoute,

  ApplicationController,
  MenuController,

  FeaturesErrorView,

  Breadcrumb
) {
  'use strict';

  // UI - User Interface Core
  // ========================

  return {

    Route: Route,
    NoSetupRoute: NoSetupRoute,

    // Utility
    // -------

    requiredProperty: function() { throw 'Implement me'; }.property(),

    // Application Base Classes
    // ------------------------

    Application: Application,
    EmbeddedApplication: EmbeddedApplication,
    ApplicationRoute: ApplicationRoute,

    // Controller Base Classes
    // -----------------------

    ApplicationController: ApplicationController,
    Controller: Em.Controller,
    ObjectController: Em.ObjectController,

    // Everything Else
    // ---------------

    FeaturesErrorView: FeaturesErrorView,
    Breadcrumb: Breadcrumb,
    MenuController: MenuController,

    // Legacy Ember
    // ------------

    Module: Em.Object.extend({
      name: null,

      rootPath: null,
      rootContext: null,

      initialize: function(App, router) {
        // TODO
      },

      absolutePath: function(relativePath) {
        var rootPath = this.get('rootPath');
        var path = rootPath + '.' + relativePath;
        return path;
      },

      transitionToPath: function(router, relativePath, relativeContext) {
        var contexts = Array.prototype.slice.call(arguments, 2);
        var path = this.absolutePath(relativePath);

        var args = Em.A([path]).concat(contexts);

        router.transitionTo.apply(router, args);
      },

      transitionToLanding: function(router) {
        throw 'TODO';
      },

      transitionToItem: function(router, id) {
        throw 'TODO';
      }
    }),

    BreadcrumbDisabled: Em.Object.extend({
      module: null,
      title: null,
      relativePath: null,
      relativeContext: null,
      icon: null,
      path: function() {
        return this.get('module').absolutePath(this.get('relativePath'));
      }.property()
    }),

    setFocus: function(el) {
      if (Em.isEmpty(el.val())) {
        if (navigator.userAgent.indexOf('MSIE ') === -1) {  // IE focus() nukes the placeholder text, so skip
          el.focus();
        }
      }
      else {
        el.focus().val(el.val());
      }
    }
  };
});

