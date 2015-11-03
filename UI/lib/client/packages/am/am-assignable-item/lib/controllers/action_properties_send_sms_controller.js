define([
  'ember',
  'desktop',
  'am-desktop',

  './action_item_base_controller',
  '../views/action_item_base_modal_view',

  'text!../templates/action_properties_send_sms.handlebars'
], function (
  Em,
  Desktop,
  AmDesktop,

  ActionItemBaseController,
  ActionBaseModalView,

  template
) {

  // Action Properties Send Email Controller
  // ==================================
  //

  return ActionItemBaseController.extend({

    hasDynamicProperties: true,
    propertiesView: ActionBaseModalView.extend({
      defaultTemplate: Em.Handlebars.compile(template)
    }),

    helpId: 1060,

    isAllSupported: true,

    isPhoneNumberInvalid: false,
    phoneErrorMessage: null,

    phoneNumber: '',
    oldPhoneNumber: '',

    smsMessage: '',
    oldSmsMessage: '',

    messageMaxLength: function() {
      var maxSize = this.get('MAX_TEXT_SIZE');

      // adjust the maxSize for webkit browsers since they represent the new line character as two characters
      if(navigator.userAgent.indexOf('AppleWebKit') != -1 && !Em.isNone(this.get('smsMessage'))) {
        maxSize += this.get('smsMessage').split(/\r\n|\r|\n/).length - 1;
      }

      return maxSize;
    }.property('smsMessage'),

    messageLengthRemaining: function() {
      return this.getLengthRemaining(this.get('smsMessage'));
    }.property('smsMessage'),

    initialize: function(model) {
      this.set('isPhoneNumberInvalid', false);

      this._super(model);
    },

    dynamicPropertiesChanged: function() {
      if (!this.get('isInitializationDone')) { return; }

      var isInvalid = this.getIsEmpty() || this.get('isPhoneNumberInvalid');

      this.setProperties({
        isActionBtnDisabled: isInvalid || !this.getIsDirty(),
        isSaveAndAssignActionBtnDisabled: isInvalid
      });

    }.observes('name',
      'description',
      'isNameDuplicate',
      'iosChecked',
      'androidChecked',
      'windowsChecked',
      'smsMessage',
      'phoneNumber',
      'isPhoneNumberInvalid'),

    getIsEmpty: function() {
      return this.getBasicIsEmpty() ||
        this.getIsEmptyField(this.get('smsMessage')) ||
        this.getIsEmptyField(this.get('phoneNumber'));
    },

    getIsDirty: function() {
      return this.getBasicIsDirty() ||
        this.get('smsMessage') !== this.get('oldSmsMessage') ||
        this.get('phoneNumber') !== this.get('oldPhoneNumber');
    },

    setDynamicProperties: function(data) {
      var smsMessage = data.smsMessage,
        phoneNumber = data.phoneNumber;

      this.setProperties({
        phoneNumber: phoneNumber,
        oldPhoneNumber: phoneNumber,

        smsMessage: smsMessage,
        oldSmsMessage: smsMessage
      });
    },

    resetDynamicProperties: function() {
      var properties = {
        phoneNumber: '',
        smsMessage: ''
      };

      this.setDynamicProperties(properties);
    },

    getFormattedPropertyList: function() {
      return this.getProperties('phoneNumber smsMessage'.w());
    }
  });
});
