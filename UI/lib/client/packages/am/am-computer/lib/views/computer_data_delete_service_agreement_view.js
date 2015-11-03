define([
  'ember',
  'desktop',

  'text!../templates/computer_data_delete_service_agreement.handlebars'
], function(
  Em,
  Desktop,

  template
  ) {
  'use strict';

  return Em.View.extend({
    defaultTemplate: Em.Handlebars.compile(template),
    layout: Desktop.ModalGenericLayout
  });
});
