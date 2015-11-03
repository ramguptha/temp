define([
  'ember',
  'packages/platform/locale-config',
  'locale',
  'timepicker',
  'logger'
], function(
  Em,
  LocaleConfig,
  Locale,
  $,
  logger
) {
  'use strict';

  return {

    // localizeOptions(options)
    // ------------------------
    //
    // Extend an options object for passing to $().datepicker or $().timepicker.
    //
    // We can't just use $.datepicker.setDefaults() or $.timepicker.setDefaults() because resources may only be
    // resolved once the application has been initialized.

    localize: function(options) {
      var r = this.r;
      var momentLocale = LocaleConfig.momentUtc().localeData();

      return Em.$.extend(
        options || {}, 

        // Strings for timepicker

        {
          timeFormat: this.mapMomentToPickerFormat(momentLocale.longDateFormat('LT'), this.momentToTimePickerFormatMap),
          currentText: r('shared.time.now'),
          closeText: r('shared.buttons.done'),
          amNames: [r('shared.time.am'), r('shared.time.amAbbr')],
          pmNames: [r('shared.time.pm'), r('shared.time.pmAbbr')],
          timeSuffix: '',
          timeOnlyTitle: r('desktop.timePickerComponent.chooseTime'),
          timeText: r('shared.time.time'),
          hourText: r('shared.time.hour'),
          minuteText: r('shared.time.minute'),
          secondText: r('shared.time.second'),
          millisecText: r('shared.time.millisecond'),
          microsecText: r('shared.time.microsecond'),
          timezoneText: r('shared.time.timezone'),
          isRTL: LocaleConfig.isRightToLeft()
        },

        // Strings for datepicker

        {
          dateFormat: this.mapMomentToPickerFormat(momentLocale.longDateFormat('ll'), this.momentToDatePickerFormatMap),
          closeText: r('shared.buttons.done'),
          prevText: r('desktop.datePickerComponent.choosePrevious'),
          nextText: r('desktop.datePickerComponent.chooseNext'),
          currentText: r('shared.time.today'),
          monthNames: Em.copy(momentLocale._months),
          monthNamesShort: Em.copy(momentLocale._monthsShort),
          dayNames: Em.copy(momentLocale._weekdays),
          dayNamesShort: Em.copy(momentLocale._weekdaysShort),
          dayNamesMin: Em.copy(momentLocale._weekdaysMin),
          weekHeader: r('desktop.datePickerComponent.weekHeader'),
          firstDay: momentLocale._week.dow,
          isRTL: LocaleConfig.isRightToLeft(),
          showMonthAfterYear: false,
          yearSuffix: ''
        }
      );
    },

    // Helpers
    // -------

    r: function(path) {
      return Locale.render(Locale.resolveGlobals(path)).toString();
    },

    momentToDatePickerFormatMap: {

      // Year
      YY: 'y',
      YYYY: 'yy',

      // Month
      M: 'm',
      MM: 'mm',
      MMM: 'M',
      MMMM: 'MM',

      // Day of Month
      D: 'd',
      DD: 'dd',

      // Day of Year
      DDD: 'o',
      DDDD: 'oo'
    },

    momentToTimePickerFormatMap: {

      // Hour
      H: 'H',
      HH: 'HH',
      h: 'h',
      hh: 'hh',

      // Minute
      m: 'm',
      mm: 'mm',

      // Second
      s: 's',
      ss: 'ss',

      // AM / PM
      A: 'TT',
      aa: 'tt'
    },

    mapMomentToPickerFormat: function(format, map) {
      return format.replace(/\[[^\]]+\]|MM?M?M?|Mo|DD?|Do|YY(YY)?|A|a|HH?|hh?|mm?|ss?/g, function(match) {

        // Map escaped sub-strings
        if (match.match(/^\[.*\]$/)) {
          return match.replace(/^\[/, '\'').replace(/\]$/, '\'');
        }

        // Map formatting tokens
        var mapped = map[match];

        if (!mapped) {
          logger.error(['Failed to map locale time format', format, map, match]);
          return '';
        }

        return mapped;
      });
    }
  };
});
