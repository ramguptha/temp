define([
  'ember'
], function(
  Em
) {
  'use strict';

  return Em.Controller.extend({
    actions: {
      cancel: function() {
        this.didCancel();
        this.send('closeModal');
      }
    },

    title: 'Error',
    message: 'An unspecified error has occurred.',
    details: null,
    resolution: null,

    showCancelButton: Em.computed.bool('closeTarget'),

    // Optional properties for the behaviour model:
    //
    // - closeTarget: if set, will send "close" on cancel.

    didCancel: function() {
      var closeTarget = this.get('closeTarget');
      if (closeTarget) {
        closeTarget.send('close');
      }
    }
  });
});
