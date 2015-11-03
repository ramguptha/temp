define([
  'ember',
  './lib/modals_service',
  './lib/has_show_modal',
  './lib/components/modals_container_component'
], function(
  Em,
  ModalsService,
  HasShowModal,
  ModalsContainerComponent
) {
  'use strict';

  return {
    appClasses: {
      ModalsService: ModalsService,
      ModalsContainerComponent: ModalsContainerComponent
    },

    HasShowModal: HasShowModal
  };
});
