define([
  'ember',
  'desktop',
  'text!../templates/user_self_help_item_track_device.handlebars',
], function(
  Em,
  Desktop,
  template
  ) {
  return Em.View.extend({
    defaultTemplate: Em.Handlebars.compile(template),
    layout: Desktop.ModalActionLayoutTemplate
  });
});
