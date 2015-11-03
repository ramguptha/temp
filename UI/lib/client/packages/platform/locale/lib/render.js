define([
  'handlebars'
], function(
  Handlebars
) {
  'use strict';

  // render(template, context)
  // -------------------------
  //
  // Return a Handlebars SafeString of the template rendered with the context, or just the template if it's a string.
  return function(template, context) {
    var rendered = null;

    if ('function' === typeof(template)) {
      rendered = template(context);
    } else {
      rendered = template;
    }

    return new Em.Handlebars.SafeString(rendered);
  }
});
