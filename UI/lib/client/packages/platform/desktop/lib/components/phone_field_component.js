define([
  'ember'
], function(
    Em
    ) {
  'use strict';

  return Em.TextField.extend({
    PHONE_INPUT_ERROR_MESSAGE: 'shared.validationMessages.invalidPhoneNumber'.tr(),
    PHONE_REGEX: /^[\s()+-]*([0-9][\s()+-]*){3,20}$/,
    
    maxlength: '50',

    errorMessage: '',

    verifyPhone: function(){
      if (!Em.isEmpty(this.get('value')) && !this.get('PHONE_REGEX').test(this.get('value'))) {
        this.set('errorMessage', this.get('PHONE_INPUT_ERROR_MESSAGE'))
      } else {
        this.set('errorMessage', '');
      }
    }.observes('value')
  });
});
