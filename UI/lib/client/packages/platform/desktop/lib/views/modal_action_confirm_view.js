define([
  'ember',
  'text!../templates/modal_action_confirm.handlebars',
  'text!../templates/modal_action_layout.handlebars',
  './keyboard_events_mixin'
], function(
  Em,
  modalActionConfirmTemplate,
  modalActionLayoutTemplate,
  KeyboardEventsMixin
) {
  return Em.View.extend(KeyboardEventsMixin, {
    defaultTemplate: Em.Handlebars.compile(modalActionConfirmTemplate),
    layout: Em.Handlebars.compile(modalActionLayoutTemplate),

    didInsertElement: function() {
      this._super();
      this.$('button.btn-action').focus();
    }
  });
});
