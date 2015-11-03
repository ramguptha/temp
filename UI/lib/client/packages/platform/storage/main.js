define([
], function(
 ) {
  'use strict';

  // Storage
  // =============
  //
  // A simple storage management module to handle both Local and Session storages.
  return {

    // Confirm that a key is valid
    isValid: function(str) {
      return ('string' === typeof(str)) && !str.match(/[ ]/);
    },

    // Get the value corresponding to the provided store key
    read: function(key, persist) {
      if (!this.isValid(key)) {
        throw ['Invalid storage key!', key];
      }

      var obj = null;

      if(this.isStorageSupported()) {
        if(Em.isNone(persist) || persist === false) {
          obj = JSON.parse(sessionStorage[key]);
        }
        else {
          try {
            obj = JSON.parse(localStorage[key]);
          } catch (e) {
            obj = null;
          }
        }
      }
      else{
        // Sorry! No web storage support..
        throw ['Cannot read value from storage. Storage is not supported in this browser.', key, persist];
      }
      return obj;
    },

    // Write a value for the given key. Returns what was written.
    //
    // Options: 
    //
    // - persist if set to true, the value will be saved in local storage. By default it is saved to session storage
    write: function(key, value, persist) {
      if (Em.isNone(key)) {
        return null;
      }

      if (!this.isValid(key)) {
        throw ['Invalid key', key, value, persist];
      }

      if(this.isStorageSupported()) {
        if(Em.isNone(persist) || persist === false) {
          sessionStorage.setItem(key, JSON.stringify(value));
        }
        else {
          localStorage.setItem(key, JSON.stringify(value));
        }
      }
      else{
        // Sorry! No web storage support..
        throw ['Cannot write value to storage. Storage is not supported in this browser.', key, value, persist];
      }

      return JSON.stringify(value);
    },

    // Clear the key from local storage
    // Please specify the scope by persist flag.
    // If flag is set to true localStorage will be cleared; otherwise sessionStorage will be cleared.
    clear: function(key, persist) {
      if (!this.isValid(key)) {
        throw ['Invalid key', key];
      }

      if(this.isStorageSupported()) {
        if(Em.isNone(persist) || persist === false) {
          sessionStorage.removeItem(key);
        }
        else {
          localStorage.removeItem(key);
        }
      }
      else{
        // Sorry! No web storage support..
        throw ['Cannot clear value from storage. Storage is not supported in this browser.', key, persist];
      }

      return key;
    },

    isStorageSupported: function() {
      return (typeof(Storage) !== 'undefined');
    }
  };
});
