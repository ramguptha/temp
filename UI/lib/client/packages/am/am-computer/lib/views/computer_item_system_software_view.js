define([
  'ember',
  'text!../templates/computer_item_system_software.handlebars'
], function(
  Em,
  template
  ) {
  return Em.View.extend({
    defaultTemplate: Em.Handlebars.compile(template)
  });
});