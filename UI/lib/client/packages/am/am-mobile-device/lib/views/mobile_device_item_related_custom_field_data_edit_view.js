define([
  'ember',
  'desktop',
  'ui',
  'text!../templates/mobile_device_item_related_custom_field_data_edit.handlebars'
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
