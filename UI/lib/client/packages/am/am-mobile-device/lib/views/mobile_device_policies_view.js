define([
  'ember',
  'text!../templates/mobile_device_policies.handlebars'
], function(Em, template) {
  return Em.View.extend({ defaultTemplate: Em.Handlebars.compile(template) });
});
