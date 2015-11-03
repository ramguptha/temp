define([
  'ember',
  'text!../templates/computer_item_about_computer.handlebars'
], function(
  Em,
  template
  ) {
  return Em.View.extend({
    defaultTemplate: Em.Handlebars.compile(template)
  });
});