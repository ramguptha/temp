define([
  'ember-core',
  'packages/platform/locale-config',
  'packages/platform/number-type',
  'packages/platform/date-type',

  './lib/resolve',
  './lib/resolve_globals',
  './lib/render',
  './lib/render_globals',
  './lib/translated',

  './lib/translate_helper',

  'i18n!./nls/strings'
], function(
  Em,
  LocaleConfig,
  NumberType,
  DateType,

  resolve,
  resolveGlobals,
  render,
  renderGlobals,
  translated,

  THelper,

  strings
) {
  'use strict';

  // AbsLocale
  // =========
  //
  // This package implements core localization support. It includes some extensions to core javascript
  // objects (String.tr()), and so is intended to be included before any user-level ember modules are defined.

  // String.tr(*dependentPropertyNames)
  // ----------------------------------
  //
  // Returns an Ember getter property that returns a SafeString based on the path specified in the string.
  // If dependentPropertyNames are specified, the getter will have them as dependencies and pass them by
  // name to the Handlebars template.
  var translate = function() {
    var path = this;

    var deps = ['App.isLocalizing'];
    var propertyNames = [];

    for (var i = 0; i < arguments.length; i++) {
      deps.push(arguments[i]);
      propertyNames.push(arguments[i]);
    }

    var propertyFunction = function() {
      var resource = resolveGlobals(path);
      var properties = this.getProperties(propertyNames);
      return render(resource, properties);
    };

    return propertyFunction.property.apply(propertyFunction, deps);
  };

  window.String.prototype.tr = translate;

  // notAvailable()
  // --------------
  //
  // Return string value for null values
  var notAvailable = function() {
    return render(resolveGlobals('shared.baseline'));
  };

  // formatMoment(value, format)
  // ---------------------------
  //
  // Implements all date/time formatters. Returns value, formatted by Moment with the given format.
  var formatMoment = function(value, format) {
    if (DateType.isValid(value)) {
      return LocaleConfig.momentUtc(value).format(format);
    } else if (Em.isNone(value)) {
      return notAvailable();
    } else throw ['Date formatter expects a numeric type or none', value];
  };

  // formatMomentLocal(value, format)
  // ---------------------------
  // Version for local time
  // Implements all date/time formatters. Returns value, formatted by Moment with the given format.
  var formatMomentLocal = function(value, format) {
    if (DateType.isValid(value)) {
      return LocaleConfig.momentLocal(value).format(format);
    } else if (Em.isNone(value)) {
      return notAvailable();
    } else throw ['Date formatter expects a numeric type or none', value];
  };


  // parseMoment(value, format)
  // --------------------------
  //
  // Implements all date/time parsers. Returns value as Date, parsed by Moment with the given format.
  var parseMoment = function(value, format) {
    if (DateType.isValid(value)) {
      // Really a date already
      return value;
    } else if (Em.isEmpty(value)) {
      return null;
    } else if ('string' === typeof(value)) {
      try {
        var parsed = LocaleConfig.momentUtc(value, format).toDate();
        return false === parsed ? null : parsed;
      } catch (e) {
        return null;
      }
    } else {
      throw ['Number parser expects a number, string or none', value];
    }
  };

  var namespace = {};

  return Em.merge(namespace, {

    // Globally shared appStrings
    appStrings: strings,

    appClasses: {
      THelper: THelper,
      TAHelper: THelper,

      TimeHelper: Em.Helper.helper(function(params, hash) {
        var value = params[0];
        return namespace.time(value);
      }),
      DateHelper: Em.Helper.helper(function(params, hash) {
        var value = params[0];
        return namespace.date(value);
      }),
      DatetimeHelper: Em.Helper.helper(function(params, hash) {
        var value = params[0];
        return namespace.dateTime(value);
      }),
    },

    resolve: resolve,
    resolveGlobals: resolveGlobals,
    render: render,
    renderGlobals: renderGlobals,

    translated: translated,

    notAvailable: notAvailable,

    // boolean(value)
    // --------------
    //
    // Format a boolean value.
    boolean: function(value) {
      if ('boolean' === typeof(value)) {
        return value ? render(resolveGlobals('shared.true')) : render(resolveGlobals('shared.false'));
      } else if (Em.isNone(value)) {
        return notAvailable();
      } else throw ['Boolean formatter expects a boolean type or none', value];
    },

    // currency(value)
    // ---------------
    //
    // Format a number as currency.
    currency: function(value) {
      if (NumberType.isValid(value)) {
        return LocaleConfig.jsWorldMonetaryFormatter.format(value);
      } else if (Em.isNone(value)) {
        return notAvailable();
      } else throw ['Currency formatter expects a numeric type or none', value];
    },

    // number(value)
    // -------------
    //
    // Format a number.
    number: function(value) {
      if (NumberType.isValid(value)) {
        return LocaleConfig.jsWorldNumericFormatter.format(value);
      } else if (Em.isNone(value)) {
        return notAvailable();
      } else throw ['Number formatter expects a numeric type or none', value];
    },

    // ordinal(value)
    // --------------
    //
    // Return N as Nth.
    ordinal: function(value) {
      if (NumberType.isValid(value)) {
        return LocaleConfig.momentUtc().localeData().ordinal(value);
      } else if (Em.isNone(value)) {
        return notAvailable();
      } else throw ['Number formatter expects a numeric type or none', value];
    },

    // parseNumber(value)
    // ------------------
    //
    // Parse a number. Returns null on parse failure.
    parseNumber: function(value) {
      if (NumberType.isValid(value)) {
        // Really a number already
        return value;
      } else if ('string' === typeof(value)) {
        try {
          return LocaleConfig.jsWorldNumericParser.parse(value);
        } catch (e) {
          return null;
        }
      } else if (Em.isNone(value)) {
        return null;
      } else {
        throw ['Number parser expects a number, string or none', value];
      }
    },

    // date(value)
    // -----------
    //
    // Format a date (no timestamp).
    date: function(value) {
      return formatMoment(value, 'll');
    },

    // date(value)
    // -----------
    //
    // Format a date (no timestamp). Local time.
    dateLocal: function(value) {
      return formatMomentLocal(value, 'll');
    },

    // time(value)
    // -----------
    //
    // Format a time.
    time: function(value) {
      return formatMoment(value, 'LT');
    },

    // timeLocal(value)
    // ----------------
    //
    // Format a local time.
    timeLocal: function(value) {
      return formatMomentLocal(value, 'LT');
    },

    // dateTime(value)
    // ---------------
    //
    // Format a date and timestamp.
    dateTime: function(value) {
      return formatMoment(value, 'lll');
    },

    // dateTimeLocal(value)
    // ---------------
    // 
    // Format a date and timestamp.
    dateTimeLocal: function(value) {
      return formatMomentLocal(value, 'lll');
    },

    // parseDate(value)
    // ----------------
    //
    // Parse a date. Returns null on parse failure.
    parseDate: function(value) {
      return parseMoment(value, 'll');
    },

    // parseTime(value)
    // ----------------
    //
    // Parse a time. Returns null on parse failure.
    parseTime: function(value) {
      return parseMoment(value, 'LT');
    },

    // parseDateTime(value)
    // --------------------
    //
    // Parse a date and timestamp. Returns null on parse failure.
    parseDateTime: function(value) {
      return parseMoment(value, 'lll');
    },

    // dayName(weekDayNumberFromSunday)
    // --------------------------------
    //
    // Given an enumerated week day number (Sunday is 0), return the day.
    dayName: function(weekDayNumberFromSunday) {
      if (!NumberType.isValid(weekDayNumberFromSunday)) {
        throw ['dayName() expects a number or none', weekDayNumberFromSunday];
      }

      if (Em.isNone(weekDayNumberFromSunday)) {
        return notAvailable();
      }

      return LocaleConfig.jsWorldLocaleConfig.getWeekdayName(weekDayNumberFromSunday % 7);
    },

    // dayAbbrName(weekDayNumberFromMonday)
    // --------------------------------
    //
    // Given an enumerated week day number (Monday is 0), return an abbreviation for the day.
    dayAbbrName: function(weekDayNumberFromMonday) {
      if (!NumberType.isValid(weekDayNumberFromMonday)) {
        throw ['dayAbbrName() expects a number or none', weekDayNumberFromMonday];
      }

      if (Em.isNone(weekDayNumberFromMonday)) {
        return notAvailable();
      }

      return LocaleConfig.jsWorldLocaleConfig.getAbbreviatedWeekdayName((weekDayNumberFromMonday + 1) % 7);
    },

    // monthName(monthNumber)
    // ----------------------
    //
    // Given an enumerated month number (January is 0), return the name of the month.
    monthName: function(monthNumber) {
      if (!NumberType.isValid(monthNumber)) {
        throw ['monthName() expects a number or none', monthNumber];
      }

      if (Em.isNone(monthNumber)) {
        return notAvailable();
      }

      return LocaleConfig.jsWorldLocaleConfig.getAbbreviatedMonthName(monthNumber % 12);
    },

    // monthAbbrName(monthNumber)
    // ----------------------
    //
    // Given an enumerated month number (January is 0), return an abbreviation for the name of the month.
    monthAbbrName: function(monthNumber) {
      if (!NumberType.isValid(monthNumber)) {
        throw ['monthName() expects a number or none', monthNumber];
      }

      if (Em.isNone(monthNumber)) {
        return notAvailable();
      }

      return LocaleConfig.jsWorldLocaleConfig.getMonthName(monthNumber % 12);
    }
  });
});
