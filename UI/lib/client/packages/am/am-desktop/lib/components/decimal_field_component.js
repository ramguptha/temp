define([
  'ember',
  'decimal'
], function(
  Em,
  Decimal
) {
  'use strict';

  // Decimal control
  return Em.TextField.extend({
    type: 'text',
    min: '0',
    max: '1000000000',
    maxlength: '100',
    value: null,

    dataType: 'decimal',

    init: function() {
      this._super();

      this.get('attributeBindings').push('min', 'max');
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
          (!event.shiftKey && !event.ctrlKey && self.isDecimalEvent(event))) {
          // let it happen, don't do anything
          return;
        }

        // stop Crl+Number keys
        if ((event.shiftKey || event.ctrlKey) && self.isDecimalEvent(event)) {
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

        // Stop all not decimals
        if (!self.isDecimalEvent(event)) {
          event.preventDefault();
        }

      });
    },

    isInvalidData: function() {
      return this.parseDecimal(this.get('value')) === null;
    }.property('value'),

    isDecimalEvent: function(event) {
      // 188 - comma
      // 190, 110 - dots from keyboard and from num pad
      // dot is for decimal and bytes
      return (  (event.keyCode >= 48 && event.keyCode <= 57) || (event.keyCode >= 96 && event.keyCode <= 105) ||
              // commas
              ((this.get('dataType') === 'decimal') && event.keyCode === 188) ||
              // dots
              ((this.get('dataType') !== 'bytes') && (event.keyCode === 190 || event.keyCode === 110))  );
    },

    valuesDiffer: function(lval, rval) {
      return (this.isValidDecimal(lval) && this.isValidDecimal(rval) && lval !== rval) ||
          (this.isValidDecimal(lval) !== this.isValidDecimal(rval));
    },

    isValidDecimal: function(value) {
      return 'number' === typeof(value) && !isNaN(value);
    },

    parseDecimal: function(str) {
      if (Em.isEmpty(str)) {
        return null;
      }
      var parsed = null;
      try {
        parsed = (this.get('dataType') === 'bytes') ? new Decimal(String(str)) : new Decimal(String(str).replace(/,/g, ''));
      } catch(e) {

      }
      if (!parsed || isNaN(parsed)) {
        return null;
      }
      return parsed;
    }
  });
});
