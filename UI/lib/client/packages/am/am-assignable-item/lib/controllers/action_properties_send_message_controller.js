define([
  'ember',

  './action_item_base_controller',
  '../views/action_item_base_modal_view',

  'text!../templates/action_properties_send_message.handlebars'
], function (
  Em,

  ActionItemBaseController,
  ActionBaseModalView,

  template
) {

  // Action Properties Send Message Controller
  // ==================================
  //
  return ActionItemBaseController.extend({
    hasDynamicProperties: true,
    propertiesView: ActionBaseModalView.extend({
      defaultTemplate: Em.Handlebars.compile(template)
    }),

    helpId: 1058,
    isIosAndroidSupported: true,

    message: '',
    oldMessage: '',

    messageMaxLength: function() {
      var maxSize = this.get('MAX_TEXT_SIZE');

      // adjust the maxSize for webkit browsers since they represent the new line character as two characters
      if(navigator.userAgent.indexOf('AppleWebKit') != -1 && !Em.isNone(this.get('message'))) {
        maxSize += this.get('message').split(/\r\n|\r|\n/).length - 1;
      }

      return maxSize;
    }.property('message'),

    messageLengthRemaining: function() {
      return this.getLengthRemaining(this.get('message'));
    }.property('message'),

    dynamicPropertiesChanged: function() {
      if (!this.get('isInitializationDone')) { return; }

      this.setProperties({
        isActionBtnDisabled: this.getIsEmpty() || !this.getIsDirty(),
        isSaveAndAssignActionBtnDisabled: this.getIsEmpty()
      });

    }.observes('name',
      'description',
      'isNameDuplicate',
      'iosChecked',
      'androidChecked',
      'message'),

    getIsEmpty: function() {
      return this.getBasicIsEmpty() || this.getIsEmptyField(this.get('message'));
    },

    getIsDirty: function() {
      return this.getBasicIsDirty() ||
        this.get('message') !== this.get('oldMessage');
    },

    setDynamicProperties: function(data) {
      var message = data.message;

      this.setProperties({
        message: message,
        oldMessage: message
      });
    },

    resetDynamicProperties: function() {
      var properties = { message: '' };

      this.setDynamicProperties(properties);
    },

    getFormattedPropertyList: function() {
      return this.getProperties('message'.w());
    }
  });
});
