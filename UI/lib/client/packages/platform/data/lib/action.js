define([
  'ember',
  'packages/platform/ajax',
  './action_history',
  'logger'
], function(
  Em,
  Ajax,
  ActionHistory,
  logger
) {
  'use strict';

  return Em.StateManager.extend({
    Ajax: Ajax,
    ActionHistory: ActionHistory,

    // From this.dataStores, the path to dependent data sources which will be invalidated once the ajax query is finished.
    dependentDataStoreNames: Em.A(),

    // Describes the action. Potentially user-visible.
    description: null,

    // Base URL for all api invocations
    apiBase: function() {
      return '/api/';
    }.property(),

    // End point to invoke
    endPoint: function() {
      throw 'Implement me';
    }.property(),

    endPointUrl: function() {
      return this.get('apiBase') + this.get('endPoint');
    }.property('apiBase', 'endPoint'),

    // Describes the completion status of the job
    percentComplete: 0,

    // HTTP verb to use when invoking the endPoint
    verb: 'post',

    // Handle for the "in-flight" AJAX request
    jqXHR: null,

    // Dependent data stores are looked up in this namespace
    dataStores: function() {
      throw 'Implement me';
    }.property(),

    // Wait this long before invalidating dependent data stores
    refreshDelay: 0,

    initialState: 'preInvokeState',

    status: function() {
      return this.get('currentState.name').replace('State', '');
    }.property('currentState'),

    preInvokeState: Em.State.create({}),

    invoke: function() {
      this.transitionTo('inProgressState');
      this.ActionHistory.push(this);
    },

    // Ember statemachines take *instances* instead of classes for their statechart, which makes sharing
    // code problematic. Work around this by putting shared code in the parent state for child states to invoke.
    onInProgressStateEnter: function(onEndPointAjaxSuccess, onEndPointAjaxError) {
      var logDebug = true;
      var self = this;

      // Do ajax invocation
      var ns = this.getProperties(
        'Ajax endPointUrl description endPoint verb body onSuccess onFail'.w()
      );
      this.set('jqXHR', ns.Ajax[ns.verb].apply(ns.Ajax, [
        ns.description,
        ns.endPointUrl,
        'application/json; charset=UTF-8',
        JSON.stringify(this),
        function success(rsp) {
          onEndPointAjaxSuccess.call(self, rsp);
        },
        function error(errorDetail) {
          // Fixme: this code is a temporary hack to workaround the the situations that Response header contains:
          // content-type: json, content-length: 0
          // The error would be: 200 or 201 Unexpected End of Input
          // This was causing issue on Software Catalog, but after migration to Java, this code would not be needed
          var contentLength = errorDetail.jqXHR.getResponseHeader('content-length');
          var status = errorDetail.jqXHR.status;

          if (errorDetail.get('isParserError') && (status === 200 || status === 201) && parseInt(contentLength) === 0) {
            onEndPointAjaxSuccess.call(self, null);
          } else {
            onEndPointAjaxError.call(self, errorDetail);
          }
        }
      ]));
    },

    // Ember statemachines take *instances* instead of classes for their statechart, which makes sharing
    // code problematic. Work around this by putting shared code in the parent state for child states to invoke.
    onEndPointAjaxSuccess: function(rsp) {
      this.transitionTo('succeededState');
      this.set('percentComplete', 100);
      this.get('onSuccess').call(this, rsp);
    },

    // Ember statemachines take *instances* instead of classes for their statechart, which makes sharing
    // code problematic. Work around this by putting shared code in the parent state for child states to invoke.
    onEndPointAjaxError: function(errorDetail) {
      this.transitionTo('failedState');
      this.get('onError').call(this, errorDetail);
    },

    // Ember statemachines take *instances* instead of classes for their statechart, which makes sharing
    // code problematic. Work around this by putting shared code in the parent state for child states to invoke.
    onInProgressStateExit: function() {
      var self = this;

      var invalidate = function() {
        self.get('dependentDataStoreNames').forEach(function(name) {
          self.get('dataStores').get(name).invalidate();
        });
      };

      // Invalidate dependent data sources, but wait a little first to allow
      // server data to update
      var refreshDelay = this.get('refreshDelay');
      if (refreshDelay > 0) {
        Em.run.later(this, invalidate, refreshDelay);
      } else {
        Em.run.next(invalidate);
      }
    },

    inProgressState: Em.State.create({
      enter: function() {
        var parentState = this.get('parentState');
        parentState.onInProgressStateEnter(parentState.onEndPointAjaxSuccess, parentState.onEndPointAjaxError);
      },

      exit: function() {
        this.get('parentState').onInProgressStateExit();
      }
    }),

    toJSON: function() {
      throw 'Implement me';
    },

    failedState: Em.State.create(),

    onError: Em.K,

    succeededState: Em.State.create(),

    onSuccess: Em.K,

    error500Details: 'The server could have timed out or is too busy, click "Try Again" to retry' +
      'or "Cancel" to  return to the admin Console. Please contact your System Administrator' +
      'if this issue continues'
  });
});
