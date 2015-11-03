define([
  'ember',
  'locale'
], function(
  Em,
  Locale
) {
  'use strict';

  return Em.Controller.extend({
    modals: Em.inject.service(),

    visibleGlobalNavMenu: true,

    breadcrumbLanding: Em.Object.extend({
      title: 'shared.homeTitle'.tr(),
      path: 'landing'
    }).create(),

    breadcrumbs: null,

    breadcrumbTop: Em.computed.oneWay('breadcrumbs.firstObject'),

    breadcrumbsTail: function() {
      return Em.makeArray(this.get('breadcrumbs')).slice(1);
    }.property('breadcrumbs.[]'),

    activateBreadcrumbs: function(breadcrumbsMinusLanding) {
      var breadcrumbs = Em.copy(Em.makeArray(breadcrumbsMinusLanding.get('chain')));
      breadcrumbs.pushObject(this.get('breadcrumbLanding'));
      this.set('breadcrumbs', breadcrumbs);
    },

    deactivateBreadcrumbs: function() {
      this.set('breadcrumbs', null);
    },

    menuGroups: function() {
      return Em.A(this.get('packagesForContent')).map(function(pkg) {
        var routeMap = pkg.topNavSpec;

        var routeActions = Em.A(routeMap.routes).map(function(spec) {
          return Em.Object.extend({
            package: pkg,
            itemClass: spec.breadcrumbButtonClassName,
            path: spec.path,
            callBack: spec.callBack,
            context: spec.context,
            name: spec.name
          }).create();
        });

        // Default the landing action to that of the first route, unless 'noLandingAction' is
        // explictly specified
        var landingAction = routeActions[0];

        if (!Em.isNone(routeMap.noLandingAction) && routeMap.noLandingAction) {
          landingAction = null;
        }

        return Em.Object.extend({
          name: routeMap.name,
          mainMenuIcon: 'main-menu-icon',
          iconClassName: routeMap.iconClassName,
          landingAction: landingAction,
          actions: routeActions
        }).create();
      });
    }.property('packagesForContent.[]'),

    // Array of packages.
    //
    // e.g. given define(['packages/am/am-mobile-device'], function(AmMobileDevice) { ... });
    // use packagesForContent: [AmMobileDevice]
    packagesForContent: []
  });
});
