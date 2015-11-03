define([
  'ember'
], function(
  Em
  ) {
  'use strict';

  // IpType
  // ========
  // Class for handling ip addresses
  // As of now, only IPv4 addresses are handled
  return {

    // given a properly formatted IPv4 address string ( 255.1.1.1 ) convert it to an int
    // if string is invalid - return null
    IPv4StringToInt: function(IPv4String) {
      if (!this.isValid(IPv4String)) {
        return null;
      }

      var result = 0;

      // magic number 4 because 4 octets in an IPv4 address
      for (var i = 0; i < 4; i++) {
        result += IPv4String.split('.')[i] * Math.pow(256, 3 - i);
      }

      return result;
    },

    // given a IPv4 int ( 3232238100 ) convert it to an IPv4 formatted address
    IPv4IntToString: function(IPv4Int) {
      if (isNaN(IPv4Int) || IPv4Int < 0 || IPv4Int > 4294967295) {
        return null;
      }

      // check for boundaries and return min and max IP values.
      /*if (IPv4Int < 0) {
        return "0.0.0.0";
      } else {
        if (IPv4Int > 4294967295) {
          return "255.255.255.255";
        }
      }*/

      var result = "";
      for (var i = 0; i < 3; i++) {
        result = IPv4Int % 256 + (result ? "." : "") + result;
        IPv4Int = (IPv4Int / 256).toString().split('.')[0];
      }

      return IPv4Int + "." + result;
    },

    /**.
     * Check validity of the IP address
     * @param ip
     * @returns {boolean}
     */
    isValid: function(ip) {
      if (Em.isNone(ip) || Em.isEmpty(ip) || typeof ip !== "string") {
        return false;
      }

      var x = ip.split("."), x1, x2, x3, x4;
      if (x.length === 4) {
        x1 = parseInt(x[0], 10);
        x2 = parseInt(x[1], 10);
        x3 = parseInt(x[2], 10);
        x4 = parseInt(x[3], 10);

        if (isNaN(x1) || isNaN(x2) || isNaN(x3) || isNaN(x4)) {
          return false;
        }

        if ((x1 >= 0 && x1 <= 255) && (x2 >= 0 && x2 <= 255) && (x3 >= 0 && x3 <= 255) && (x4 >= 0 && x4 <= 255)) {
          return true;
        }
      }
      return false;
    }
  }
});