define([
  'ember',
  'desktop',
  'ui',
  'text!../templates/mobile_policy_add_third_party_app.handlebars'
], function (
  Em,
  Desktop,
  UI,
  addThirdPartyTemplate
  ) {
  return Em.View.extend({
    defaultTemplate: Em.Handlebars.compile(addThirdPartyTemplate),
    layout: Desktop.ModalWizardLayoutTemplate,
    didInsertElement: function() {
      // it's not possible to style this via imported CSS only since the grid displays data via two tables:
      // 'paged-table frozen' ( for check marks ) and 'paged-table free' ( for the data )
      var style = Em.$('<style>.paged-table th, .paged-table td { height: 35px; }</style>');
      this.set('cssAddedToHead', Em.$('html > head').append(style));
    },
    willDestroyElement: function() {
      // remove all the styles in head so that we don't populate the DOM every time this modal is opened
      // may break things if we start putting other styles in head
      Em.$(Em.$('head style').get(0)).remove();
    }
  });
});