define([
  'ember',
  './has_nav_tabs',
  'text!../templates/tab_content_layout.handlebars'
], function(
  Em,
  HasNavTabs,
  template
) {
  'use strict';

  return Em.View.extend(HasNavTabs, {
    defaultTemplate: Em.Handlebars.compile(template.replace('{{yield}}', '{{outlet}}'))
  });
});
