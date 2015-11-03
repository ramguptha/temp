define([
  'ember'
], function (
    Em
    ) {
  'use strict';

  return Em.TextField.extend({
    EMAIL_INPUT_ERROR_MESSAGE: 'shared.validationMessages.invalidEmailAddress'.tr(),
    EMAILREGEX: /^[^!#$%&'*+/\\=?^`{|}~(),:;<>"[\]]+@[^!#$%&'*+/\\=?^`{|}~(),:;<>"[\]]+\.[^!#$%&'*+/\\=?^`{|}~(),.:;<>"[\]]+$/i,

    maxlength: '50',
    errorMessage: '',

    verifyEmail: function () {
      var value = this.get('value').trim();

      if (!Em.isEmpty(value) && (!this.get('EMAILREGEX').test(value) || value.match(/ /))) {
        this.set('errorMessage', this.get('EMAIL_INPUT_ERROR_MESSAGE'))
      } else {
        this.set('errorMessage', '');
      }
    }.observes('value')
  });
});
