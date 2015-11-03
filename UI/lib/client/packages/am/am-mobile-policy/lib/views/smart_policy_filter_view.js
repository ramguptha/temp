define([
  'ember',
  'text!../templates/smart_policy_filter.handlebars'
], function(
  Em,
  template
  ) {
  'use strict';

  return Em.View.extend({
    defaultTemplate: Em.Handlebars.compile(template)
  });
});
