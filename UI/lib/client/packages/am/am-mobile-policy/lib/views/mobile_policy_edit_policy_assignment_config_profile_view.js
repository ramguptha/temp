define([
  'ember',
  'desktop',
  'ui',
  'text!../templates/config_profile_edit_policy_assignment.handlebars'
], function(
  Em,
  Desktop,
  UI,
  template
  ) {
  return Em.View.extend({
    defaultTemplate: Em.Handlebars.compile(template),
    layout: Desktop.ModalWizardLayoutTemplate
  });
});