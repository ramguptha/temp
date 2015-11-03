define([
  'locale',
  'packages/platform/bound-alias-shim',

  './date_time_picker_component_base'
], function(
  Locale,
  boundAliasShim,

  DateTimePickerComponentBase
) {
  'use strict';

  // TimePickerComponent
  // ===================
  //
  // Wrapper for Trent Richardson's timepicker extension for jQuery UI datepicker.
  return DateTimePickerComponentBase.extend({

    time: boundAliasShim('valueAsDateInUtc'),

    // Setup / Teardown of the jQuery *picker widget
    // ---------------------------------------------

    setupPicker: function(options) {
      this.$().timepicker(options);
    },

    teardownPicker: function() {
      this.$().timepicker('destroy');
    },

    // Data Comparisons and Transforms
    // -------------------------------

    format: function(valueAsDate) {
      return Locale.time(valueAsDate);
    },

    parse: function(valueAsString) {
      return Locale.parseTime(valueAsString);
    }
  });
});
