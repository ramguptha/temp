define([
  'ember',
  'desktop',
  'ui',
  'text!../templates/user_self_help_clear_passcode.handlebars'
], function(
  Em,
  Desktop,
  UI,
  clearPasscodeTemplate
  ) {
  return Em.View.extend({
    defaultTemplate: Em.Handlebars.compile(clearPasscodeTemplate),
    layout: Desktop.ModalWizardLayoutTemplate,

    didInsertElement: function() {
      if (!Em.isNone(this.$("#newPasscode"))) {
        UI.setFocus(this.$("#newPasscode"));
      }
    }
  });
});
