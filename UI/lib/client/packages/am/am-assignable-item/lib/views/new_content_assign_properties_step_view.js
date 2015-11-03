define([
  'ember',
  'desktop',
  'ui',
  'text!../templates/new_content_assign_properties_step.handlebars'
], function(
  Em,
  Desktop,
  UI,
  template
) {
  return Em.View.extend({
    defaultTemplate: Em.Handlebars.compile(template),
    layout: Desktop.ModalWizardLayoutTemplate,

    didInsertElement: function() {
      var controller = this.get('controller');

      UI.setFocus(this.$("#customCategory"));
      this.$('#customCategory').autocomplete({
        source: controller.getBuiltInCategories()
      });
    }
  });
});
