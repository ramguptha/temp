define([
  'ember',
  'desktop',
  'ui',
  'text!../templates/computer_data_delete_request.handlebars',
  '../templates/modal_device_freeze_data_delete_layout'
], function(
  Em,
  Desktop,
  UI,
  template,
  ModalDeviceFreezeLayout
  ) {
  return Em.View.extend({
    defaultTemplate: Em.Handlebars.compile(template),
    layout: ModalDeviceFreezeLayout,

    didInsertElement: function() {
      UI.setFocus(this.$(".password-name-field"));
    }

  });
});
