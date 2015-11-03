define([
  'ember',
  'desktop',
  'ui',
  'text!../templates/user_self_help_item_send_message.handlebars'
], function(
  Em,
  Desktop,
  UI,
  sendMessageTemplate
  ) {
  return Em.View.extend({
    defaultTemplate: Em.Handlebars.compile(sendMessageTemplate),
    layout: Desktop.ModalWizardLayoutTemplate,

    didInsertElement: function() {
      UI.setFocus(this.$("#enterMessage"));
      var controller = this.get('context');

      this.$("#enterMessage").bind('paste', function(e) {
        var elem = $(this);

        setTimeout(function() {
          controller.set('message', elem.val());
        }, 100);
      });
    }
  });
});
