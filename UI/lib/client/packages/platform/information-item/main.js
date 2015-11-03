define([
  'ember'
], function(
  Em
  ) {
  'use strict';

  // Information Item
  // ========
  // Information item type that is used by AM
  // This type is to use the same operators as numbers, but have special regex rules
  return Em.Object.extend({
    regex: null,
    type: null
  });
});