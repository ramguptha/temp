define([
  'ember',
  'help',
  'packages/am/am-assignable-item',
  'packages/am/am-mobile-policy',
  'packages/am/am-mobile-device',
  'packages/am/am-computer'
], function(
  Em,
  Help,
  AmAssignableItem,
  AmMobilePolicy,
  AmMobileDevice,
  AmComputer
  ) {
  'use strict';

  return Em.Controller.extend({
    systemMessages: [
      Em.Object.create({
        title: 'Welcome to Absolute',
        body: new Em.Handlebars.SafeString('<p>If this is your first time using Absolute Manage on the web, please consider taking a quick tour of the system. <a href="#" class="tour-cta-button">Take the tour</a></p>')
      })
    ],

    urlForHelp: Help.uri(1000),

    content: Em.A([AmComputer, AmMobileDevice, AmMobilePolicy, AmAssignableItem]).map(function(pkg) {
      var spec = pkg.topNavSpec;
      var route = pkg.topNavSpec.routes[0];

      return Em.Object.extend({
        package: pkg,
        icon: spec.landingIcon,
        name: spec.name,
        landingButtonClassName: spec.landingButtonClassName,
        path: route.path,
        context: route.context,
        callBack: route.callBack
      }).create();
    })
  });
});
