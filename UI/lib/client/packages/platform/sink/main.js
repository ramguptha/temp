define([
  'ember'
], function(
  Em
) {
  'use strict';

  // Sink
  // ====
  //
  // A sink is a MutableArray which is always empty. Adding items has no effect. It is used as a default (class)
  // value for optional bindable properties which are also MutableArrays. Also implements _clear()_.

  return Em.Object.extend(Em.MutableEnumerable, {
    length: 0,

    nextObject: function(index, previousObject, context) {
      // Empty!
      return undefined;
    },

    addObject: function(obj) {
      // Do nothing!
    },

    removeObject: function(obj) {
      // Do nothing!
    },

    clear: function() {
      // Do nothing!
    }
  });
});
