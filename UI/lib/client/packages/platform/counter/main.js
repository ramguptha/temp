define([
  'ember',
  'packages/platform/number-type',
  'packages/platform/async-status'
], function(
  Em,
  NumberType,
  AsyncStatus
) {
  'use strict';

  // Counter
  // =======
  //
  // General purpose asynchronous counting interface.

  // Count Status
  // ------------

  var CountStatus = AsyncStatus.extend();

  // Counter
  // -------

  var Counter = Em.Object.extend({
    CountStatus: CountStatus,

    // The count!
    total: null,

    hasTotal: function() {
      return !Em.isNone(this.get('total'));
    }.property('total'),

    // Set to when the total was last set.
    countedAt: null,

    // If set, will automatically invoke count() after the configured time _and_ will continue to do so
    // whenever reset.
    autoCountDelayInMilliseconds: null,

    // Em.run.later() handle for a future scheduled count
    scheduledCount: null,

    init: function() {
      // Observers
      var delay = this.get('autoCountDelayInMilliseconds');

      // Observers, and schedule a count, if necessary
      if (!this.get('paused')) {
        this.scheduleCount(delay);
      }
    },

    reset: function() {
      this.cancelCounting();
      this.setProperties({
        total: null,
        loadStatus: null
      });

      if (!this.get('paused')) {
        this.scheduleCount(this.get('autoCountDelayInMilliseconds'));
      }
    },

    autoCountDelayInMillisecondsDidChange: function() {
      this.scheduleCountUnlessFailed(this.get('autoCountDelayInMilliseconds'));
    }.observes('autoCountDelayInMilliseconds'),

    scheduleCount: function(delayInMilliseconds) {
      Em.run.cancel(this.get('scheduledCount'));
      
      if (NumberType.isValid(delayInMilliseconds)) {
        this.set('scheduledCount', Em.run.later(this, this.count, delayInMilliseconds));
      }
    },

    scheduleCountUnlessFailed: function(delayInMilliseconds) {
      if (!this.get('loadStatus.isFailed')) {
        this.scheduleCount(delayInMilliseconds);
      }
    },

    // Start an asynchronous count.
    count: function(context, successCallback, cancelCallback, errorCallback) {
      var self = this;

      if (self.get('loadStatus.isCancelled')) {
        return;
      }

      var loadStatus = self.get('loadStatus');
      if (!loadStatus) {
        loadStatus = self.CountStatus.create();
        self.set('loadStatus', loadStatus);
      }

      loadStatus.set('context', context);
      loadStatus.get('requests').pushObject({
        successCallback: successCallback,
        cancelCallback: cancelCallback,
        errorCallback: errorCallback
      });

      var countSuccessCallback = function(count) {
        if (loadStatus.get('isCancelled')) {
          return;
        }

        self.setProperties({
          total: count,
          countedAt: new Date()
        });
        loadStatus.setLoaded();

        loadStatus.get('requests').forEach(function(request) {
          if (request.successCallback) {
            request.successCallback.call(self, loadStatus.get('context'), count);
          }
        });

        self.set('loadStatus', null);
      };

      var countErrorCallback = function(detail) {
        if (loadStatus.get('isCancelled')) {
          return;
        }

        loadStatus.setFailed(detail);

        loadStatus.get('requests').forEach(function(request) {
          if (request.errorCallback) {
            request.errorCallback.call(self, loadStatus.get('context'), detail);
          }
        });
      };

      if (!loadStatus.get('isInvoked')) {
        this.getCount(loadStatus.get('context'), countSuccessCallback, countErrorCallback);
        loadStatus.setInvoked();
      }

      return this;
    },

    loadStatus: null,

    // Cancel all counting in progress.
    cancelCounting: function() {
      var self = this;
      var loadStatus = self.get('loadStatus');

      if (!loadStatus) {
        return;
      }

      var loadStatusRequests = loadStatus.get('requests');
      var pendingRequests = loadStatusRequests.slice(0);
      loadStatusRequests.clear();

      pendingRequests.forEach(function(request) {
        if (request.cancelCallback) {
          request.cancelCallback.call(self, loadStatus.get('context'));
        }
      });

      loadStatus.setCancelled();

      return this;
    },

    // Sub-classes implement asynchronous counting by overriding _getCount()_. 
    //
    // _getCount()_ is invoked from _count()_ to retrieve the requested data. On success, _getCount()_ is
    // expected to invoke the _successCallback(context, count)_, and on failure _getCount()_ is expected to invoke
    // _errorCallback(context, data)_.

    getCount: function(context, successCallback, errorCallback) {
      throw 'Implement me';
    },

    // Pausing / Resuming Count
    // ------------------------

    paused: false,

    // When paused becomes false, resume loading.
    pausedDidChange: function() {
      if (!this.get('paused')) {
        this.scheduleCountUnlessFailed(this.get('autoCountDelayInMilliseconds'));
      } else {
        this.cancelCounting();
      }
    }.observes('paused')
  });

  return Counter.reopenClass({
    CountStatus: CountStatus
  });
});
