define([
  'ember',
  'desktop',
  'text!../templates/mobile_device_update_info.handlebars',
], function(
  Em,
  Desktop,
  updateDeviceInfoTemplate
  ) {
  return Em.View.extend({
    defaultTemplate: Em.Handlebars.compile(updateDeviceInfoTemplate),
    layout: Desktop.ModalWizardLayoutTemplate
  });
});
