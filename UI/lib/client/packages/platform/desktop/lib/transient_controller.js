define([
  'ember'
], function(
  Em
) {
  'use strict';

  // Chiefly for use with ModalLayer, which can notify controllers that they should clean up by setting 'shuttingDown'
  return Em.Mixin.create({
    targetShuttingDownDidChange: function() {
      if (this.get('target.shuttingDown') && this.shutdown) {
        this.shutdown();
      }
    }.observes('target.shuttingDown')
  });
});
