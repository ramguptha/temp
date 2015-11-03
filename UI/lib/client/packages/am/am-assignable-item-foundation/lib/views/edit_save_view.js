define([
  'ember',
  'desktop',
  'text!../templates/edit_save.handlebars'
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
