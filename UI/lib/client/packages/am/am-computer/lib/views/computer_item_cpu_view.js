define([
  'ember',
  'text!../templates/computer_item_cpu.handlebars'
], function(
  Em,
  template
  ) {
  return Em.View.extend({
    defaultTemplate: Em.Handlebars.compile(template)
  });
});