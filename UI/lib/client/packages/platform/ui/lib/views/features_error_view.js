define([
  'ember',
  'text!../templates/features_error.handlebars'
], function(
  Em,
  template
) {
  'use strict';

  return Em.View.extend({
    defaultTemplate: Em.Handlebars.compile(template)
  });
});
