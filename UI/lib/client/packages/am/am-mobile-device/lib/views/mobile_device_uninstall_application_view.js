define([
  'ember',
  'desktop',
  'ui',
  'text!../templates/mobile_device_uninstall_application.handlebars'
], function(
  Em,
  Desktop,
  UI,
  uninstallApplicationTemplate
  ) {
  return Em.View.extend({
    defaultTemplate: Em.Handlebars.compile(uninstallApplicationTemplate),
    layout: Desktop.ModalActionLayoutTemplate
  });
});
