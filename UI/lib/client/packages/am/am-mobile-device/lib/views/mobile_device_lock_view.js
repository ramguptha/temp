define([
  'ember',
  'desktop',

  'text!../templates/mobile_device_lock.handlebars'
], function(
  Em,
  Desktop,

  template
  ) {
  return Em.View.extend({
    defaultTemplate: Em.Handlebars.compile(template),
    layout: Desktop.ModalWizardLayoutTemplate
  });
});
