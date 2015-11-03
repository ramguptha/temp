define([
  'ember',
  'text!../templates/modal_generic_layout.handlebars'
], function(
    Em,
    template
    ) {
  'use strict';

  return Em.Handlebars.compile(template);
});
