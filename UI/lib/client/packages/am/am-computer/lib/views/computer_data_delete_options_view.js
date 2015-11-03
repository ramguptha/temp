define([
  'ember',
  'desktop',
  'ui',

  'text!../templates/computer_data_delete_options.handlebars',
  '../templates/modal_device_freeze_data_delete_layout'
], function(
  Em,
  Desktop,
  UI,

  template,
  ModalDeviceLayout
  ) {
  return Em.View.extend({
    defaultTemplate: Em.Handlebars.compile(template),
    layout: ModalDeviceLayout

  });
});
