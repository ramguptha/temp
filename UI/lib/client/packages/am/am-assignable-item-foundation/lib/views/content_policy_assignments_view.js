define([
  'ember',
  'desktop',
  'text!../templates/content_policy_assignments.handlebars'
], function(
  Em,
  Desktop,
  template
) {
  return Em.View.extend({
    defaultTemplate: Em.Handlebars.compile(template),
    layout: Desktop.ModalWizardLayoutTemplate
  });
});
