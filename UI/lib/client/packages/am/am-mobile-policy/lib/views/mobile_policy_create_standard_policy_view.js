define([
  'ember',
  'desktop',
  'text!../templates/mobile_policy_set_policy_name.handlebars'
], function(
  Em,
  Desktop,
  SetNameTemplate
  ) {
  return Em.View.extend(Desktop.KeyboardEventsMixin,{
      defaultTemplate: Em.Handlebars.compile(SetNameTemplate),
      layout: Desktop.ModalWizardLayoutTemplate
  });
});
