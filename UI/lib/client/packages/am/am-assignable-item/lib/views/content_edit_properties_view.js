define([
  'ember',
  'desktop',
  'ui',

  'text!../templates/content_edit_properties.handlebars'
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

      UI.setFocus(this.$("#contentDisplayName"));
      this.$('#customCategory').autocomplete({
        source: controller.getBuiltInCategories()
      });
    }
  });
});
