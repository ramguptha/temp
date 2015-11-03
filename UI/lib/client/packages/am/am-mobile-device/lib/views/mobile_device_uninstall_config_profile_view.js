define([
  'ember',
  'desktop',
  'ui',
  'text!../templates/mobile_device_uninstall_config_profile.handlebars',
], function(
  Em,
  Desktop,
  UI,
  uninstallConfigProfileTemplate
  ) {
  return Em.View.extend({
    defaultTemplate: Em.Handlebars.compile(uninstallConfigProfileTemplate),
    layout: Desktop.ModalActionLayoutTemplate
  });
});
