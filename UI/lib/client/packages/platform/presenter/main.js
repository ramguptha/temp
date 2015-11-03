define([
  'ember'
], function(
  Em
) {
  'use strict';

  // Presenter
  // =========
  //
  // Responsible for transforming a data field and its metadata to a DOM-ready format.

  return Em.Object.extend({
    // Required
    name: null,

    // Label
    renderLabel: function() {
      return this.get('name');
    },

    // Value
    renderValue: function(content) {
      return Em.get(content, this.get('name'));
    },

    // Metrics
    width: 100
  });
});
