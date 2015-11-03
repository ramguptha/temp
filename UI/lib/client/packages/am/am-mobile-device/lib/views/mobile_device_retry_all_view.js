define([
  'ember',
  'desktop',
  'text!../templates/mobile_device_retry_all.handlebars'
], function (
  Em,
  Desktop,
  RetryAll
  ) {
  return Em.View.extend({
    defaultTemplate: Em.Handlebars.compile(RetryAll),
    layout: Desktop.ModalWizardLayoutTemplate
  });
});
