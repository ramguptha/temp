define([
  'ember',
  'text!../templates/action_details.handlebars'
], function(
  Em, template
) {
  return Em.View.extend({
    defaultTemplate: Em.Handlebars.compile(template)
  });
});
