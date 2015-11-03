define([
  'ember',
  'packages/platform/ajax',
  'packages/platform/data',
  './namespace',
  'packages/am/am-push-notifications'
], function(
  Em,
  Ajax,
  AbsData,
  AmData,
  AmPushNotifications
) {
  'use strict';

  return AbsData.get('Action').extend({
    dataStores: function() {
      return AmData.get('stores');
    }.property(),

    apiBase: function() {
      return AmData.get('urlRoot') + '/api/';
    }.property(),

    jobId: null,
    jobStatusPollInterval: 500,

    progressUpdateCallback: null,
    progressUpdateContext: null,
    loginPathRedirectOnError: '../login/?sessionTimedOut=true',

    onEndPointAjaxError: function(errorDetail) {
      if( errorDetail.jqXHR.status === 401 ) {
        document.location = this.get('loginPathRedirectOnError');
      } else {
        this._super(errorDetail);
      }
    },

    onEndPointAjaxSuccess: function(rsp) {
      // Handle batch requests which return a jobid that we then use to poll for the status (percent complete)
      if (!Em.isNone(rsp) && !Em.isNone(rsp.jobId)) {
        this.set('jobId', rsp.jobId);
        Em.run.later(this, this.getJobStatus, this.get('jobStatusPollInterval'));
      } else {
        this._super();
      }
    },

    jobStatusUrl: function() {
      return this.get('apiBase') + 'job/' + this.get('jobId') + '/status';
    }.property('apiBase', 'jobId'),

    onInProgressStateExit: function() {
      var self = this;

      var invalidate = function() {
        self.get('dependentDataStoreNames').forEach(function(name) {
          var dataStoresDataSources = self.get('dataStores').get(name).dataSourcesByOwner, hasPushEndpoint = false;

          for (var owner in dataStoresDataSources) {
            if (dataStoresDataSources.hasOwnProperty(owner) && !Em.isEmpty(dataStoresDataSources[owner].pushEndpoint) ) {
              hasPushEndpoint = true;
              break;
            }
          }

          if( !AmPushNotifications.get('isSocketActive') || !hasPushEndpoint ) {
            self.get('dataStores').get(name).invalidate();
          }

        });
      };

      // Invalidate dependent data sources, but wait a little first to allow
      // server data to update
      var refreshDelay = this.get('refreshDelay');
      if (refreshDelay > 0) {
        Em.run.later(this, invalidate, refreshDelay);
      } else {
        invalidate.call(this);
      }
    },

    getJobStatus: function() {
      var self = this;

      Ajax.get(
        'Job Status',
        this.get('jobStatusUrl'),
        {},
        function(rsp) {
          var percentComplete = rsp.percentComplete;
          if (percentComplete == 100) {
            self.transitionTo('succeededState');
            self.get('onSuccess')(rsp);
          } else {
            var progressUpdateCallback = self.get('progressUpdateCallback');
            if (typeof progressUpdateCallback === 'function') {
              progressUpdateCallback.call(self.get('progressUpdateContext'), percentComplete);
            }

            // Adjust the progress status polling interval if necessary
            if (percentComplete - self.get('percentComplete') < 1) {
              var pollInterval = self.get('jobStatusPollInterval') * 2;
              if (pollInterval > 8000) {
                pollInterval = 8000;
              }
              self.set('jobStatusPollInterval', pollInterval);
            }
            self.set('percentComplete', percentComplete);

            Em.run.later(self, self.getJobStatus, self.get('jobStatusPollInterval'));
          }
        },
        function(jqXHR, textStatus, errorThrown) {
          self.transitionTo('failedState');
          self.get('onError')(jqXHR, errorThrown, textStatus);
        }
      );
    }
  });
});
