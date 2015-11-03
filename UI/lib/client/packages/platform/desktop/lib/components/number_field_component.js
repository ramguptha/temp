define([
  'ember'
], function(
  Em
) {
  'use strict';

  return Em.Component.extend({
    tagName: 'input',
    attributeBindings: ['accept', 'autocomplete', 'autosave', 'dir', 'formaction', 'formenctype', 'formmethod', 'formnovalidate', 'formtarget', 'height', 'inputmode', 'lang', 'list', 'max', 'min', 'multiple', 'name', 'pattern', 'size', 'step', 'type', 'value', 'width'],

    classNames: ['input-field-number-file-size'],

    tPlaceholder: 'shared.placeholders.enterValue'.tr(),

    input: function(event) {
      this.set('value', event.target.value);
    },

    init: function() {
      this._super();
      this.updateValueOnNumberChange();
    },

    didInsertElement: function() {
      var self = this;
      this.$().attr('placeholder', self.get('tPlaceholder')).focus();
      /*
       * Taken from:
       * http://stackoverflow.com/questions/995183/how-to-allow-only-numeric-0-9-in-html-inputbox-using-jquery
       */
      this.$().keydown(function(event) {
        // Allow: backspace, delete, tab, escape, enter. (period is not allowed)
        if ( $.inArray(event.keyCode,[46,8,9,27,13]) !== -1 ||
            // Allow: Ctrl+A
          (event.keyCode == 65 && event.ctrlKey === true) ||
            //allow only < max number. (maxlength property doesn't work in chrome)
          (event.target.valueAsNumber < parseInt(self.get('max'))) ||
            // Allow: home, end, left, right
          (event.keyCode >= 35 && event.keyCode <= 39)) {
          // let it happen, don't do anything
        } else {
          // Ensure that it is a number and stop the keypress
          if (event.shiftKey || (event.keyCode < 48 || event.keyCode > 57) && (event.keyCode < 96 || event.keyCode > 105 ) || (event.target.valueAsNumber >= parseInt(self.get('max')))) {
            event.preventDefault();
          }
        }
      });

      // required for FF and IE to move the cursor to the end of the input for when this components gets re-rendered when the user is typing
      this.$().focus().val('').val(this.get('value'));
    },

    number: null,
    type: 'number',
    min: '0',
    max: '1000000000',
    step: '1',
    maxlength: '10',

    updateValueOnNumberChange: function() {
      var value = this.get('value');
      var parsedValue = this.parseNumber(value);
      var number = this.get('number');

      if (this.valuesDiffer(parsedValue, number)) {
        this.set('value', this.isValidNumber(number) ? '' + number : '');
      }
    }.observes('number'),

    updateNumberOnParsedValueChange: function() {
      var value = this.get('value');
      var parsedValue = this.parseNumber(value);
      var number = this.get('number');

      if (this.valuesDiffer(parsedValue, number)) {
        this.set('number', parsedValue);
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
