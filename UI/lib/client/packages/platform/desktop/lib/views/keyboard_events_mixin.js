define([
  'ember',
  'packages/platform/activity-monitor'
], function(
  Em,
  ActivityMonitor
) {
  'use strict';

  return Em.Mixin.create({
    keyDown: function(e) {
      var code = e.which; // recommended to use e.which, it's normalized across browsers
      if(code==13) {
        e.preventDefault();
        if(this.$('div.modal-wizard-body').length !== 0) {
          //this is a wizard
          this.$('div.modal-wizard-body button.btn-action').click();
        }
        else {
          //this is just a action popup
          this.$('div.modal-action-body button.btn-action').click();
        }
        ActivityMonitor.stopAndNote(e);
      }
    }
  });
});
