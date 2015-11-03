define([
  'ember',
  'locale',
  'packages/platform/date-type'
], function(
  Em,
  Locale,
  DateType
) {
  'use strict';

  return Em.Mixin.create({
    TextView: Em.TextView,

    valuesDiffer: function(lval, rval) {
      return (
        DateType.isValid(lval) && DateType.isValid(rval) && lval.getTime() !== rval.getTime()
      ) || (
        Em.isNone(lval) !== Em.isNone(rval)
      );
    },

    parseDate: function(str) {
      return Locale.parseDate(str);
    },

    formatDate: function(date) {
      return DateType.isValid(date) ? Locale.date(date) : null;
    },

    formatNonUTCDate: function(date) {
      if (Em.isNone(date) || !DateType.isValid(date)) {
        return null;
      }

      return '' + date.getFullYear() + '/' + this.pad(date.getMonth() + 1, 2) + '/' + this.pad(date.getDate(), 2);
    },

    formatUTCDateToUTCString: function(date, hasTime) {
      var dateString = '' + this.pad(date.getMonth() + 1, 2) + '/' + this.pad(date.getDate(), 2) + '/' +  date.getFullYear();

      if (hasTime ) {
        dateString += ' ' + this.pad(date.getHours(), 2) + ':' + this.pad(date.getMinutes(), 2);
      }

      return dateString;
    },

    formatDateToString: function(date) {
      var dateString = '' + date.getFullYear() + '-' + this.pad(date.getMonth() + 1, 2) + '-' + this.pad(date.getDate(), 2) + 'T'
        + this.pad(date.getHours(), 2) + ':' + this.pad(date.getMinutes(), 2) + ':00Z';

      return dateString;
    },

    formatDateToUTCString: function(date) {
      var dateString = '' + date.getUTCFullYear() + '-' + this.pad(date.getUTCMonth() + 1, 2) + '-' + this.pad(date.getUTCDate(), 2) + 'T'
        + this.pad(date.getUTCHours(), 2) + ':' + this.pad(date.getUTCMinutes(), 2) + ':00Z';

      return dateString;
    },

    pad: function(num, digits) {
      var result = '0000000000' + num;
      return result.substr(result.length - digits);
    }
  });
});
