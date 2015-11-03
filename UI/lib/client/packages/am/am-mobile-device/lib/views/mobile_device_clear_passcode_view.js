define([
  'ember',
  'desktop',

  'text!../templates/mobile_device_clear_passcode.handlebars'
], function(
  Em,
  Desktop,

  clearPasscodeTemplate
  ) {
  return Em.View.extend({
    defaultTemplate: Em.Handlebars.compile(clearPasscodeTemplate),
    layout: Desktop.ModalWizardLayoutTemplate
  });
});
