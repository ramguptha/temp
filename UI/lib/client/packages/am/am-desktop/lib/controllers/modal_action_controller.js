define([
  'ember',
  'desktop'
], function(
  Em,
  Desktop
) {
  'use strict';

  // Modal Action Controller with close button
  return Desktop.ModalActionController.extend({
    actions: {
      close: function() {
        this.send('closeModal');
      }
    },

    displayClose: true
  });
});
