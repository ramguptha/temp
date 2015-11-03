define([
  'ember',

  'text!../templates/mobile_device_user.handlebars'
], function (
  Em,

  template
) {
  return Em.View.extend({
    defaultTemplate: Em.Handlebars.compile(template)
  });
});
