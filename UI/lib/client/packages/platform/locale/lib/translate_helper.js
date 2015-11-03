define([
  'handlebars',
  'ember-core',
  './resolve_globals'
], function(
  Handlebars,
  Em,
  resolveGlobals
) {
  'use strict';

  return Em.Helper.extend({
    compute: function(params, data) {
      var path = params[0];
      var template = resolveGlobals(path);

      if ('function' === typeof(template)) {
        return new Em.Handlebars.SafeString(template(data));
      } else {
        return new Em.Handlebars.SafeString(template);
      }
    },

    recomputeWhenIsLocalizingChanges: function() {
      this.get('App.isLocalizing');
      this.recompute();
    }.observes('App.isLocalizing'),

    init: function() {
      this.get('App.isLocalizing');
    }
  });
});
