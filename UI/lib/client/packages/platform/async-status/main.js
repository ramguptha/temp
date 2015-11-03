define([
  'ember'
], function(
  Em
) {
  'use strict';

  // Async Status
  // ============
  //
  // Describes the status of an asynchronous process.

  var State = {
    INVOKED: 'INVOKED',
    LOADED: 'LOADED',
    CANCELLED: 'CANCELLED',
    FAILED: 'FAILED'
  };

  return Em.Object.extend({
    context: null,
    requests: function() {
      return [];
    }.property(),

    state: null,

    invokedAt: null,
    loadedAt: null,
    cancelledAt: null,
    failedAt: null,

    lastError: null,

    getTime: function(name) {
      var time = null;
      var date = this.get(name);

      if (date) {
        time = date.getTime();
      }

      return time;
    },

    // Invoked!

    isInvoked: Em.computed.equal('state', State.INVOKED),

    setInvoked: function() {
      this.setProperties({
        invokedAt: new Date(),
        state: State.INVOKED
      });

      return this;
    },

    // Cancelled!

    isCancelled: Em.computed.equal('state', State.CANCELLED),

    setCancelled: function() {
      this.setProperties({
        invokedAt: new Date(),
        state: State.CANCELLED
      });

      return this;
    },

    // Failed!

    isFailed: Em.computed.equal('state', State.FAILED),

    setFailed: function(error) {
      this.setProperties({
        invokedAt: new Date(),
        state: State.FAILED,
        lastError: error
      });

      return this;
    },

    // Loaded!

    isLoaded: Em.computed.equal('state', State.LOADED),

    setLoaded: function() {
      this.setProperties({
        invokedAt: new Date(),
        state: State.LOADED
      });

      return this;
    }
  });
});
