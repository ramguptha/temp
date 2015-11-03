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

  // DatePickerComponent
  // ===================
  //
  // Wrapper for jQuery UI datepicker.
  return DateTimePickerComponentBase.extend({

    date: boundAliasShim('valueAsDateInUtc'),
    tChooseDatePlaceholder: 'desktop.advancedFilterComponent.chooseDatePlaceholder'.tr(),

    placeholder: function() {
      return this.get('tChooseDatePlaceholder').toString();
    }.property(),
    
    // Setup / Teardown of the jQuery *picker widget
    // ---------------------------------------------

    setupPicker: function(options) {
      this.$().datepicker(options);
    },

    teardownPicker: function() {
      this.$().datepicker('destroy');
    },

    // Data Comparisons and Transforms
    // -------------------------------

    format: function(valueAsDate) {
      return Locale.date(valueAsDate);
    },

    parse: function(valueAsString) {
      return Locale.parseDate(valueAsString);
    }
  });
});
