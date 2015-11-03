define([
  'ember',
  'text!../templates/content_details.handlebars'
], function(
  Em, template
) {
  return Em.View.extend({
    defaultTemplate: Em.Handlebars.compile(template)
  });
});
