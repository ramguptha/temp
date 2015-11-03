define([
  'ember',
  'desktop',
  'text!../templates/mobile_device_set_device_name.handlebars'
], function (
  Em,
  Desktop,
  SetDeviceNameTemplate
  ) {
  return Em.View.extend({
    defaultTemplate: Em.Handlebars.compile(SetDeviceNameTemplate),
    layout: Desktop.ModalWizardLayoutTemplate
  });
});
