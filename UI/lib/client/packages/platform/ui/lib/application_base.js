define([
  'ember',
  'handlebars',

  'packages/platform/activity-monitor',

  './application_route',
  './controllers/menu_controller'
], function(
    Em,
    Handlebars,

    ActivityMonitorController,

    ApplicationRoute,
    MenuController
) {
  'use strict';

  // Namespace
  // =========
  //
  // Base class for Next Gen applications
  var App = Em.Application.extend({
    LOG_TRANSITIONS: true,
    LOG_TRANSITIONS_INTERNAL: true,
    LOG_VIEW_LOOKUPS: true,
    LOG_ACTIVE_GENERATION: true,
    LOG_BINDINGS: true,

    // If true, show all dates in UTC. Otherwise, show them in the timezone configured in the browser.
    isShowingUtc: false,

    // Resource strings
    translations: function() {
      return {};
    }.property(),

    // The name of the current locale
    locale: function() {
      var defaultLocale = (window.navigator.language || window.navigator.userLanguage).toLowerCase();
      return Em.get(this, 'requireJsConfig.config.i18n.locale') || defaultLocale;
    }.property(),

    layoutMetricsChangedAt: null,

    // The Require.js config
    requireJsConfig: function() {
      return window.requirejs.s.contexts._.config;
    }.property(),

    // If true, resource lookups will return the _keys_ instead of the _values_.
    isLocalizing: false,

    // Merge the provided package into the global App namespace
    mergePackage: function(pkg) {

      // Classes
      if (pkg.appClasses) {
        var appClasses = pkg.appClasses;

        // First, protect from collisions
        for (name in appClasses) {
          if (appClasses.hasOwnProperty(name) && this[name]) {
            throw (['Namespace collision!', this, pkg, name]).join(', ');
          }
        }

        // Then merge.
        this.reopen(appClasses);
      }

      // Actions
      if (pkg.appActions) {
        this.ApplicationRoute = this.ApplicationRoute.extend({
          actions: pkg.appActions
        });
      }

      // Strings
      if (pkg.appStrings) {
        var appStrings = pkg.appStrings;
        var translations = this.get('translations');

        // First, protect from collisions
        for (name in appStrings) {
          if (appStrings.hasOwnProperty(name) && translations[name]) {
            throw (['Namespace collision!', this, pkg, name]).join(', ');
          }
        }

        var merge = function(target, strings) {
          for (var key in strings) {
            var value = strings[key];

            if ('string' === typeof(value)) {
              target[key] = value;
            } else if ('object' === typeof(value)) {
              if (!target.ref) {
                target[key] = {};
                merge(target[key], value);
              }
            } else {
              throw ['Invalid resource', key, value];
            }
          }
        };

        // Then merge.
        merge(translations, appStrings);
      }

      // Initialization
      if (pkg.initialize) {
        pkg.initialize(this);
      }
    },

    ApplicationRoute: ApplicationRoute,

    NothingView: Em.View.extend({ defaultTemplate: Em.Handlebars.compile('') }),

    MenuController: MenuController,
    ActivityMonitorController: ActivityMonitorController
  });

  // Declare the media that we care about. (for ember-responsive)
  App.responsive({
    media: {
      phone:   '(max-width: 480px)',
      mobile:  '(min-width: 481px) and (max-width: 768px)',
      tablet:  '(min-width: 769px) and (max-width: 992px)',
      desktop: '(min-width: 993px) and (max-width: 1200px)',
      jumbo:   '(min-width: 1201px)'
    }
  });

  // Debug all the things
  // --------------------

  Em.run.backburner.DEBUG = true;

  return App;
});
