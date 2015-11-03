define([
  'ember',

  './action_item_base_controller',
  '../views/action_item_base_modal_view',

  'text!../templates/action_properties_send_email.handlebars'
], function (
  Em,

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

    helpId: 1059,

    isAllSupported: true,

    emailToErrorMessage: null,
    emailCcErrorMessage: null,

    isEmailValid: function() {
      return Em.isEmpty(this.get('emailToErrorMessage')) && Em.isEmpty(this.get('emailCcErrorMessage'));
    }.property('emailToErrorMessage', 'emailCcErrorMessage'),

    emailSubject: '',
    oldEmailSubject: '',

    emailTo: '',
    oldEmailTo: '',

    emailMessage: '',
    oldEmailMessage: '',

    emailCc: '',
    oldEmailCc: '',

    initialize: function(model) {
      this.setProperties({
        emailToErrorMessage: null,
        emailCcErrorMessage: null
      });

      this._super(model);
    },

    dynamicPropertiesChanged: function() {
      if (!this.get('isInitializationDone')) { return; }

      var isInvalid = this.getIsEmpty() || !this.get('isEmailValid');

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
      'emailMessage',
      'emailSubject',
      'emailTo',
      'emailCc',
      'isEmailValid'),

    getIsEmpty: function() {
      return this.getBasicIsEmpty() || Em.isEmpty(this.get('emailSubject').trim()) || this.getIsEmptyField(this.get('emailTo').trim());
    },

    getIsDirty: function() {
      return this.getBasicIsDirty() ||
        this.get('emailMessage') !== this.get('oldEmailMessage') ||
        this.get('emailSubject') !== this.get('oldEmailSubject') ||
        this.get('emailTo') !== this.get('oldEmailTo') ||
        this.get('emailCc') !== this.get('oldEmailCc');
    },

    setDynamicProperties: function(data) {
      var emailTo = data.emailTo,
        emailCc = data.emailCc,
        emailSubject = data.emailSubject,
        emailMessage = data.emailMessage;

      this.setProperties({
        emailTo: emailTo,
        oldEmailTo: emailTo,

        emailCc: emailCc,
        oldEmailCc: emailCc,

        emailSubject: emailSubject,
        oldEmailSubject: emailSubject,

        emailMessage: emailMessage,
        oldEmailMessage: emailMessage
      });
    },

    resetDynamicProperties: function() {
      var properties = {
        emailTo: '',
        emailCc: '',
        emailSubject: '',
        emailMessage: ''
      };

      this.setDynamicProperties(properties);
    },

    getFormattedPropertyList: function() {
      return  {
        emailTo: this.get('emailTo').replace(';', ','),
        emailCc: this.get('emailCc').replace(';', ','),
        emailSubject: this.get('emailSubject'),
        emailMessage: this.get('emailMessage')
      };
    }
  });
});
