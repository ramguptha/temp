define([
  'ember',
  'desktop',
  'text!../templates/computer_gather_inventory.handlebars'
], function(
  Em,
  Desktop,
  gatherInventoryTemplate
  ) {
  return Em.View.extend({
    defaultTemplate: Em.Handlebars.compile(gatherInventoryTemplate),
    layout: Desktop.ModalWizardLayoutTemplate
  });
});
