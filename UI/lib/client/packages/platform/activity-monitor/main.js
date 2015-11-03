define([
  'ember',
  'packages/platform/send-ember-action'
], function(
  Em,
  sendEmberAction
) {
  'use strict';

  // Activity Monitor
  // ================
  //
  // Notes the timestamp of the latest click or keypress.

  return Em.Controller.extend({
    lastUserActivityAt: null,
    lastSessionActivityAt: null,

    touchUser: function() {
      var now = new Date();
      this.trace('touched user', now);
      this.set('lastUserActivityAt', now);
    },

    touchSession: function() {
      var now = new Date();
      this.trace('touched session', now);
      this.set('lastSessionActivityAt', now);
    },

    tracing: false,
    trace: function() {
      if (this.get('tracing')) {
        console.log.apply(console, arguments);
      }
    }
  }).reopenClass({

    // We consider clicks and keypresses to be user activity. Usually they are caught by listeners on top
    // level elements, but if stopPropagation() is invoked, the top level listeners won't receive the events.
    //
    // So! When stopping click and keypress events, do it via this function.
    stopAndNote: function(evt) {
      evt.stopPropagation();
      sendEmberAction('noteUserActivity');
    }
  });
});
