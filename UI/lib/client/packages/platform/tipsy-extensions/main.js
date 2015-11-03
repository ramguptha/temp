define([
  'tipsy'
], function(
  $
) {
  'use strict';

  // Override the default elementOptions behavior to set the gravity based on the class attribute of the 
  // related element. A class name of "tooltip-<direction abbreviation>" will set the corresponding gravity.
  $.fn.tipsy.elementOptions = function(element, options) {
    (element.className || '').split(/\s+/).forEach(function(className) {
      var matches = className.match(/tooltip-(.*)/);
      if (matches) {
        options = $.extend({}, options, { gravity: matches[1] });
      }
    });
    return options;
  };

  $.fn.releaseTipsyData = function() {
    this.removeData('tipsy');
  };

  return $;
});
