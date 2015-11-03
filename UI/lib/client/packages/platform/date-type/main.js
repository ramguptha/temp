define(function() {
  'use strict';

  // DateType
  // ========
  //
  // This module encapsulates core logic related to Dates and Times.

  var MONDAY = 'mon';
  var TUESDAY = 'tue';
  var WEDNESDAY = 'wed';
  var THURSDAY = 'thu';
  var FRIDAY = 'fri';
  var SATURDAY = 'sat';
  var SUNDAY = 'sun';

  var DAYS_IN_ORDER = [ MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY ];

  return {

    // Date Constants
    // --------------

    MONDAY: MONDAY,
    TUESDAY: TUESDAY,
    WEDNESDAY: WEDNESDAY,
    THURSDAY: THURSDAY,
    FRIDAY: FRIDAY,
    SATURDAY: SATURDAY,
    SUNDAY: SUNDAY,

    DAYS_IN_ORDER: DAYS_IN_ORDER,

    // isValid(value)
    // --------------
    //
    // Returns true if value is a valid Date instance.
    isValid: function(value) {
      if (!(value instanceof Date)) {
        return false;
      }

      var time = value.getTime();
      return Boolean(!isNaN(time));
    },

    // dayNumber(day)
    // --------------
    //
    // Given a constant representing a day, return its ordinal value according to DAYS_IN_ORDER, or -1 if not found.
    dayNumber: function(day) {
      return DAYS_IN_ORDER.indexOf(day);
    },

    // transformUtcOffset(value, fromUtcOffsetInHours, toUtcOffsetInHours)
    // -------------------------------------------------------------------
    //
    // Returns value unmodified if !isValid(value), otherwise returns a new Date with the time offset from
    // fromUtcOffsetInHours to toUtcOffsetInHours.
    transformUtcOffset: function(value, fromUtcOffsetInHours, toUtcOffsetInHours) {
      if (!this.isValid(value)) {
        return value;
      }

      var totalOffsetInMilliseconds = (fromUtcOffsetInHours - toUtcOffsetInHours) * 60 * 60 * 1000;

      return new Date(value.getTime() + totalOffsetInMilliseconds);
    },

    // incrementByUtcOffset(value)
    // ---------------------------
    //
    // Returns value unmodified if !isValue(value), otherwise returns a new Date which is getTimezoneOffset()
    // minutes into the future from the provided one.
    incrementByUtcOffset: function(value) {
      if (!this.isValid(value)) {
        return value;
      }

      var totalOffsetInMilliseconds = value.getTimezoneOffset() * 60 * 1000;

      return new Date(value.getTime() + totalOffsetInMilliseconds);
    },

    // decrementByUtcOffset(value)
    // ---------------------------
    //
    // Returns value unmodified if !isValue(value), otherwise returns a new Date which is getTimezoneOffset()
    // minutes into the past from the provided one.
    decrementByUtcOffset: function(value) {
      if (!this.isValid(value)) {
        return value;
      }

      var totalOffsetInMilliseconds = -value.getTimezoneOffset() * 60 * 1000;

      return new Date(value.getTime() + totalOffsetInMilliseconds);
    }
  };
});
