define([
  'ember'
], function(
  Em
) {
  'use strict';

  return Em.Object.create({
    invalidatedAt: null,

    invalidate: function() {
      this.set('invalidatedAt', new Date());
    }
  });
});
