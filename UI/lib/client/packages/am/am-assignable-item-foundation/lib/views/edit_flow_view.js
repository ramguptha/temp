define([
  'ember',
  'desktop',
  'text!../templates/edit_flow.handlebars'
], function(
  Em,
  Desktop,
  template
) {
  return Em.View.extend({
    defaultTemplate: Em.Handlebars.compile(template)
  });
});
