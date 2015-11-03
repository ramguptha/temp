define([
  'ember',
  'radioButtonGroup',
  'formatter',
  'locale',
  'packages/platform/locale-config',
  'packages/platform/date-type',

  './lib/components/content_time_picker_component',
  './lib/components/content_date_time_picker_component',

  'text!./lib/templates/daily_time_interval_selector.handlebars',
  'text!./lib/templates/date_period_selector.handlebars',

  'text!./lib/templates/availability-time.handlebars',
  'i18n!./nls/strings'
], function(
  Em,
  RadioButtonGroup,
  Formatter,
  Locale,
  LocaleConfig,
  DateType,

  AmContentTimePickerComponent,
  AmContentDateTimePickerComponent,

  dailyIntervalSelectorTemplate,
  datePeriodSelectorTemplate,

  template,
  strings
) {
  'use strict';

  // Availability Time
  // =================
  //
  // Encapsulates the interaction of setting the availability Date/Time

  var AvailabilityTimeComponent = Em.Component.extend({
    layout: Em.Handlebars.compile(template),

    tTimeStoredInUTC: 'availabilityTimeComponent.timeStoredInUTC'.tr(),
    tNotValidStartTime: 'availabilityTimeComponent.validation.notValidStartTime'.tr(),
    tNotValidEndTime: 'availabilityTimeComponent.validation.notValidEndTime'.tr(),
    tStartTimeAfterEndTime: 'availabilityTimeComponent.validation.startTimeAfterEndTime'.tr(),

    tNotValidStartDateTime: 'availabilityTimeComponent.validation.notValidStartDateTime'.tr(),
    tNotValidEndDateTime: 'availabilityTimeComponent.validation.notValidEndDateTime'.tr(),
    tStartDateAfterEndTime: 'availabilityTimeComponent.validation.startDateAfterEndTime'.tr(),

    RadioButton: RadioButtonGroup.RadioButton,

    // View used for availability selection via daily interval
    dailyIntervalSelectorView: Em.View.extend({
      defaultTemplate: Em.Handlebars.compile(dailyIntervalSelectorTemplate)
    }),

    // View used for availability selection via from-until date/times
    datePeriodSelectorView: Em.View.extend({
      defaultTemplate: Em.Handlebars.compile(datePeriodSelectorTemplate)
    }),

    dailyIntervalContainerClass: "hours-container",
    datePeriodContainerClass: "date-container",

    // Controls the visibility of the component
    isVisible: false,

    availabilitySelector: 0,

    dailyIntervalBtnSelected: false,
    datePeriodBtnSelected: false,

    // Time picker values for setting availability daily start / end times
    availabilityDailyStart: null,
    availabilityDailyEnd: null,
    availabilityDailyStartUTC: null,
    availabilityDailyEndUTC: null,

    // Date-time picker values for availability start / end dates & times
    availabilityDateStart: null,
    availabilityDateEnd: null,
    availabilityDateStartUTC: null,
    availabilityDateEndUTC: null,

    // Original values for start/end time
    originalAvailabilityStart: null,
    originalAvailabilityEnd: null,

    // If not complete (A time field is missing), disable the action button on the owner of the component
    isDateTimeValid: false,

    // If the user enters the same value for start/end time, disable the action
    dateTimeChanged: false,

    // Formatted time compatible with the server requirements
    formattedTime: null,

    // Available assigned time on the server
    assignedTime: null,

    timeErrorMessage: null,
    dateErrorMessage: null,
    infoMessage: null,

    init: function() {
      this._super();

      // reset the info message and intervals selection
      this.set('infoMessage', null);
    },

    visibilityChanged: function() {
      if (this.get('isVisible')) {
        this.setAvailabilitySelection(1);
      } else {
        // Save the current selector before we reset the selector value, so that we don't override with
        // the default if the Set availability time button is subsequently re-checked
        this.setAvailabilitySelection(0);
      }

      this.validateAvailabilitySettings();
    }.observes('isVisible').on('init'),

    availabilityDateTimeChanged: function () {
      if(this.get('paused')) { return; }

      this.validateAvailabilitySettings();
    }.observes('availabilityDailyStart', 'availabilityDailyEnd',
      'availabilityDateStart', 'availabilityDateEnd'),

    availabilityTypeChanged: function (router, event) {
      if(this.get('paused')) { return; }
      this.set('paused', true);
      var availabilitySelector = 0;

      switch(event) {
        case 'dailyIntervalBtnSelected':
          availabilitySelector = 1;
          break;

        case 'datePeriodBtnSelected':
          availabilitySelector = 2;
          break;
      }

      this.setAvailabilitySelection(availabilitySelector);
      this.validateAvailabilitySettings(availabilitySelector);

      this.set('paused', false);

    }.observes('dailyIntervalBtnSelected', 'datePeriodBtnSelected'),


    validateAvailabilitySettings: function (availabilitySelector) {
      var selector = availabilitySelector || this.get('availabilitySelector'),
        availabilityDailyStart = this.get('availabilityDailyStart'),
        availabilityDailyEnd = this.get('availabilityDailyEnd'),
        availabilityDateStart = this.get('availabilityDateStart'),
        availabilityDateEnd = this.get('availabilityDateEnd'),
        isValid = false;

      this.set('infoMessage', null);

      switch (selector) {
        case 0:
          // Always available, date/time settings are not applicable
          break;

        case 1:
          // Validate daily time interval settings
          if (availabilityDailyStart && !this.isValidDate(availabilityDailyStart)) {
            this.setError(this.get('tNotValidStartTime'), true);

          } else if (availabilityDailyEnd && !this.isValidDate(availabilityDailyEnd)) {
            this.setError(this.get('tNotValidEndTime'), true);

          } else if (this.compareDates(availabilityDailyEnd, availabilityDailyStart) <= 0) {
            this.setError(this.get('tStartTimeAfterEndTime'), true);

          } else if (availabilityDailyStart === null || availabilityDailyStart.length === 0 ||
            availabilityDailyEnd === null || availabilityDailyEnd.length === 0) {
            this.setError(null);

          } else {
            isValid = true;

          }

          this.setProperties({
            availabilityDailyStartUTC: availabilityDailyStart ?
              LocaleConfig.momentUtc(availabilityDailyStart).format('HH:mm') : null,
            availabilityDailyEndUTC: availabilityDailyEnd ?
              LocaleConfig.momentUtc(availabilityDailyEnd).format('HH:mm') : null
          });
          break;

        case 2:
          // Validate date-time period settings
          if (availabilityDateStart && !this.isValidDate(availabilityDateStart)) {
            this.setError(this.get('tNotValidStartDateTime'), false);

          } else if (availabilityDateEnd && !this.isValidDate(availabilityDateEnd)) {
            this.setError(this.get('tNotValidEndDateTime'), false);

          } else if (this.compareDates(availabilityDateEnd, availabilityDateStart) <= 0) {
            this.setError(this.get('tStartDateAfterEndTime'), false);

          } else if (availabilityDateStart === null || availabilityDateStart.length === 0 ||
            availabilityDateEnd === null || availabilityDateEnd.length === 0) {
            this.setError(null);

          } else {
            isValid = true;
          }

          this.setProperties({
            availabilityDateStartUTC: availabilityDateStart ?
              LocaleConfig.momentUtc(availabilityDateStart).format('YYYY-MM-DD HH:mm') : null,
            availabilityDateEndUTC: availabilityDateEnd ?
              LocaleConfig.momentUtc(availabilityDateEnd).format('YYYY-MM-DD HH:mm') : null
          });
          break;
      }

      if (isValid) {
        // Reset existing error messages
        this.setError(null);

        this.set('infoMessage', this.get('tTimeStoredInUTC'));
      }

      this.set('isDateTimeValid', isValid);
      this.set('targetObject.isAvailabilityTimeValid', isValid);

      this.createFormattedTime();
    },

    setAvailabilitySelection: function (selection) {
      this.set('availabilitySelector', selection);
      this.set('targetObject.availabilitySelector', selection);

      if (selection === 1) {
        this.setProperties({
          dailyIntervalBtnSelected: true,
          dailyIntervalContainerClass: "hours-container",

          datePeriodBtnSelected: false,
          datePeriodContainerClass: "date-container-disabled"
        });

      } else if (selection === 2) {
        this.setProperties({
          dailyIntervalBtnSelected: false,
          dailyIntervalContainerClass: "hours-container-disabled",

          datePeriodBtnSelected: true,
          datePeriodContainerClass: "date-container"
        });
      }
    },

    // Set availability time properties if they were already available on the server
    populateAvailabilityTime: function () {
      if (!this.get('assignedTime')) { return; }

      var startTime = this.get('assignedTime').startTime,
        endTime = this.get('assignedTime').endTime;

      if (!startTime || !endTime) { return; }

      switch (this.get('availabilitySelector')) {
        case 0:
          // Always
          break;

        case 1:
          // Daily interval
          var date = new Date();
          var startHourAndMinute = Formatter.convertTimeInterval(startTime).split(':');
          var startTimeUtc = new Date(date.getFullYear(), date.getMonth(), date.getDate(), startHourAndMinute[0], startHourAndMinute[1]);

          var endHourAndMinute = Formatter.convertTimeInterval(endTime).split(':');
          var endTimeUtc = new Date(date.getFullYear(), date.getMonth(), date.getDate(), endHourAndMinute[0], endHourAndMinute[1]);

          this.setProperties({
            availabilityDailyStart: startTimeUtc,
            availabilityDailyEnd: endTimeUtc,

            availabilityDailyStartUTC: startTime,
            availabilityDailyEndUTC: endTime
          });

          this.setAvailabilitySelection(1);
          break;

        case 2:
          // Fixed period
          var startDateUTC = LocaleConfig.momentUtc(startTime, 'YYYY-MM-DD[T]HH:mm:ssZ', true).format('YYYY-MM-DD HH:mm');
          var endDateUTC = LocaleConfig.momentUtc(endTime, 'YYYY-MM-DD[T]HH:mm:ssZ', true).format('YYYY-MM-DD HH:mm');

          this.setProperties({
            availabilityDateStart: new Date(startTime),
            availabilityDateEnd: new Date(endTime),

            availabilityDateStartUTC: startDateUTC,
            availabilityDateEndUTC: endDateUTC
          });

          this.setAvailabilitySelection(2);
          break;
      }

      this.setProperties({
        originalAvailabilityStart: startTime,
        originalAvailabilityEnd: endTime,

        isDateTimeValid: true,
        dateTimeChanged: false
      });

    }.observes('assignedTime').on('init'),

    isValidDate: function (dateStr) {
      var date = new Date(dateStr);
      return (date.toString() != 'Invalid Date');
    },

    compareDates: function (date1, date2) {
      if (Em.isEmpty(date1) || Em.isEmpty(date2)) {
        return 1;
      }

      return date1.getTime() - date2.getTime();
    },

    createFormattedTime: function () {
      var convertDateToTimeString = function(date) {
        return date.toTimeString().match(/\d{2}:\d{2}/)[0];
      };

      var convertDateToISO8601String = function(date) {
        return date.toISOString().match(/.*T\w*:\w*:\w*/)[0] + 'Z';
      };

      var toUTC = function(date) {
        return new Date(date.getUTCFullYear(), date.getUTCMonth(), date.getUTCDate(), date.getUTCHours(), date.getUTCMinutes(), date.getUTCSeconds());
      };

      var startTime = null,
        endTime = null;

      if (this.get('dailyIntervalBtnSelected')) {
        var availabilityDailyStart = this.get('availabilityDailyStart'),
          availabilityDailyEnd = this.get('availabilityDailyEnd');

        if ((availabilityDailyStart && this.isValidDate(availabilityDailyStart)) &&
          (availabilityDailyEnd && this.isValidDate(availabilityDailyEnd))) {

          startTime = convertDateToTimeString(toUTC(availabilityDailyStart));
          endTime = convertDateToTimeString(toUTC(availabilityDailyEnd));
        }

      } else {
        var availabilityDateStart = this.get('availabilityDateStart'),
          availabilityDateEnd = this.get('availabilityDateEnd');

        if ((availabilityDateStart && this.isValidDate(availabilityDateStart)) &&
          (availabilityDateEnd && this.isValidDate(availabilityDateEnd))) {

          startTime = convertDateToISO8601String(availabilityDateStart);
          endTime = convertDateToISO8601String(availabilityDateEnd);
        }
      }

      if (startTime && endTime) {
        var originalAvailabilityStart = this.get('originalAvailabilityStart'),
          originalAvailabilityEnd = this.get('originalAvailabilityEnd');

        var formattedTime = { startTime: startTime, endTime: endTime},
          dateTimeChanged = originalAvailabilityStart !== startTime || originalAvailabilityEnd !== endTime;

        this.setProperties({
          formattedTime: formattedTime,
          'targetObject.formattedTime': formattedTime,
          dateTimeChanged: dateTimeChanged,
          'targetObject.dateTimeChanged': dateTimeChanged
        });
      }
    },

    setError: function (message, isTimeError) {
      this.setProperties({
        timeErrorMessage: isTimeError ? message : null,
        dateErrorMessage: !isTimeError ? message : null
      });

      if(message) {
        this.set('isDateTimeValid', false);
      }
    }
  });

  return AvailabilityTimeComponent.reopenClass({
    appClasses: {
      AvailabilityTimeComponent: AvailabilityTimeComponent,
      AmContentTimePickerComponent: AmContentTimePickerComponent,
      AmContentDateTimePickerComponent: AmContentDateTimePickerComponent
    },
    appStrings: strings
  });
});
