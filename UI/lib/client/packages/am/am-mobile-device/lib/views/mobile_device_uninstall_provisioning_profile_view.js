define([
  'ember',
  'desktop',
  'ui',
  'text!../templates/mobile_device_uninstall_provisioning_profile.handlebars',
], function(
  Em,
  Desktop,
  UI,
  UninstallProvProfileTemplate
  ) {
  return Em.View.extend({
      defaultTemplate: Em.Handlebars.compile(UninstallProvProfileTemplate),
    layout: Desktop.ModalActionLayoutTemplate
  });
});
