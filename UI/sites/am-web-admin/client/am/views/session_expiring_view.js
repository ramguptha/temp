define([
  'ember',
  'desktop',
  'text!../templates/session_expiring.handlebars'
], function(
  Em,
  Desktop,
  sessionExpiryTemplate
) {
  return Em.View.extend({
    template: Em.Handlebars.compile(sessionExpiryTemplate),
    layout: Desktop.get('ModalActionLayoutTemplate')
  });
});
