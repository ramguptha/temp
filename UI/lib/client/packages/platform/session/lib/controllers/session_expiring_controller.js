define([
  './countdown_controller'
], function(
  CountdownController
) {
  'use strict';

  // Session Expiring Controller
  // ===========================

  return CountdownController.extend({
    actions: {
      refreshSession: function() {
        // close the modal, then allow action to bubble to the application for handling.
        this.send('closeModal');

        return true;
      }
    },

    modalActionWindowClass: 'modal-action-window',

    secsToExpiry: null, 

    tick: function() {
      var secsToExpiry = this.getSecsToExpiry();

      if (secsToExpiry > 0) {
        this.set('secsToExpiry', secsToExpiry);
      }
    },

    // For Override by sub-classes: get the number of seconds left until the session expires
    getSecsToExpiry: function() {
      throw 'Implement me';
    }
  });
});
