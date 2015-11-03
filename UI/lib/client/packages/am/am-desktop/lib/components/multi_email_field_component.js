define([
  'ember'
], function (
    Em
    ) {
  'use strict';

  return Em.TextField.extend({
    EMAIL_INPUT_ERROR_MESSAGE: 'shared.validationMessages.invalidMultiEmailAddress'.tr(),
    EMAILREGEX: /^[^!#$%&'*+/\\=?^`{|}~(),:;<>"[\]]+@[^!#$%&'*+/\\=?^`{|}~(),:;<>"[\]]+\.[^!#$%&'*+/\\=?^`{|}~(),.:;<>"[\]]+$/i,

    maxlength: '100',
    errorMessage: '',

    verifyEmail: function () {
      // Throw error if emails are separated by email
      // or any email address contains a space character
      var validateEmail = function(email, self) {
        var value = email.trim();
        return self.get('EMAILREGEX').test(value) && !value.match(/ /);
      };

      var self = this;
      var value = this.get('value').toString();
      var isValid = true, errorMessage = '';

      if(!Em.isEmpty(value)) {
        // We allow both semicolon and comma as delimiters.
        // split by the email delimiter then filter out the empty array members
        // Make sure the entered character is not only the delimiter character
        if (value.match(/,|;/) && value.indexOf('@') !== -1) {
          value.split(/,|;/).filter(function (i) {
            return i;
          }).map(function (email) {
            isValid = validateEmail(email, self);
          });
        } else {
          isValid = validateEmail(value, self);
        }
      }

      this.set('errorMessage', isValid ? '' : this.get('EMAIL_INPUT_ERROR_MESSAGE'));
    }.observes('value')
  });
});
