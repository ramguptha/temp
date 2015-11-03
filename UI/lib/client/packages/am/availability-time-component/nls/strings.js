define({
  root: {
    availabilityTimeComponent: {

      timeStoredInUTC: 'Times are stored in Coordinated Universal Time (UTC).',

      validation: {
        notValidStartTime: 'Enter a valid start time.',
        notValidEndTime: 'Enter a valid end time.',
        startTimeAfterEndTime: 'Make sure the start time is before the end time.',
        notValidStartDateTime: 'Enter a valid start date/time.',
        notValidEndDateTime: 'Enter a valid end date/time.',
        startDateAfterEndTime: 'Make sure the start date (From) precedes the end date (until).'
      },

      dailyTimeIntervalSelector: {
        everyDayBetween: 'Every day between',
        and: 'and',
        utc: '(UTC)'
      },

      datePeriodSelector: {
        from: 'From',
        until: 'until'
      }
    }
  },

  // Supported Locales
  // -----------------

  'ja': true
});
