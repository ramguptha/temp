define([
  'ember'
], function(  
  Em
) {
  'use strict';

  // Modal Countdown Controller
  // ==========================
  //
  // Schedules the tick() function once per second when shown, stops when closed.

  return Em.Controller.extend({
    modalActionWindowClass: 'modal-action-window',

    tickWrapper: null,
    tickHandle: null,

    tick: Em.K,

    onShowModal: function() {
      this.start();
    },

    onCloseModal: function() {
      this.stop();
    },

    init: function() {
      var self = this;

      this.set('tickWrapper', function() {
        self.tick();
      });
    },

    start: function() {
      this.tick();
      this.set('tickHandle', window.setInterval(this.get('tickWrapper'), 1000));
    },

    stop: function() {
      // Stop the timeout checker
      window.clearInterval(this.get('tickHandle'));
      this.set('tickHandle', null);
    }
  });
});
