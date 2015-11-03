define([
  'ember'
], function(
  Em
) {
  'use strict';

  // Original version does not work with type = "text". There is no property valueAsNumber. maxlength works on chrome.
  // Original version does not work in wizard mode. Number field is empty after click back button and return to the page with this TextField. This bug is fixed here.
  //\ui\lib\client\packages\desktop\lib\views\number_field_view.js

  // There are som difference between number_field_view and this version. Keep them separately
  // The difference is in type="text" and some bugs fixing
  return Em.TextField.extend({
    init: function() {
      this._super();
      this.updateNumberOnParsedValueChange();
      this.get('attributeBindings').push('min', 'max', 'step');
    },

    didInsertElement: function() {
      var self = this;
      /*
      * Taken from:
      * http://stackoverflow.com/questions/995183/how-to-allow-only-numeric-0-9-in-html-inputbox-using-jquery
      */
      this.$().keydown(function(event) {
        // Allow: backspace, delete, tab, escape, enter. (period is not allowed)
        if ( $.inArray(event.keyCode,[46,8,9,27,13]) !== -1 ||
          // Allow: Ctrl+A
            (event.keyCode == 65 && event.ctrlKey === true) ||
          // Allow: home, end, left, right
            (event.keyCode >= 35 && event.keyCode <= 39) ||
          //Ensure that it is a number
          (!event.shiftKey && !event.ctrlKey && self.isNumberEvent(event))) {
          // let it happen, don't do anything
          return;
        }

        // stop Crl+Number keys
        if ((event.shiftKey || event.ctrlKey) && self.isNumberEvent(event)) {
          event.preventDefault();
          return;
        }

        // Allow: Ctrl+V. Change to Shift+Insert
        if(event.keyCode == 86 && event.ctrlKey === true) {
          event.keyCode = 45;
          event.ctrlKey = false;
          event.shift = true;
          return;
        }

        // Stop all not numbers
        if (!self.isNumberEvent(event)) {
          event.preventDefault();
        }

      });
    },

    isNumberEvent: function(event) {
      return ((event.keyCode >= 48 && event.keyCode <= 57) || (event.keyCode >= 96 && event.keyCode <= 105));
    },

    type: 'number',
    min: '0',
    max: '1000000000',
    step: '1',
    maxlength: '10',
    value: null,

    updateNumberOnParsedValueChange: function() {
      var value = this.get('value');
      var parsedValue = this.parseNumber(value);

      if (parsedValue === null) {
        this.set('value', '');
      }
    }.observes('value'),

    valuesDiffer: function(lval, rval) {
      return (this.isValidNumber(lval) && this.isValidNumber(rval) && lval !== rval) ||
          (this.isValidNumber(lval) !== this.isValidNumber(rval));
    },

    isValidNumber: function(value) {
      return 'number' === typeof(value) && !isNaN(value);
    },

    parseNumber: function(str) {
      if (Em.isEmpty(str)) {
        return null;
      }
      var parsed = Number(str);
      if (isNaN(parsed)) {
        return null;
      }
      return parsed;
    }
  });
});
