define([
  'ember',
  'desktop',
  'text!../templates/custom_field_item_add_edit.handlebars'
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
