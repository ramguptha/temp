define([
  'ember',
  './lib/namespace',
  'locale',
  'packages/platform/number-type',
  'packages/platform/date-type'
], function(
  Em,
  Formatter,
  Locale,
  NumberType,
  DateType
) {
  'use strict';

  // Formatter
  // =========
  //
  // The Formatter package contains a collection of filter functions for transforming values into formatted strings.

  return Formatter.reopen({

    /**
     * Format bytes into the most logical magnitude (KB, MB, or GB etc).
     */
    formatBytes: function(bytes) {
      if (!bytes) {
        return '0 ' + Locale.renderGlobals('shared.formatBytes.bytes');
      }

      var units = [ Locale.renderGlobals('shared.formatBytes.bytes'),
          Locale.renderGlobals('shared.formatBytes.kB'),
          Locale.renderGlobals('shared.formatBytes.mB'),
          Locale.renderGlobals('shared.formatBytes.gB'),
          Locale.renderGlobals('shared.formatBytes.tB') ],
          i;

      for (i = 0; bytes >= 1024 && i < 4; i++) {
        bytes /= 1024;
      }

      return bytes.toFixed(i === 0 ? 0 : 2) + ' ' + units[i];
    },

    formatMegabytes: function(megabytes) {
      var bytes = megabytes * 1024 * 1024;
      return Formatter.formatBytes(bytes);
    },

    formatBytesOrNA: function(bytes) {
      if (!bytes) {
        return Locale.notAvailable();
      }
      return Formatter.formatBytes(bytes);
    },

    formatSpeedBit: function(bit) {
      if (!bit) {
        return '0 ' + Locale.renderGlobals('shared.formatSpeed.bits');
      }

      var units = [Locale.renderGlobals('shared.formatSpeed.bits'),
          Locale.renderGlobals('shared.formatSpeed.kbits'),
          Locale.renderGlobals('shared.formatSpeed.mbits'),
          Locale.renderGlobals('shared.formatSpeed.gbits'),
          Locale.renderGlobals('shared.formatSpeed.tbits')],
          i;

      for (i = 0; bit >= 1000 && i < 3; i++) {
        bit /= 1000;
      }

      return bit.toFixed(i === 0 ? 0 : 1) + ' ' + units[i];
    },

    formatSpeedBitOrNA: function(bit) {
      if (!bit) {
        return Locale.notAvailable();
      }
      return Formatter.formatSpeedBit(bit);
    },

    formatBoolean: function(bool) {
      return Locale.boolean(!!bool);
    },

    formatBooleanOrNA: function(bool) {
      return !Em.isNone(bool) ? Locale.boolean(!!bool) : Locale.notAvailable();
    },

    stringToBoolean: function(value)
    {
      var result = { "true" : true, "false" : false }[value];
      return result == null ? value : result;
    },

    formatNumberOrNA: function(number) {
      number = !Em.isEmpty(number) ? Number(number) : number;
      
      // TODO: This behaviour is mandated by unit tests but seem undesirable
      if ('' === number) {
        return Locale.notAvailable();
      }

      return NumberType.isValid(number) ? Locale.number(number) : Locale.notAvailable();
    },

    formatNumberToBooleanOrNA: function(number) {
      if (number == null || number == undefined) {
        return Locale.notAvailable();
      } else {
        return Locale.boolean(number > 0);
      }
    },

    /**
     * Date.toLocaleString() and Date.toLocaleDateString() return browser-dependent strings e.g., "6/14/2013 9:30:04 AM"
     * on Chrome and "Friday, June 14, 2013 9:30:04 AM". So we format the date in a browser independent way for 
     * consistency, but note that this will need to be internationalized.
     *
     * TODO: there should only be one of formatDate() and formatShortDate().
     */
    formatDate: function(date) {
      if (Em.isNone(date) || date.getTime() === 0 || isNaN(date)) {
        return Locale.notAvailable();
      } else {
        return Locale.date(date);
      }
    },

    formatDateLocal: function(date) {
      if (Em.isNone(date) || date.getTime() === 0 || isNaN(date)) {
        return Locale.notAvailable();
      } else {
        return Locale.dateLocal(date);
      }
    },

    formatShortDate: function(date) {
      if (Em.isNone(date) || date.getTime() === 0) {
        return Locale.notAvailable();
      } else {
        return Locale.date(date);
      }
    },

    formatTimeOnly: function(time) {
      if (Em.isNone(time) || time.getTime() === 0 || isNaN(time)) {
        return '--';
      }
      else {
        var timeString = time.getHours() > 12 ?
        time.getHours() - 12 + ':' + ('0' + time.getMinutes()).slice(-2) + ':' + ('0' + time.getSeconds()).slice(-2) + ' ' + Locale.renderGlobals('shared.time.pm'):
        time.getHours() + ':' + ('0' + time.getMinutes()).slice(-2) + ':' + ('0' + time.getSeconds()).slice(-2) +  ' ' + Locale.renderGlobals('shared.time.am');

        return timeString;
      }
    },

    formatTime: function(time) {
      if (Em.isNone(time) || time.getTime() === 0 || isNaN(time)) {
        return Locale.notAvailable();
      } else {
        return Locale.dateTime(time);
      }
    },

    formatShortDateTime: function(time) {
      if (Em.isNone(time) || time.getTime() === 0 || isNaN(time)) {
        return Locale.notAvailable();
      }
      else {
        return Formatter.formatShortDate(time) + ' ' + Formatter.formatTimeOnly(time);
      }
    },

    // ex. Jun 19, 2015 3:04 PM
    formatTimeLocal: function(time) {
      if (Em.isNone(time) || time.getTime() === 0 || isNaN(time)) {
        return Locale.notAvailable();
      } else {
        return Locale.dateTimeLocal(time);
      }
    },

    formatTimeIntervalLocal: function(utcHoursMinutesString) {
      var formatted = Locale.notAvailable();

      var utcParsed = this.parseHoursMinutes(utcHoursMinutesString);
      if (utcParsed) {
        var date = new Date();
        date.setUTCHours(utcParsed.hours, utcParsed.minutes);
        formatted = Locale.timeLocal(date);
      }

      return formatted;
    },

    formatOSVersion: function(version) {
      return version ? [
        (version >> 24) & 0xff,
        (version >> 20) & 0xf,
        (version >> 16) & 0xf
        ].join('.').replace(/(\.0){1}$/, '') : Locale.notAvailable();
    },

    formatOSVersionPlus: function(version) {
      return version ? Formatter.formatOSVersion(version) + "+" : Locale.notAvailable();
    },

    formatOSVersionComputer: function(version) {
      return version ? [
        (version >> 8).toString(16),
        version >> 4 & 0xf,
        version & 0xf
      ].join('.').replace(/(\.0){1}$/, '') : Locale.notAvailable();
    },

    formatAgentVersion: function(version) {
      var agentVersion;

      if(version) {
        // Special formatting for beta version === 6. Normal version has 8
        if(((version >> 12) & 0xf) === 6) {
          agentVersion = [
            (version >> 24) & 0xff,
            (version >> 20) & 0xf
          ].join('.').replace(/(\.0){1}$/, '') + 'b' + (version & 0xf);
        } else {
          agentVersion = Formatter.formatOSVersion(version);
        }
      } else {
        agentVersion = Locale.notAvailable();
      }


      return agentVersion;
    },

    formatIPv4Address: function(ipAddress) {
      return ipAddress != null ? [
        (ipAddress >> 24) & 0xff,
        (ipAddress >> 16) & 0xff,
        (ipAddress >> 8) & 0xff,
        ipAddress & 0xff
      ].join('.') : Locale.notAvailable();
    },

    /**
     * Format clock speed into the most logical magnitude (KHz, MHz, or GHz etc).
     */
    formatClockSpeed: function(speed) {
      if (!speed) {
        return Locale.notAvailable();
      }

      var units = [ Locale.renderGlobals('shared.formatClockSpeed.hertz'),
                    Locale.renderGlobals('shared.formatClockSpeed.kHz'),
                    Locale.renderGlobals('shared.formatClockSpeed.mHz'),
                    Locale.renderGlobals('shared.formatClockSpeed.gHz') ];
      var i;

      for (i = 0; speed >= 1000 && i < 3; i++) {
        speed /= 1000;
      }

      return speed.toFixed(i === 0 ? 0 : 1) + ' ' + units[i];
    },

    formatPercent: function(percentVal) {
      return (Em.isNone(percentVal) ? Locale.notAvailable() : percentVal + '%');
    },

    // Convert intervals from days, hours or minutes to seconds
    // -------------------------
    //
    // Given the interval (Integer) and the frequency (String) (2, 'minutes')
    // Return the converted value in seconds (Integer) (120)
    //
    formatIntervalDaysHoursOrMinutesInSecs: function(interval, timeString) {
      var seconds;

      if (timeString.toLowerCase() === 'minutes') { // minutes
        seconds = interval*60;
      } else if (timeString.toLowerCase() === 'hours') { // hours
        seconds = interval*60*60;
      } else if (timeString.toLowerCase() === 'days') { //days
        seconds = interval * 60 * 60 * 24;
      }

      return seconds;
    },

    // Convert intervals from seconds to days, hours or minutes in String format
    // -------------------------
    //
    // Given the seconds (Integer) (120)
    // Return the interval + localized frequency (String) ('2 Minutes')
    //
    formatIntervalInDaysHoursOrMinutesToString: function(secs) {
      var value, timeString;

      //days
      if(secs%(60*60*24) === 0) {
        value = secs/(60*60*24);

        if(value === 1) {
          timeString = Locale.renderGlobals('shared.time.day').toString();
        } else {
          timeString = Locale.renderGlobals('shared.time.days').toString();
        }
      } else if(secs%(60*60) === 0) { // hours
        value = secs/(60*60);

        if(value === 1) {
          timeString = Locale.renderGlobals('shared.time.hour').toString();
        } else {
          timeString = Locale.renderGlobals('shared.time.hours').toString();
        }
      } else { // minutes
        value = secs/60;

        if(value === 1) {
          timeString = Locale.renderGlobals('shared.time.minute').toString();
        } else {
          timeString = Locale.renderGlobals('shared.time.minutes').toString();
        }
      }

      return value + ' ' + timeString;
    },

    // Parse intervals from seconds to days, hours or minutes to an object
    // -------------------------
    //
    // Given the seconds (Integer) (120)
    // Return the { interval: interval, frequency: frequency (non-localized version)}  { interval: 2, frequency: 'hours' }
    //
    parseIntervalInDaysHoursOrMinutes: function(secs) {
      var interval, frequency;

      if(secs%(60*60*24) === 0) { // days

        interval = secs/(60*60*24);
        frequency = 'days';

      } else if(secs%(60*60) === 0) { // hours

        interval = secs/(60*60);
        frequency = 'hours';

      } else { // minutes

        interval = secs/60;
        frequency = 'minutes';
      }

      return {
        interval: interval,
        frequency: frequency
      }
    },

    formatIntervalInHoursMinutesFromSecs: function(secs) {
      var interval = '';

      if (secs) {
        var totalDiff = secs;

        var days = Math.floor(totalDiff / 60 / 60 / 24);
        totalDiff -= days * 60 * 60 * 24;

        var hours = Math.floor(totalDiff / 60 / 60);
        totalDiff -= hours * 60 * 60;

        var minutes = Math.floor(totalDiff / 60);

        // Format Hours
        var hourText = '00';
        if (hours > 0){ hourText = String(hours);}
        if (hourText.length == 1) { hourText = '0' + hourText }

        // Format Minutes
        var minText = '00';
        if (minutes > 0){ minText = String(minutes);}
        if (minText.length == 1) { minText = '0' + minText }

        interval = hourText + ':' + minText;
      }

      return interval;
    },

    // return string in format: 3 years, 8 months, 12 days
    formatIntervalInSecs: function(secs) {

      // This seems to be the algorithm currently used in AM Admin Console app which assumes 365 days per year and
      // 30 days per month.
      var interval = '';

      if (secs) {
        var days = Math.floor(secs / 60 / 60 / 24);
        var months = Math.floor(days / 30);
        var years = Math.floor(days / 365);

        months = months - (years * 12);
        days = days - (years * 365);

        if (days >= 30) {
          days = days - (months * 30);

          //If wrong calculation - fix it
          if (days < 0 && months > 1) {
            months--;
            days = Math.floor(secs / 86400) - (years * 365) - (months * 30);
          }
        }

        // Format Years
        var yearText = '';
        if (years > 0){
          yearText = String(years);
          yearText += (years > 1) ? ' ' + Locale.renderGlobals('shared.formatInterval.yearsLowCase') + ', ' : ' ' + Locale.renderGlobals('shared.formatInterval.yearLowCase') + ', ';
        }

        // Format Months
        var monthText = '';
        if (months > 0) {
          monthText = String(months);
          monthText += months > 1 ? ' ' + Locale.renderGlobals('shared.formatInterval.monthsLowCase') + ', ' : ' ' + Locale.renderGlobals('shared.formatInterval.monthsLowCase') + ', ';
        }

        // Format Days
        var dayText;
        dayText = String(days);
        dayText += days === 1 ? ' ' + Locale.renderGlobals('shared.formatInterval.dayLowCase') : ' '+ Locale.renderGlobals('shared.formatInterval.daysLowCase');

        interval = yearText + monthText + dayText;
      }

      return Em.isEmpty(interval) ? Locale.notAvailable() : interval;
    },


    formatFileAssignmentType: function(assignmentType) {
      var assignmentTypeString = null;
      switch (assignmentType) {
        case 0:
          assignmentTypeString = Locale.renderGlobals('amMobilePolicies.shared.assignmentRuleOptions.forbidden');
          break;
        case 1:
          assignmentTypeString = Locale.renderGlobals('amMobilePolicies.shared.assignmentRuleOptions.onDemandAutoRemove');
          break;
        case 2:
          assignmentTypeString = Locale.renderGlobals('amMobilePolicies.shared.assignmentRuleOptions.autoInstallAutoRemove');
          break;
        case 3:
          assignmentTypeString = Locale.renderGlobals('amMobilePolicies.shared.assignmentRuleOptions.autoInstall');
          break;
        case 4:
          assignmentTypeString = Locale.renderGlobals('amMobilePolicies.shared.assignmentRuleOptions.onDemand');
          break;
      }
      return assignmentTypeString;
    },

    // this formats a number like 123456.12 to 123,456.12
    formatDecimal: function (value) {
      var parts = value.toString().split(".");
      parts[0] = parts[0].replace(/\B(?=(\d{3})+(?!\d))/g, ",");
      return parts.join(".");
    },

    formatFileAvailabilitySelector: function(availabilitySelector) {
      var availabilitySelectorString = null;
      switch (availabilitySelector) {
        case 0:
          availabilitySelectorString = Locale.renderGlobals('amMobilePolicies.shared.availabilitySelector.always');
          break;
        case 1:
          availabilitySelectorString = Locale.renderGlobals('amMobilePolicies.shared.availabilitySelector.dailyInterval');
          break;
        case 2:
          availabilitySelectorString = Locale.renderGlobals('amMobilePolicies.shared.availabilitySelector.fixedPeriod');
          break;
      }
      return availabilitySelectorString;
    },

    formatFileAssignmentTime: function(assignmentTime) {
      if (Em.isNone(assignmentTime)) {
        return '';
      }

      if (assignmentTime.indexOf('-') !== -1) {
        return Formatter.formatTimeLocal(new Date(assignmentTime));
      } else {
        // return Formatter.toLocalTimeInterval(assignmentTime);
        return Formatter.formatTimeIntervalLocal(assignmentTime);
      }
    },

    /**
     * Convert a Date to UTC ISO 8601 formatted string
     */
    toUTC8601String: function(date) {
      return date.getUTCFullYear() + 
        '-' + this.pad(date.getUTCMonth() + 1, 2) + '-' + this.pad(date.getUTCDate(), 2) +
        'T' + this.pad(date.getUTCHours(), 2) + ':' + this.pad(date.getUTCMinutes(), 2) + 
        ':' + this.pad(date.getUTCSeconds(), 2) + 'Z';
    },

    /**
     * Convert a Date to Local date-time string of format MM/DD/YYYY HH:MM
     */
    toLocalDateTimeString: function(date) {
      return this.pad(date.getMonth() + 1, 2) + '/' + this.pad(date.getDate(), 2) + '/' + date.getFullYear() +
        ' ' + this.pad(date.getHours(), 2) + ':' + this.pad(date.getMinutes(), 2);
    },

    /**
     * Convert a (Local) HH:MM Time Interval (24 hr) to a UTC time interval
     */
    toUTCTimeInterval: function(localTimeInterval) {
      return this.convertTimeInterval(localTimeInterval, true);
    },

    /**
     * Convert a UTC HH:MM Time Interval (24 hr) to a local time interval
     */
    toLocalTimeInterval: function(utcTimeInterval) {
      return this.convertTimeInterval(utcTimeInterval, false);
    },

    // parseHoursMinutes(string)
    // -------------------------
    //
    // Given a string in HH:MM format, return an object like:
    //
    //     { hours: Number(HH), minutes: Number(MM) }
    //
    // Otherwise return null. Parser applies no restrictions related to the range of possible hours and minutes.
    parseHoursMinutes: function(str) {
      var parsed = null;

      if ('string' === typeof(str)) {
        var index = str.indexOf(':');
        if (index !== -1 && index > 0 && index < str.length - 1) {
          parsed = {
            hours: Number(str.substring(0, index), 10),
            minutes: Number(str.substring(index + 1, str.length), 10)
          };
        }
      }

      return parsed;
    },

    convertTimeInterval: function(timeInterval, toUTC) {
      var intervalUTC = '';
      var parsed = this.parseHoursMinutes(timeInterval);
      if (parsed) {
        var hours = parsed.hours;
        var mins = parsed.minutes;

        var date = new Date();
        var offset = toUTC ? date.getTimezoneOffset() : 0 - date.getTimezoneOffset();
        var offsetMins = offset % 60;
        var offsetHours = (offset - offsetMins) / 60;
        var minsUTC = mins + offsetMins;
        var hoursUTC = hours + offsetHours;

        if (hoursUTC >= 24) {
          hoursUTC -= 24;
        } else if (hoursUTC < 0) {
          hoursUTC += 24;
        }

        intervalUTC = this.pad(hoursUTC, 2) + ':' + this.pad(minsUTC, 2);
      }

      return intervalUTC;
    },

    formatErrorResponse: function(responseText) {
      var res = '';
      if (!Em.isEmpty(responseText)) {
        try {
          var response = eval('(' + responseText + ')');
          if (!Em.isNone(response)) {
            if (!Em.isEmpty(response.message)) {
              res = Locale.renderGlobals('shared.errorFormatter.message') + ' ' + response.message;
            }
            if (!Em.isEmpty(response.errorDescription)) {
              res = Em.String.htmlSafe(res + '<br />' + Locale.renderGlobals('shared.errorFormatter.description') + ' ' + response.errorDescription);
            }
            if (!Em.isEmpty(response.server)) {
              res = Em.String.htmlSafe(res + '<br />' + Locale.renderGlobals('shared.errorFormatter.server') + ' ' + response.server);
            }
            if (!Em.isEmpty(response.errorCode)) {
              res = Em.String.htmlSafe(res + '<br />' + Locale.renderGlobals('shared.errorFormatter.errorCode') + ' ' + response.errorCode);
            }
          }
        } catch (e) { }
      }
      return res;
    },

    formatHyperlinkToNewPage: function(url) {
      var href = '';
      if (!Em.isEmpty(url)) {
        href = Em.String.htmlSafe('<a href="' + url + '" target="_blank">' + url + '</a>');
      }
      return href;
    },

    // TODO: Cleanup
    camelCaseToTitleCase: function(str) {
      return this.acronymsCapitalized(str.dasherize().replace(/-/g, ' ').replace(/(?:^|\s)\w/g, function(match) {
        return match.toUpperCase();
      }));
    },

    acronymsCapitalized: function(str) {
      return str.replace(/\b(utc|cpu|cpus|os|ip)\b/gi, function(abbr) {
        var lowerCaseAbbr = abbr.toLowerCase();
        switch(lowerCaseAbbr) {
          case 'cpus':
            return 'CPUs';
            break;
          case 'utc':
            return '(UTC)';
            break;
          default:
            return abbr.toUpperCase();
        }
      });
    },

    /**
     * Pad a number with leading zeros up to the specified number of digits (max 10)
     */
    pad: function (num, size) {
      var s = "000000000" + num;
      return s.substr(s.length-size);
    },

    /**
     * Remove leading and trailing spaces from a string.
     *
     * @param str string to be trimmed
     * @return string with any leading and trailing spaces removed
     */
    trim: function(str) {
      return str.replace(/^\s\s*/, '').replace(/\s\s*$/, '');
    },

    safeTrim: function(str) {
      if (typeof str === 'string') {
        str = str.trim();
      }
      return str;
    },

    toString: function(val) {
      return '' + val;
    },

    toStringOrNA: function(val) {
      var trimmedVal = val && typeof val === 'string' ? val.trim() : val;
      return (Em.isEmpty(trimmedVal) ? Locale.notAvailable() : '' + val);
    },

    toStringOrUnknownError: function(val) {
      var trimmedVal = val && typeof val === 'string' ? val.trim() : val;
      if(typeof val === 'number') {
        return Locale.renderGlobals('shared.unknownError') + ' (' + val + ')';
      }
      return (Em.isEmpty(trimmedVal) ? Locale.notAvailable() : '' + val);
    },

    showIdentifierLink: function(val) {
      // hardcoded the link href based on the old cc link
      // TODO to be updated after an endpoint is implemented
      var onclick = ''; // 'onclick="alert(0); return false;"';
      return  Em.String.htmlSafe('<a ' + onclick + ' href="\/Pages\/Administration\/ComputerSummary.aspx?esn=' + val + '">' + val + '</a>');
    },

    // TODO not used anywhere
    formatFpFileVersion: function(version) {
      return version ? [
        version['major'],
        version['minor'],
        version['revision'],
        version['build']
      ].join('.') : '';
    },

    /**
     * Formatter used for fingerprints view
     * @param suite
     * @returns {*}
     */
    formatSuiteName: function(suite) {
      return (!Em.isEmpty(suite)) ? suite : Locale.renderGlobals('shared.standaloneApplications').toString();
    },

    /**
     * Formatter used for software catalog view
     * @param publisher
     * @returns {*}
     */
    formatPublisherName: function(publisher) {
      return (!Em.isEmpty(publisher)) ? publisher : Locale.renderGlobals('shared.unknownPublisher').toString();
    },

    /**
     * Formatting the fingerprint db name column in the fingerprints view
     * @param dbType
     */
    formatTitleDbName: function(dbType) {
      return (dbType === 'Baseline' || Em.isEmpty(dbType))
          ? Em.String.htmlSafe('<span title="' +
              Locale.renderGlobals('ccSoftwareTitling.catalogSoftwarePage.body.appNotDefinedTooltip') + '">' +
              Locale.renderGlobals('shared.baseline') + '</span>')
          : dbType;
    },

    formatTitleDbType: function(dbType) {
      return dbType !== 'Customer' ? Locale.renderGlobals('shared.predefined') : Locale.renderGlobals('shared.customized');
    },

    formatReportType: function(bool) {
      return bool === true ? Locale.renderGlobals('shared.predefined') : Locale.renderGlobals('shared.customized');
    },

    // TODO not used anywhere
    formatTitleDbIsEnabled: function(isEnabled) {
      return isEnabled ? 'Active' : '';
    },

    // TODO not used anywhere
    formatReportCount: function(count) {
      return count + (count === 1 ? ' Device' : ' Devices');
    },

    formatOwnership: function(ownership) {
      switch (ownership) {
        case 0:
        case null:
          return Locale.renderGlobals('shared.baseline');
        default:
          return ownership;
      }
    },

    formatChangeType: function(type) {
      var iconClass;
      var titleContent;

      switch (type) {
        case 0:
          iconClass = 'icon-item-new';
          titleContent = Locale.renderGlobals('ccDevice.devicePage.historyTab.changeHistory.tooltipNewItemAdded');
          break;
        case 1:
          iconClass = 'icon-item-changed';
          titleContent = Locale.renderGlobals('ccDevice.devicePage.historyTab.changeHistory.tooltipItemChanged');
          break;
        case 2:
          iconClass = 'icon-minus';
          titleContent = Locale.renderGlobals('ccDevice.devicePage.historyTab.changeHistory.tooltipItemRemoved');
          break;
      }
      return Em.String.htmlSafe('<span data-tooltip-attr="title" data-sticky-tooltip="true" title="' + titleContent
          + '" class="' + iconClass + ' icon-font-size-m color-primary-icon"></span>');
    },

    capitaliseFirstLetter: function(string) {
      return string.charAt(0).toUpperCase() + string.slice(1);
    },

    decimalToString: function(value) {
      value = value.toString();
      var decArr = value.split('.');
      var valueBeforeDot = decArr[0];
      var valueAfterDot = '';
      if(decArr.length > 1) {
        // Ignore others. 1234.56.78. Only 56 is used
        valueAfterDot = decArr[1];
      }

      var pattern = /(-?\d+)(\d{3})/;
      while (pattern.test(valueBeforeDot))
        valueBeforeDot = valueBeforeDot.replace(pattern, "$1,$2");
      return valueBeforeDot + (decArr.length > 1 ? '.' : '') + valueAfterDot;
    },

    stringToDecimal: function(value) {
      // Replace all commas
      value = value.replace(/,/g, '');

      // http://stackoverflow.com/questions/8140612/remove-all-dots-except-the-first-one-from-a-string
      // matching all substrings consisting of either (a) the start of the string + any potential number of non-dot characters + a dot,
      // or (b) any existing number of non-dot characters. When we join all matches back together, we have essentially removed any dot except the first.

      // Remove all dots except the first one from a string
      var match = value.match(/^[^.]*\.|[^.]+/g);
      return match ? match.join('') : value;
    },

    singularizeNestedLabels: function(label) {
      var _label;
      var dictionaryOfNestedCollectionNames = {
        memories: 'memory',
        cdRoms: 'cdRom',
        disks: 'disk',
        displays: 'display',
        keyboards: 'keyboard',
        networkAdapters: 'networkAdapter',
        pointingDevices: 'pointingDevice',
        printers: 'printer',
        sounds: 'soundDevice',
        volumes: 'driveVolume'
      };

      var splitLabel = label.split('.');

      if(splitLabel.length > 1) {
        var firstPartOfLabel = splitLabel[0];
        var secondPartOfLabel = splitLabel[1];
        var modifiedFirstPartOfLabel;

        if(modifiedFirstPartOfLabel = dictionaryOfNestedCollectionNames[firstPartOfLabel]) {
          _label = modifiedFirstPartOfLabel+'.'+secondPartOfLabel;
        }
      }
      else {
        if(modifiedFirstPartOfLabel = dictionaryOfNestedCollectionNames[label]) {
          _label = modifiedFirstPartOfLabel;
        }
      }

      return _label;
    }
  });
});
