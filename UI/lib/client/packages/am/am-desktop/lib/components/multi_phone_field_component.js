define([
  'ember'
], function(
    Em
    ) {
  'use strict';

  return Em.TextField.extend({

    PHONE_INPUT_ERROR_MESSAGE: 'shared.validationMessages.invalidMultiPhoneNumber'.tr(),
    PHONE_REGEX: /^([\s+-]*[0-9]{1,2}[-. ]?)?\(?([0-9]{3})\)?[-. ]?([0-9]{3})[-. ]?([0-9]{4})$/,

    // RegEx supports:
    // +1 (604) 555 7777
    // +1-(604)-555-7777
    // +1.(604).555.7777
    // +1.604.555.7777
    // 1.604.555.7777
    // 604-555-7777
    // 604.555.7777

    // Description for Reg Ex:
    // Example +1-(604)-555-7777
    // /^ - begin
    // ([\s+-]*[0-9]{1,2}[-. ]?)  "+1-"  - space, '+' or '-' with number [0-9]. One or 2 symbols for number. After that can be '-', '.' or space (optional)
    // ? - above line is optional, can be number without +1 prefix
    // \(?([0-9]{3})\)?[-. ]?     "(604)-"  \(? - optional bracket , \)? - another optional bracket, ([0-9]{3}) - number 3 symbols, after number [-. ]? - optional '-', '.' or space
    // ([0-9]{3})[-. ]?           "555-"    - number 3 symbols, [-. ]? - optional '-', '.' or space
    // ([0-9]{4})                 "7777"    - number 4 symbols
    // $/ - end

    maxTextSize: 255,
    errorMessage: '',
    isValidationError: false,

    verifyPhone: function(){
      var self = this;
      var value = this.get('value').toString();
      var isValid = true;

      if(!Em.isEmpty(value)) {

        var maxSize = this.get('maxTextSize');
        if (value.length > maxSize) {
          value = value.substring(0, maxSize);
          this.set('value', value)
        }

        // split by the phone delimiter then filter out the empty array members
        // Make sure the phone number does not contain only the delimiter character
        value = value.split(',').filter(function (i) {
          return i;
        });

        if (!Em.isEmpty(value)) {
          isValid = value.every(function (phone) {
            return self.get('PHONE_REGEX').test(phone.trim());
          });
        }
        else {
          isValid = false;
        }
      }


      this.set('errorMessage', isValid ? '' : this.get('PHONE_INPUT_ERROR_MESSAGE'));

      this.set('isValidationError', !isValid);

    }.observes('value')
  });
});
