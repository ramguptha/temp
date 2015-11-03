define([
  'ember',
  'text!../templates/tab_content_layout.handlebars'
], function(
  Em,
  template
) {
  'use strict';

  return Em.Handlebars.compile(template);
});
