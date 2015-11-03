define([
  'ember',
  'desktop',
  'text!../templates/user_self_help_item_remote_erase.handlebars',
], function(
  Em,
  Desktop,
  remoteEraseTemplate
  ) {
  return Em.View.extend({
    defaultTemplate: Em.Handlebars.compile(remoteEraseTemplate),
    layout: Desktop.ModalWizardLayoutTemplate
  });
});
