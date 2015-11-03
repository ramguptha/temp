define([
  'ember',
  'packages/platform/date-type',
  'packages/platform/locale-config',
  'locale',
  'timepicker',
  '../localized_date_time_pickers'
], function(
  Em,
  DateType,
  LocaleConfig,
  Locale,
  $,
  LocalizedDateTimePickers
) {
  'use strict';

  // Base Class for jQuery UI DatePicker derived Views
  // =================================================

  return Em.TextField.extend({
    classNames: 'date-input-field',
    
    App: function() { return window.App; }.property(),

    // If true, render times in UTC, otherwise render browser timezone
    isShowingUtc: Em.computed.oneWay('App.isShowingUtc'),

    // Data is stored in UTC.
    valueAsDateInUtc: null,

    valueAsDateInViewTimezone: Em.computed('valueAsDateInUtc', 'isShowingUtc', {
      set: function(key, value) {
        // View UTC Offset => UTC
        var isShowingUtc = this.get('isShowingUtc');
        this.set('valueAsDateInUtc', isShowingUtc ? value : DateType.incrementByUtcOffset(value));
        return value;
      },
      get: function() {
        // UTC => View UTC Offset
        var isShowingUtc = this.get('isShowingUtc');
        var unadjustedValue = this.get('valueAsDateInUtc');
        return isShowingUtc ? unadjustedValue : DateType.decrementByUtcOffset(unadjustedValue);
      }
    }),

    // Setup / Teardown of the View
    // ----------------------------

    init: function() {
      this._super();

      var valueAsDateInViewTimezone = this.get('valueAsDateInViewTimezone');
      if (DateType.isValid(valueAsDateInViewTimezone)) {
        this.set('value', this.format(valueAsDateInViewTimezone));
      }

      // Observers
      this.getProperties('value valueAsDateInViewTimezone'.w());
    },

    didInsertElement: function() {
      var valueAsDateInViewTimezone = this.get('valueAsDateInViewTimezone');
      var options = {};

      if (DateType.isValid(valueAsDateInViewTimezone)) {
        valueAsDateInViewTimezone = this.offsetTimeForPicker(valueAsDateInViewTimezone);

        options = {
          defaultDate: valueAsDateInViewTimezone,
          hour: valueAsDateInViewTimezone.getHours(),
          minute: valueAsDateInViewTimezone.getMinutes(),
          second: valueAsDateInViewTimezone.getSeconds(),
          millisecond: valueAsDateInViewTimezone.getMilliseconds(),
          microsecond: 0
        };
      }

      // NOTE NOTE NOTE: We configure the picker to be in UTC, to have a baseline UTC offset to
      // work from. We do this because the picker implements local time in a flawed manner (e.g.
      // with broken daylight savings offsets).
      //
      // So! The picker thinks it is operating in UTC, but the times we present to it are really
      // in the view UTC offset. Once again, timezone transforms are performed by this view instead of 
      // the picker. The picker renders valueAsDateInViewTimezone.utc().
      this.setupPicker(LocalizedDateTimePickers.localize(options));
    },

    willDestroyElement: function() {
      this.teardownPicker();
    },

    // Setup / Teardown of the jQuery *picker widget
    // ---------------------------------------------

    setupPicker: function(options) {
      throw 'Implement me';
    },

    teardownPicker: function() {
      throw 'Implement me';
    },

    // Data Comparisons and Transforms
    // -------------------------------

    format: function(valueAsDate) {
      throw 'Implement me';
    },

    parse: function(valueAsString) {
      throw 'Implement me';
    },

    isValid: function(value) {
      return DateType.isValid(value);
    },

    valuesDiffer: function(lval, rval) {
      return (this.isValid(lval) && this.isValid(rval) && lval.getTime() !== rval.getTime()) ||
          (this.isValid(lval) !== this.isValid(rval));
    },

    offsetTimeForPicker: function(time) {
      // The timepicker thinks in local time rather than UTC, so present dates to it with the timezone adjusted
      // so that "local time" is really UTC.
      return DateType.isValid(time) ? new Date(time.getTime() + (time.getTimezoneOffset() * 1000 * 60)) : null;
    },

    // Observers
    // ---------

    updateValueOnTimeChange: function() {
      var value = this.get('value');
      var parsedValue = this.parse(value);
      var valueAsDateInViewTimezone = this.get('valueAsDateInViewTimezone');

      if (this.valuesDiffer(parsedValue, valueAsDateInViewTimezone)) {
        this.set('value', valueAsDateInViewTimezone);
        this.$().timepicker('setDate', this.offsetTimeForPicker(valueAsDateInViewTimezone));
      }
    }.observes('valueAsDateInViewTimezone'),

    updateTimeOnParsedValueChange: function() {
      var value = this.get('value');
      var parsedValue = this.parse(value);
      var valueAsDateInViewTimezone = this.get('valueAsDateInViewTimezone');

      if (this.valuesDiffer(parsedValue, valueAsDateInViewTimezone)) {
        this.set('valueAsDateInViewTimezone', parsedValue);
      }
    }.observes('value')
  });
});
