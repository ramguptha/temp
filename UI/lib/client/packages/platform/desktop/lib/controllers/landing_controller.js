define([
  'ember'
], function(
  Em
) {
  'use strict';

  return Em.Controller.extend({
    Package: function() { throw 'required'; }.property(),
    View: function() { throw 'required'; }.property(),

    path: function() {
      return { package: this.get('Package'), path: this.get('Package.topNavRouteMap').landingRelativePath };
    }.property()
  });
});
