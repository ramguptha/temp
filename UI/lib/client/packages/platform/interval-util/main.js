define([
  'ember'
], function(
  Em
  ) {
  'use strict';

  // IntervalType
  // ========
  // Class for handling intervals
  return {
    // Take an interval represented as a string and convert it to an int according to AM Console's rules
    // null will be returned if the provided string is in invalid format
    formatIntervalToInt: function(intervalStr) {
      var yearVal, monthVal, dayVal, timeVal, hourVal, minVal, secVal=0;

      // First check the general syntax for correctness
      if( intervalStr.replace(/((\d+\s+years(\s|$))|(\d+\s+year(\s|$))|(\d+\s+y(\s|$))|(\d+\s+months(\s|$))|(\d+\s+month(\s|$))|(\d+\s+m(\s|$))|(\d+\s+days(\s|$))|(\d+\s+day(\s|$))|(\d+\s+d(\s|$|:))|\d+:\d+(:\d+)?)/gi,'').trim() !== ""){
        return null;
      }

      yearVal = this.getValueFromIntervalPartString(intervalStr.match(/(\d+\s+(year(\s|$)|years(\s|$)|y(\s|$)))/gi));
      monthVal = this.getValueFromIntervalPartString(intervalStr.match(/(\d+\s+(month(\s|$)|months(\s|$)|m(\s|$)))/gi));
      dayVal = this.getValueFromIntervalPartString(intervalStr.match(/(\d+\s+(day(\s|$)|days(\s|$)|d(\s|$)))/gi));
      timeVal = this.getValueFromIntervalPartString(intervalStr.match(/\d+:\d+(:\d+)?/g), true);

      if( yearVal === null || monthVal === null || dayVal === null || timeVal === null ) {
        return null;
      }

      hourVal = timeVal[0];
      minVal = timeVal[1];

      if(timeVal.length === 3 ) {
        secVal = timeVal[2];
      }

      if( minVal >= 60 || secVal >= 60 ) {
        return null;
      }

      // WARNING: incorrectly using 365 ( instead of 365.25 ) days in a year, 30 days in a month. Don't like it? Blame AM Console
      return (yearVal * 365 * 24 * 60 * 60 ) + ( monthVal * 30 * 24 * 60 * 60 ) + ( dayVal * 24 * 60 * 60 ) +
        ( hourVal * 60 * 60 ) + ( minVal * 60 ) + Number(secVal);
    },

    // Deserialize an interval value provided as an int to a string
    formatIntToInterval: function(intervalInt) {
      var yearVal, monthVal, dayVal, hourVal, minVal, secVal, rtnString = "", intervalIntRemainder;

      yearVal = Math.floor(intervalInt / 365 / 24 / 60 / 60);
      intervalIntRemainder = intervalInt - yearVal * 365 * 24 * 60 * 60;

      monthVal = Math.floor( intervalIntRemainder / 30 / 24 / 60 / 60);
      intervalIntRemainder -= monthVal * 30 * 24 *60 * 60;

      dayVal = Math.floor( intervalIntRemainder / 24 / 60 / 60);
      intervalIntRemainder -= dayVal * 24 * 60 * 60;

      hourVal = Math.floor( intervalIntRemainder / 60 / 60);
      intervalIntRemainder -= hourVal * 60 * 60;

      minVal = Math.floor( intervalIntRemainder / 60 );
      secVal = intervalIntRemainder - minVal * 60;

      if( yearVal != 0 ) {
        rtnString += (yearVal + (yearVal === 1 ? " year " : " years "));
      }
      if( monthVal != 0 ) {
        rtnString += (monthVal + (monthVal === 1 ? " month " : " months "));
      }
      if( dayVal != 0 ) {
        rtnString += (dayVal + (dayVal === 1 ? " day " : " days "));
      }

      rtnString += (hourVal + ":" + minVal);

      if( secVal != 0 ) {
        rtnString += (":" + secVal);
      }

      return rtnString;
    },

    // Helper parsing function for serializeToInt
    getValueFromIntervalPartString: function(intervalPartStr, isTime) {
      if (isTime) {
        if (intervalPartStr !== null && intervalPartStr.length > 1) {
          return null;
        } else if (intervalPartStr != null) {
          var timeLength = intervalPartStr[0].split(':').length;
          return timeLength > 3 || timeLength < 2 ? null : intervalPartStr[0].split(':');
        } else {
          return [0, 0];
        }
      } else {
        if (intervalPartStr !== null && intervalPartStr.length > 1) {
          return null;
        } else if (intervalPartStr !== null) {
          return intervalPartStr[0].match(/\d+/g)[0];
        } else {
          return 0;
        }
      }
    }
  }
});