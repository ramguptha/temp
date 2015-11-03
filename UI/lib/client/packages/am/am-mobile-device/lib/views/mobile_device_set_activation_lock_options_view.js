define([
  'ember',
  'desktop',
  'text!../templates/mobile_device_set_activation_lock_options.handlebars',
], function (
  Em,
  Desktop,
  RetryAll
  ) {
  return Em.View.extend({
    defaultTemplate: Em.Handlebars.compile(RetryAll),
    layout: Desktop.ModalActionLayoutTemplate
  });
});
