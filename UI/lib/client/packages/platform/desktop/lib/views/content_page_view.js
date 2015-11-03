define([
  'ember',
  './page_view',
  'text!../templates/content_page.handlebars'
], function(
  Em,
  PageView,
  template
) {
  'use strict';

  return PageView.extend({ 
    defaultTemplate: Em.Handlebars.compile(template)
  });
});
