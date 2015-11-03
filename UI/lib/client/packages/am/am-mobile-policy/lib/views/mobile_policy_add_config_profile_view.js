define([
  'ember',
  'desktop',
  'text!../templates/mobile_policy_add_config_profile.handlebars'
], function(
  Em,
  Desktop,
  addConfigProfileTemplate
  ) {
  return Em.View.extend({
    defaultTemplate: Em.Handlebars.compile(addConfigProfileTemplate),
    layout: Desktop.ModalWizardLayoutTemplate
  });
});