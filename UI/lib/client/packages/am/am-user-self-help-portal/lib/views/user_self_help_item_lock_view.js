define([
  'ember',
  'desktop',
  'text!../templates/user_self_help_item_lock.handlebars'
], function(
  Em,
  Desktop,
  deviceLockTemplate
  ) {
  return Em.View.extend({
    defaultTemplate: Em.Handlebars.compile(deviceLockTemplate),
    layout: Desktop.ModalWizardLayoutTemplate
  });
});
