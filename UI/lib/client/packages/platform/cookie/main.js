define([
  'packages/platform/regex'
], function(
  Regex
) {
  'use strict';

  // Cookie
  // ======
  //
  // A simple cookie management module.
  return {

    // Confirm that a key or value meets cookie formatting requirements
    isValid: function(str) {
      return ('string' === typeof(str)) && !str.match(/[ ;]/);
    },

    // Get the value corresponding to the provided cookie
    read: function(key) {
      if (!this.isValid(key)) {
        throw ['Invalid cookie key', key];
      }

      var match = document.cookie.match(new RegExp('[^;\\s]*' + Regex.esc(key) + '=([^; ]*)'));

      return (null === match || undefined === match) ? undefined : match[1];
    },

    // Write a value for the given key. Returns what was written.
    //
    // Options: 
    //
    // - durationInSeconds: if specified and greater than zero, cookie will have that duration. Otherwise it will last for the browser session.
    // - path: used if specified, otherwise "/"
    write: function(key, value, options) {
      if (!this.isValid(key) || !this.isValid(value)) {
        throw ['Invalid key or value', key, value, options];
      }

      if (null === options || undefined === options) {
        options = {};
      }

      // Your basic cookie.
      var serializedPair = key + '=' + value;

      // Optional duration.
      var durationInSeconds = options.durationInSeconds;
      if (durationInSeconds > 0) {
        serializedPair = serializedPair + '; max-age=' + durationInSeconds;
      }

      // Optional path.
      var path = options.path;
      if (null === path || undefined === path) {
        path = '/';
      }
      serializedPair = serializedPair + '; path=' + path; 

      // If the protocol is secure, so should the cookie be.
      if ('https:' === location.protocol) {
        serializedPair = serializedPair + '; secure';
      }

      document.cookie = serializedPair;

      return value;
    },

    // Buh-bye. Note that to clear a cookie, the exact same path and domain must be used.
    clear: function(key, options) {
      if (!this.isValid(key)) {
        throw ['Invalid cookie key', key];
      }

      if (null === options || undefined === options) {
        options = {};
      }

      var serializedPair = key + '=; max-age=0';

      // Optional path.
      var path = options.path;
      if (null === path || undefined === path) {
        path = '/';
      }
      serializedPair = serializedPair + '; path=' + path; 

      document.cookie = serializedPair;

      return key;
    }
  };
});
