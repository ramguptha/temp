define(function() {
  'use strict';

  // boundAliasShim(name)
  // ==================================================================
  //
  // Work-around of alias properties not working properly with components in Ember 1.13.5
  //
  // See https://github.com/emberjs/ember.js/issues/12069

  return function(name) {
    return Em.computed(name, {
      get: function(key) {
        return this.get(name);
      },

      set: function(key, value) {
        this.set(name, value);
        return value;
      }
    });
  };
});
