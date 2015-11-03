define([
  'ember',
  'desktop',
  'ui',

  'text!../templates/action_item_base_modal.handlebars'
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
      this._super();

      UI.setFocus(this.$("#actionName"));
    }
  });
});
