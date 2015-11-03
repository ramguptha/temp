define([
  'ember'
], function(
  Em
) {
  'use strict';

  // Offset Monitor
  // ==============
  //
  // Use this class to monitor the offset of a DOM element. Instantiate it with a callback in _offsetDidChange_ and
  // a context to invoke it under in _offsetDidChangeContext_. Start monitoring with _monitor(element)_ and stop 
  // monitoring with _stop()_.
  return Em.Object.extend({

    // Element to monitor
    element: null,

    // Offset at the beginning of monitoring
    startOffset: null,

    // Returned by Em.run.later()
    scheduledCheck: null,

    // Callback invoked when the position changes
    offsetDidChange: Em.K,

    // Invoke offsetDidChange with the following context
    offsetDidChangeContext: null,

    monitor: function(element) {
      if (element !== this.get('element')) {
        this.setProperties({
          element: element,
          startOffset: Em.$(element).offset()
        });
      }

      if (!this.get('scheduledCheck')) {
        this.schedule();
      }
    },

    schedule: function() {
      this.set('scheduledCheck', Em.run.later(this, this.checkOffset, 100));
    },

    stop: function() {
      var scheduledCheck = this.get('scheduledCheck');
      if (scheduledCheck) {
        Em.run.cancel(scheduledCheck);

        this.setProperties({
          element: null,
          startOffset: null,
          scheduledCheck: null
        });
      }
    },

    checkOffset: function() {
      if (!this.get('isDestroyed') && !this.get('isDestroying')) {
        var element = this.get('element');

        if (element) {
          var startOffset = this.get('startOffset');
          var currentOffset = element.offset();

          if (
            !currentOffset || (currentOffset.top !== startOffset.top) || (currentOffset.left !== startOffset.left)
          ) {
            this.offsetDidChange.call(this.get('offsetDidChangeContext'));
          }

          this.schedule();
        } else {
          // Options are no longer visible - stop monitoring until next time.
          this.stop();
        }
      }
    }
  });
});
