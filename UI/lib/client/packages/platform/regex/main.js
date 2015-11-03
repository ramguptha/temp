define([], function() {
  'use strict';

  // Regex
  // =====
  //
  // Utility methods for regular expressions.
  return {
    esc: function(str) {
      return String(str).replace(/([-()\[\]{}+?*.$\^|,:#<!\\])/g, '\\$1').replace(/\x08/g, '\\x08');
    }
  };
});
