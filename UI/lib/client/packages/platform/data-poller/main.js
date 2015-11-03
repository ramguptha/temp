define([
  'ember',
  'guid'
], function(
  Em,
  Guid
) {
  'use strict';

  // Data Poller
  // ===========
  //
  // Polls a DataStore on a given interval. Polling only runs when subscribers have declared themselves interested
  // via _subscribe()_.
  //
  // Subclasses must implement:
  // 
  // - pollingDelayInMilliseconds
  // - dataSource
  // - model
  // - acquire(lock, sucessCallback, errorCallback, force)

  var REQUIRED = function() {
    throw 'Implement me';
  };

  return Em.Object.extend({
    Guid: Guid,

    // Subscriptions
    // -------------
    //
    // Calling code declares its interest in the polled data by subscribing to it via _subscribe()_. Subscribing
    // forces a poll to immediately occur. Polling is maintained while there are subscribers.

    // When subscribers.length > 0, we poll.
    subscribers: function() {
      return Em.A();
    }.property(),

    subscribe: function(subscriber) {
      var subscribers = this.get('subscribers');

      if (subscribers.contains(subscriber)) {
        throw ['Subscribed with the same subscriber twice!', subscriber, subscribers];
      }

      subscribers.pushObject(subscriber);
      this.invokePoll();
    },

    unsubscribe: function(subscriber) {
      var subscribers = this.get('subscribers');

      if (!subscribers.contains(subscriber)) {
        throw ['Tried to unsubscribe with an unknown subscriber', subscriber, subscribers];
      }

      subscribers.removeObject(subscriber);
    },

    hasSubscriptions: function() {
      return this.get('subscribers.length') > 0;
    }.property('subscribers.length'),

    // Polling
    // -------

    scheduledPoll: null,

    // True if a poll is in flight
    pollInProgress: Em.computed.oneWay('dataSource.loadInProgress'),

    // Date of last successful poll
    lastPolledAt: Em.computed.oneWay('dataSource.loadedAt'),

    // If last poll failed, the error encountered
    lastPollError: Em.computed.oneWay('dataSource.error'),

    invokePoll: function() {
      var self = this;

      var pollEnded = function(dataSource) {
        self.set('scheduledPoll', null);
        self.maintainPolling();
      };

      if (!this.get('pollingInProgress')) {
        this.poll(pollEnded, pollEnded);
      }
    },

    maintainPolling: function() {
      if (this.get('hasSubscriptions')) {
        var scheduledPoll = this.get('scheduledPoll');

        if (scheduledPoll) {
          Em.run.cancel(scheduledPoll);
        }

        scheduledPoll = Em.run.later(
          this,
          function() {
            if (this.get('hasSubscriptions')) {
              this.invokePoll();
            }
          },
          this.get('pollingDelayInMilliseconds')
        );

        this.set('scheduledPoll', scheduledPoll);
      }
    },

    // This will usually be Em.computed.oneWay into some value in env.
    pollingDelayInMilliseconds: REQUIRED.property(),

    dataSource: REQUIRED.property(),
    lock: Guid.property(),

    poll: function(successCallback, errorCallback) {
      var dataSource = this.get('dataSource');
      if (dataSource) {
        dataSource.freshen(true, successCallback, errorCallback, this);
      }
    }
  });
});
