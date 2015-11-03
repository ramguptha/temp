define([
  './countdown_controller'
], function(
  CountdownController
) {
  'use strict';

  // Session Renewal Warning Controller
  // ==================================

  return CountdownController.extend({
    secsToExpiry: null, 
    secsToRetry: null,

    tick: function() {
      var secsToExpiry = this.getSecsToExpiry();
      this.set('secsToExpiry', secsToExpiry >= 0 ? secsToExpiry : null);

      var secsToRetry = this.getSecsToRetry();
      this.set('secsToRetry', secsToRetry >= 0 ? secsToRetry : null);
    },

    // For Override by sub-classes: get the number of seconds left until the session expires
    getSecsToExpiry: function() {
      throw 'Implement me';
    },

    getSecsToRetry: function() {
      throw 'Implement me';
    }
  });
});
