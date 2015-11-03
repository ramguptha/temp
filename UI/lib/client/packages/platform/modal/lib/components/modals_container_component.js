define([
  'ember',
  'text!../templates/modals_container.handlebars'
], function(
  Em,
  modalsContainerTemplate
) {
  'use strict';

  // Modal Container
  // ===============
  //
  // All modals are rendered within the modals container.
  //
  // Injectable properties:
  //
  // - modals: An instance of ModalService
  return Em.Component.extend({
    layout: Em.Handlebars.compile(modalsContainerTemplate)
  });
});
