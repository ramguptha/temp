define([
  'ember',
  './lib/namespace',

  'locale'
], function(
  Em,
  AmUserFormatter,

  Locale
) {
  'use strict';

  // am-user-formatter
  // =========
  //
  // The am-user-formatter package contains a collection of filter functions for transforming values into formatted strings.

  return AmUserFormatter.reopen({

    getOsText: function (osPlatform, isTablet, model) {
      // Default PC
      var text = Locale.renderGlobals('amUserSelfServicePortal.deviceDetails.pcPlatform');

      // All devices
      switch (osPlatform) {
        case 1:  // Mac OS X
          text = Locale.renderGlobals('amUserSelfServicePortal.deviceDetails.appleMacPlatform');
          break;
        case 10: // iOS
          text = isTablet ? Locale.renderGlobals('amUserSelfServicePortal.deviceDetails.iOsTabletPlatform') : Locale.renderGlobals('amUserSelfServicePortal.deviceDetails.iOsPhonePlatform');
          // TODO localization ???, it works for Jp
          if (model.toLowerCase().search('apple tv') != -1) {
            text = Locale.renderGlobals('amUserSelfServicePortal.deviceDetails.appleTvPlatform');
          }
          break;
        case 11: // Android
          text = isTablet ? Locale.renderGlobals('amUserSelfServicePortal.deviceDetails.androidTabletPlatform') : Locale.renderGlobals('amUserSelfServicePortal.deviceDetails.androidPhonePlatform');
          break;
        case 12: // Windows Phone (EAS)
        case 13: // Windows Phone
          text = Locale.renderGlobals('amUserSelfServicePortal.deviceDetails.windowsPhonePlatform');
          break;
      }

      return text;
    },

    getOsClass: function (osPlatform, isTablet, model) {
      // Default PC
      var text = 'icon-pc-laptop computer-laptop';

      // All devices
      switch (osPlatform) {
        case 1:  // Mac OS X
          text = 'icon-mac-laptop computer-laptop';
          break;
        case 10: // iOS
          text = isTablet ? 'icon-ios-tablet' : 'icon-ios-phone';
          // TODO localization ???, it works for Jp
          if (model.toLowerCase().search('apple tv') != -1) {
            text = 'icon-apple-tv';
          }
          break;
        case 11: // Android
          text = isTablet ? 'icon-android-tablet' : 'icon-android-phone';
          break;
        case 12: // Windows Phone (EAS)
        case 13: // Windows Phone
          text = 'icon-windows-phone';
          break;
      }

      return text;
    },

    getListOsClass: function (osPlatform, isTablet, model) {
      // Default PC
      var text = 'icon-menu-pc-laptop';

      // All devices
      switch (osPlatform) {
        case 1:  // Mac OS X
          text = 'icon-menu-mac-laptop';
          break;
        case 10: // iOS
          text = isTablet ? 'icon-menu-ios-tablet' : 'icon-menu-ios-phone';
          // TODO localization ???, it works for Jp
          if (model.toLowerCase().search('apple tv') != -1) {
            text = 'icon-menu-apple-tv';
          }
          break;
        case 11: // Android
          text = isTablet ? 'icon-menu-android-tablet' : 'icon-menu-android-phone';
          break;
        case 12: // Windows Phone (EAS)
        case 13: // Windows Phone
          text = 'icon-menu-windows-phone';
          break;
      }

      return text;
    },

    isPhoneDevice: function (osPlatform, model) {
      var isPhone = false;

      // All devices
      switch (osPlatform) {
        case 10: // iOS
          isPhone = true;
          // TODO localization ???, it works for Jp
          if (model.toLowerCase().search('apple tv') != -1) {
            isPhone = false;
          }
          break;
        case 11: // Android
        case 12: // Windows Phone (EAS)
        case 13: // Windows Phone
          isPhone = true;
          break;
      }

      return isPhone;
    },

    isAndroidDevice: function (osPlatform) {
      return osPlatform === 11
    },

    isIOsDevice: function (osPlatform) {
      return osPlatform === 10
    },

    isAppleTvDevice: function (osPlatform, model) {
      return osPlatform === 10 &&
            model.toLowerCase().search('apple tv') != -1;
    },

    isMacDevice: function (osPlatform, isComputer) {
      return isComputer && osPlatform === 1;
    },

    // TODO verify - Not used
    getDeviceBigIconPath: function (osPlatform, isTablet, model) {
      // Mobile devices
      var iconFolder = '../packages/platform/desktop/icons/devices/';
      switch (osPlatform) {
        case 1:  // Mac OS X (currently don't have a specific Mac OS X device icon, but won't get this anyway for Mobile devices?)
        case 10: // iOS
          // TODO localization ???, it works for Jp
          if (model.toLowerCase().search('apple tv') != -1) {
            return iconFolder + 'apple-tv-84.png';
          }
          return iconFolder + (isTablet ? 'ipad-84.png' : 'iphone-84.png');
        case 2: // Windows (currently assume this will be used for Windows8)
          return '../packages/platform/desktop/icons/os-logos/' + '64-windows-8.png';  // TODO update when windows device icons available
        case 11: // Android
          return iconFolder + (isTablet ? 'android-tablet-84.png' : 'android-phone-84.png');
        case 12: // Windows Phone (EAS)
        case 13: // Windows Phone
          return '../packages/platform/desktop/icons/os-logos/' + '64-windows-8phone.png';// TODO update when windows phone device icons available
      }

      // Computers
      return this.getIconPathOsPlatformComputers('32', this.get('data.deviceType'));
    },

    // TODO verify
    getOsSmallIconPath: function (osPlatform) {
      var iconFolder = '../packages/platform/desktop/icons/os-logos/';
      var version = 16;

      // Mobile devices
      switch (osPlatform) {
        case 1:  // Mac OS X
          return iconFolder + version + '-apple-osx.png';
        case 10: // iOS
          return iconFolder + version + '-apple-ios-b.png';
        case 2: // Windows (currently assume this will be used for Windows8)
          return iconFolder + version + '-windows-8.png';
        case 11: // Android
          return iconFolder + version + '-android.png';
        case 12: // Windows Phone (EAS)
        case 13: // Windows Phone
          return iconFolder + version + '-windows-8phone.png';
      }

      // Computers
      return this.getIconPathOsPlatformComputers(version, osPlatform);
    },

    // TODO verify
    getIconPathOsPlatformComputers: function (version, osPlatform) {
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
    }

  });
});
