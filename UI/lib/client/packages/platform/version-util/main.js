define([
  'locale'

], function(
  Locale

) {
  'use strict';

  return {
    formatIntToVersion: function(int) {
      return [
          (String(int) >> 24) & 0xff,
          (String(int) >> 20) & 0xf,
          (String(int) >> 16) & 0xf
      ].join('.').replace(/(\.0){1}$/, '');
    },

    formatVersionToInt: function(version) {
      var splitVersion = String(version).split('.');

      return splitVersion[0] << 24 | splitVersion[1] << 20 | (splitVersion[2] || 0) << 16 | 1 << 15;
    },

    // Wrote some new functions to avoid existing code re-testing

    //Java script supports only 32 bit arithmetic. Split 64 number to 2 parts
    formatIntToFileVersion: function(versionHigh, versionLow) {
      var shortVersionFormat = versionHigh === 0;
      //shortVersionFormat - 1.2.3
      //longVersionFormat  - 1.2.3.4
      return shortVersionFormat ? this.formatShortFileVersion(versionLow) : this.formatLongFileVersion(versionHigh, versionLow);
    },

    formatShortFileVersion: function(version) {
      // 1.2.3    Max value: 99.16.16. Hex: 99FF
      // 1.2.3b4
      var betaFlag = version >> 12 & 0xf;
      var betaLetter = '';
      switch (betaFlag) {
        case 4:
          betaLetter = 'a';
          break;
        case 6:
          betaLetter = 'b';
          break;
        case 2:
          betaLetter = 'd';
          break;
        case 8:
          betaLetter = 'f';
          break;
      }

      var versionFirstNumberFlag = (version >> 24) & 0xf,
          versionAddFirstNumberFlag = (version >> 28) & 0xf,
          versionSecondNumberFlag = (version >> 20) & 0xf,
          versionThirdNumberFlag =  (version >> 16) & 0xf,
          versionForthNumberFlag = version & 0xfff;

      // Letter exist only with 4th number
      if(!versionForthNumberFlag) {
        betaLetter = '';
      }

      return version ? [
        versionAddFirstNumberFlag === 0 ? versionFirstNumberFlag : (versionForthNumberFlag ? versionFirstNumberFlag.toString() + versionAddFirstNumberFlag.toString() : versionAddFirstNumberFlag.toString() + versionFirstNumberFlag.toString()),
        versionSecondNumberFlag === 0 ? '' : versionSecondNumberFlag,
        Em.isEmpty(betaLetter) ?
          versionThirdNumberFlag :
          versionThirdNumberFlag + betaLetter + (versionForthNumberFlag === 0 ? '' : versionForthNumberFlag)
      ].join('.').replace(/(\.0){1}/g, '').replace(/(\.\.)/g, '.') : Locale.notAvailable();
    },

    formatLongFileVersion: function(versionHigh, versionLow) {
      if(!versionLow) {
        return Locale.notAvailable();
      }

      // 1.2.3.4

      // 1.2
      var high = [
        (versionHigh >> 16) & 0xffff,  //1
        versionHigh & 0xffff           //2
      ].join('.').replace(/(\.0){1}$/, '');

      // 3.4
      var low = [
        (versionLow >> 16) & 0xffff,  //3
        versionLow & 0xffff           //4
      ].join('.').replace(/(\.0){1}$/, '');


      // 1.2.3.4    '1.2' - high, '3.4' - low
      return high + '.' + low;
    },

    formatFileVersionToInt: function(version) {
      var splitVersionArray = String(version).split('.');

      // TODO Saving works correctly only with max 4094.4094.4094.4094

      return splitVersionArray.length == 4 ?
        // Long Version. '1.2' - high, '3.4' - low
        [splitVersionArray[0] << 16 | splitVersionArray[1], splitVersionArray[2] << 16 | splitVersionArray[3] ] :
        // Short version
        this.packShortVersion(splitVersionArray);
    },

    packShortVersion: function(splitVersionArray) {

      var isTwoParts = splitVersionArray.length === 2;
      var isTreeParts = splitVersionArray.length === 3;
      var versionString = isTreeParts ? splitVersionArray[2] : splitVersionArray[1];

      // Can be optimized
      var betaFlagPos = versionString.search('a');
      if(betaFlagPos === -1) {
        betaFlagPos = versionString.search('b');
        if(betaFlagPos === -1) {
          betaFlagPos = versionString.search('d');
          if(betaFlagPos === -1) {
            betaFlagPos = versionString.search('f');
          }
        }
      }

      var betaFlagLetter = betaFlagPos > -1 ? versionString.charAt(betaFlagPos) : null,
          betaFlag = null;
      if(betaFlagLetter) {
        switch (betaFlagLetter) {
          case 'a':
            betaFlag = 4;
            break;
          case 'b':
            betaFlag = 6;
            break;
          case 'd':
            betaFlag = 2;
            break;
          case 'f':
            betaFlag = 8;
            break;
        }
      }

      // From old code. This part "1 << 15" add "8000" (Hex)
      var thirdPart = isTwoParts ? (versionString || 0) << 20 | 1 << 15 : (versionString || 0) << 16 | 1 << 15;

      if(betaFlag) {
        var numberBefore = versionString.substring(0,betaFlagPos);
        var numberAfter = versionString.substring(betaFlagPos + 1, versionString.length);
        thirdPart = (isTreeParts ? ((numberBefore || 0) << 16) : ((numberBefore || 0) << 20)) |
                    (numberAfter || 0) |
                    betaFlag << 12;
      }

      // Samples:
      // 23.1
      // 45.1.2
      // 1.3b4
      // 1.15d99
      // 1.2.5b4
      // 99.15.15d99

      var version = [(
          // 1 number
         (  splitVersionArray[0].length === 1 ?
            splitVersionArray[0] << 24 :
            (splitVersionArray[0].charAt(1) << 24) | (splitVersionArray[0].charAt(0) << 28)  ) |
          // 2 number
         (isTreeParts ? splitVersionArray[1] : 0) << 20 |
          // 3 part (for 2 pars it is a second one)
         thirdPart)
      ];

      return version;
    },

    validateFileVersion: function(dataValue) {
      var SHORT_VERSION_REGEX = /^([0-9]?[0-9]\.)?(([0-9]|1[1-5])\.)(([0-9]|1[1-5])|([0-9]|1[1-5])[abdf][1-9][1-9]?)$/;

      // Description for SHORT_VERSION_REGEX:
      // ([0-9]?[0-9]\.) - first number is optional. Max 99. After that is dot
      // (([0-9]|1[1-5])\.) - can be one digit from 0 to 9, or 2 digits from 1 to 15. After that is dot
      // Here are 2 parts
      // (([0-9]|1[1-5])|([0-9]|1[1-5])[abdf]?[1-9]?[1-9])?
      // First part
      // ([0-9]|1[1-5]) -  can be one digit from 0 to 9, or 2 digits from 1 to 15.
      // Second part (optional)
      // ([0-9]|1[1-5])[abdf][1-9][1-9]?) The same, but together with one letter: abdf and one digit. The second digit is optional.
      // ? in the end means: the second part is optional
      // See another example: ui/lib/client/packages/am/am-desktop/lib/views/multi_phone_field_view.js

      var LONG_VERSION_REGEX = /^((6553[0-5]|655[0-2]\d|65[0-4]\d\d|6[0-4]\d{3}|[1-5]\d{4}|[1-9]\d{0,3}|0)\.){3}(6553[0-5]|655[0-2]\d|65[0-4]\d\d|6[0-4]\d{3}|[1-5]\d{4}|[1-9]\d{0,3}|0)$/;
      // Description for LONG_VERSION_REGEX:
      // from: http://regexlib.com/REDetails.aspx?regexp_id=1120
      // Original:
      //^(6553[0-5]|655[0-2]\d|65[0-4]\d\d|6[0-4]\d{3}|[1-5]\d{4}|[1-9]\d{0,3}|0)$

      // For 1.2
      var SHORTEST_VERSION_REGEX = /^([0-9]{1,2}\.(([0-9]|1[0-5])|([0-9]|1[1-5])[abdf][1-9][1-9]?))$/;


      var isValid = true;

      try {
        var length = dataValue.split('.').length;
        switch(length) {
          case 1:
          case 2:
            isValid = SHORTEST_VERSION_REGEX.test(dataValue.trim());
            break;
          case 3:
            // Short version 99.15.15 + beta symbols (a, b, d, f) 1.2.4b2 or long version 65535.65535.65535.65535
            // Supports X.X.X format. Max 99.15.15. Valid 99.15f15, 99.15.15f99
            isValid = SHORT_VERSION_REGEX.test(dataValue.trim());
            break;
          default:
            // Supports X.X.X.X format. Max 65535.65535.65535.65535
            isValid = LONG_VERSION_REGEX.test(dataValue);
            break;
        }

      } catch(e) {
        // Can be unavailable string: --
        isValid = false;
      }
      return isValid;
    }

  }
});
