define([
  'ember',
  'desktop',
  'text!../templates/action_policy_assignment.handlebars'
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
