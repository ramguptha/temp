define([
  'ember',
  'tipsy',
  'text!../templates/breadcrumbs.handlebars'
], function(
    Em,
    $,
    template
    ) {
  'use strict';

  return Em.View.extend({
    defaultTemplate: Em.Handlebars.compile(template)
  });
});
