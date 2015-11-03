define([
  'ember',
  'desktop',
  'ui',
  'text!../templates/user_self_help_item_reset_tracking_passcode.handlebars'
], function(
  Em,
  Desktop,
  UI,
  template
  ) {
  return Em.View.extend({
    defaultTemplate: Em.Handlebars.compile(template),
    layout: Desktop.ModalActionLayoutTemplate

  });
});
