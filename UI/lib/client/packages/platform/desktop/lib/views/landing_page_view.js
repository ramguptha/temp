define([
  'jquery',
  'ember',
  './page_view',
  'text!../templates/landing_page.handlebars'
], function(
  $,
  Em, 
  Page,
  template
) {
  'use strict';

  // TODO: Use a better way to change global styling. 
  return Page.extend({
    defaultTemplate: Em.Handlebars.compile(template),

    didInsertElement: function() {
      $('body').addClass('landing');
    },

    willDestroyElement: function() {
      $('body').removeClass('landing');
    }
  });
});
