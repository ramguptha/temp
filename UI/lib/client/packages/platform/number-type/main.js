define(function() {
  'use strict';

  return {
    isValid: function(value) {
      return Boolean('number' === typeof(value) && !isNaN(value));
    }
  };
});
