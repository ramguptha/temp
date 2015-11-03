define([
  'ember',
  'desktop',
  'ui',
  'text!../templates/mobile_policy_add_in_house_app.handlebars'
], function (
  Em,
  Desktop,
  UI,
  addInHouseAppTemplate
  ) {
  return Em.View.extend({
    defaultTemplate: Em.Handlebars.compile(addInHouseAppTemplate),
    layout: Desktop.ModalWizardLayoutTemplate
  });
});