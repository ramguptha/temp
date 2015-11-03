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

    date: boundAliasShim('valueAsDateInUtc'),

    tChooseDateAndTimePlaceholder: 'desktop.advancedFilterComponent.chooseDateAndTimePlaceholder'.tr(),

    placeholder: function() {
      return this.get('tChooseDateAndTimePlaceholder').toString();
    }.property(),

    // Setup / Teardown of the jQuery *picker widget
    // ---------------------------------------------

    setupPicker: function(options) {
      this.$().datetimepicker(options);
    },

    teardownPicker: function() {
      this.$().datetimepicker('destroy');
    },

    // Data Comparisons and Transforms
    // -------------------------------

    format: function(valueAsDate) {
      return Locale.dateTime(valueAsDate);
    },

    parse: function(valueAsString) {
      return Locale.parseDateTime(valueAsString);
    }
  });
});
