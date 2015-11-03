define([
  'ember',
  'packages/platform/desktop',
  'text!../templates/mobile_device_set_enrollment_user.handlebars',
], function (
  Em,
  Desktop,
  setDeviceEnrollmentUser
  ) {
  return Em.View.extend({
    template: Em.Handlebars.compile(setDeviceEnrollmentUser),
    layout: Desktop.ModalWizardLayoutTemplate
  });
});
