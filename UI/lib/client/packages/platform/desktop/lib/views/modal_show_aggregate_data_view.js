define([
  'ember',

  '../layouts/modal_generic_layout',
  'text!../templates/modal_show_aggregate_data.handlebars'
], function(
  Em,

  ModalGenericLayout,
  template
) {
  'use strict';

  return Em.View.extend({
    defaultTemplate: Em.Handlebars.compile(template),
    layout: ModalGenericLayout,

    didInsertElement: function() {
      this.$('button.btn-action').focus();
    },

    // TODO We should think of a better way to remove the active class from the selected td in the
    // listview in the layer underneath
    willDestroyElement: function() {
      $('td.active').removeClass('active');
    }
  });
});
