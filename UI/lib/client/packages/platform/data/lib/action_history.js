define([
  'ember'
], function(
  Em
) {
  'use strict';

  return Em.Object.create({
    all: Em.A(),

    push: function(action) {
      this.get('all').pushObject(action);
    }
  });
});
