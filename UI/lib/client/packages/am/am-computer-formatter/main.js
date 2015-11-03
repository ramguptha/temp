define([
  'ember',
  './lib/namespace',
  'formatter',

  'locale'
], function(
  Em,
  AmComputerFormatter,
  Formatter,

  Locale
) {
  'use strict';

  // am-computer-formatter
  // The am-computer-formatter package contains a collection of filter functions for transforming values into formatted strings.

  return AmComputerFormatter.reopen({

    getIconPathOsPlatform: function (version, osPlatform) {
      var iconFolder = '../packages/platform/desktop/icons/os-logos/';
      // Unknown Platform by default. See InfoItemEnumerations.xml
      var iconPath = iconFolder + version + '-unknown-os.png';

      if($.inArray(osPlatform,[1, 2]) > -1) {
        iconPath = iconFolder + version + '-apple-osx-c.png';
      } else if ($.inArray(osPlatform,[200]) > -1) {
        iconPath = iconFolder + version + '-windows-generic.png';
      } else if ($.inArray(osPlatform,[3,7,8,9]) > -1) {
        iconPath = iconFolder + version + '-windows-2000.png';
      } else if ($.inArray(osPlatform,[10,11,12,13,27,28,29,30,31,32]) > -1) {
        iconPath = iconFolder + version + '-windows-2003.png';
      } else if ($.inArray(osPlatform,[33,34,35,36,37,38,39,40,41,42,43,44,45,46,47,48]) > -1) {
        iconPath = iconFolder + version + '-windows-2008.png';
      } else if ($.inArray(osPlatform,[49,50,51,52,53,54,55,56,57,58,59,60,61,62]) > -1) {
        iconPath = iconFolder + version + '-windows-2008r2.png';
      } else if ($.inArray(osPlatform,[4,5,6,14,15,16]) > -1) {
        iconPath = iconFolder + version + '-windows-xp.png';
      } else if ($.inArray(osPlatform,[17,18,19,20,21,22,23,24,25,26]) > -1) {
        iconPath = iconFolder + version + '-windows-vista.png';
      } else if ($.inArray(osPlatform,[63,64,65,66,67,68,69,70,71,72,73]) > -1) {
        iconPath = iconFolder + version + '-windows-7.png';
      } else if ($.inArray(osPlatform,[74,75,76,77,78,79,110,111,112,113,114,115]) > -1) {
        iconPath = iconFolder + version + '-windows-8.png';
        // Windows 2012 Icons
      } else if ($.inArray(osPlatform,[80,81,82,83,84,85,86,87,88,89]) > -1) {
        iconPath = iconFolder + version + '-windows-2008.png';
        // Windows 2012 R2 Icons
      } else if ($.inArray(osPlatform,[116,117,118]) > -1) {
        iconPath = iconFolder + version + '-windows-2008r2.png';
      } else if ($.inArray(osPlatform,[100, 101, 102, 103, 104, 105]) > -1) {
        iconPath = iconFolder + version + '-linux-generic.png';
      }
      return iconPath;
    },

    getIconClassFreezeStatus: function (computerDeviceFreezeStatus) {
      var attributeClass;
      switch (computerDeviceFreezeStatus) {
        // 'None (Idle)'
        case 0:
          attributeClass = 'icon-checkmark1';
          break;
        // 'Freeze Requested'
        case 1:
          attributeClass = 'icon-time-request';
          break;
        // 'Frozen Successfully'
        case 2:
          attributeClass = 'icon-passcode';
          break;
        // 'Unfreeze Requested'
        case 3:
          attributeClass = 'icon-time-request';
          break;
        // 'Unfrozen by User'
        case 4:
          attributeClass = 'icon-checkmark1';
          break;
        // 'Unfrozen by Admin'
        case 5:
          attributeClass = 'icon-checkmark1';
          break;
        // 'Freeze Error'
        case 6:
          attributeClass = 'icon-circle-attention';
          break;
        // 'Unfreeze Error'
        case 7:
          attributeClass = 'icon-circle-attention';
          break;
      }
      return attributeClass;
    },

    getIconClassCommandStatus: function (computerCommandStatus) {
      var attributeClass;
      switch (computerCommandStatus) {
        // 'Scheduled'
        case 0:
          attributeClass = 'icon-clock in-grid-icon status-scheduled';
          break;
        // 'Executing'
        case 1:
          attributeClass = 'icon-recycle in-grid-icon status-executing';
          break;
        // 'Finished successfully'
        case 2:
          attributeClass = 'icon-checkmark1 in-grid-icon status-finished-successfully';
          break;
        // 'Finished with error'
        case 3:
          attributeClass = 'icon-circle-attention in-grid-icon status-finished-with-error';
          break;
        // 'Deferred'
        case 4:
          attributeClass = 'icon-triangle-attention in-grid-icon status-deferred';
          break;
        // 'Admin canceled'
        case 5:
          attributeClass = 'icon-triangle-attention in-grid-icon status-admin-canceled';
          break;
        // 'Transferring file to server'
        case 6:
          attributeClass = 'icon-end-of-life in-grid-icon status-transferring-file-to-server';
          break;
      }
      return attributeClass;
    },

    isMacPlatform: function(osPlatformNumber) {
      return $.inArray(osPlatformNumber,[1, 2]) > -1;
    },

    isWinPlatform: function(osPlatformNumber) {
      return $.inArray(osPlatformNumber,
          [200,
          3,7,8,9,
          10,11,12,13,27,28,29,30,31,32,
          33,34,35,36,37,38,39,40,41,42,43,44,45,46,47,48,
          49,50,51,52,53,54,55,56,57,58,59,60,61,62,
          4,5,6,14,15,16,
          17,18,19,20,21,22,23,24,25,26,
          63,64,65,66,67,68,69,70,71,72,73,
          74,75,76,77,78,79,110,111,112,113,114,115,
          80,81,82,83,84,85,86,87,88,89,
          116,117,118]) > -1;
    },

    // Convert intervals
    // return string in this format: 9 days, 00:16 (another example: 3 years, 8 months, 12 days, 00:16)
    formatIntervalInSecsCombined: function(diffSec){
      if (Em.isNone(diffSec)) {
        return Locale.notAvailable();
      } else {
        return this.formatIntervalInSecsFromCurrentDate(diffSec);
      }
    },

    // Convert difference between current date and parameter 'date'
    // return string in this format: 9 days, 00:16 (another example: 3 years, 8 months, 12 days, 00:16)
    formatIntervalInSecsFromCurrentDate: function(date){
      if (Em.isNone(date) || date.getTime() === 0) {
        return Locale.notAvailable();
      } else {
        var diffSec = (new Date().getTime() - date.getTime()) / 1000;
        return Formatter.formatIntervalInSecs(diffSec) + ', ' + Formatter.formatIntervalInHoursMinutesFromSecs(diffSec);
      }
    }

  });
});
