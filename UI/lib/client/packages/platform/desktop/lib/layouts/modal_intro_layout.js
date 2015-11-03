define([
  'ember',
  'text!../templates/modal_intro_layout.handlebars'
], function(
    Em,
    template
    ) {
  'use strict';

  return Em.Handlebars.compile(template);
});
