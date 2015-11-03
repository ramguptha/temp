define([
  'ember',
  './nav_page_view',
  './keyboard_events_mixin',
  'text!../templates/nav_content_page.handlebars'
], function(
  Em,
  NavPageView,
  KeyboardEvents,
  template
) {
  'use strict';

  return NavPageView.extend(KeyboardEvents, {
    defaultTemplate: Em.Handlebars.compile(template)
  });
});
