define([
  'ember',
  'text!../templates/modal_action_layout.handlebars'
], function(
  Em,
  template
) {
  'use strict';

  return Em.Handlebars.compile(template);
});
