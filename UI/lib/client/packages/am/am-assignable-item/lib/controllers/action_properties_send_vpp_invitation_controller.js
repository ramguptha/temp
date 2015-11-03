define([
  'ember',

  './action_properties_register_related_base_controller',
  '../views/action_item_base_modal_view',

  'text!../templates/action_properties_send_vpp_invitation.handlebars'
], function (
  Em,

  ActionsPropertiesRegisterRelatedBaseController,
  ActionBaseModalView,

  template
) {

  // Action Properties Send VPP Invitation Controller
  // ==================================
  //

  return ActionsPropertiesRegisterRelatedBaseController.extend({
    propertiesView: ActionBaseModalView.extend({
      defaultTemplate: Em.Handlebars.compile(template)
    }),

    helpId: 1082,

    messageMaxLength: function() {
      var maxSize = this.get('MAX_TEXT_SIZE');

      // adjust the maxSize for webkit browsers since they represent the new line character as two characters
      if(navigator.userAgent.indexOf('AppleWebKit') != -1 && !Em.isNone(this.get('inviteSMSMessage'))) {
        maxSize += this.get('inviteSMSMessage').split(/\r\n|\r|\n/).length - 1;
      }

      return maxSize;
    }.property('inviteSMSMessage'),

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
      'inviteMessage',
      'inviteSubject',
      'inviteSMSMessage',
      'mdmChecked',
      'clipChecked',
      'smsChecked',
      'emailChecked',
      'absMessageChecked',
      'vppRecordId'),

    getIsEmpty: function() {
       return this.getBasicIsEmpty() || this.getIsInviteOptionsEmpty();
    },

    getIsDirty: function() {
      return this.getBasicIsDirty() || this.getIsInvitePropertiesDirty() ||
        this.get('vppRecordId') !== this.get('oldVppRecordId');
    },

    setDynamicProperties: function(data) {
      var inviteMessage = data.inviteMessage,
        inviteSubject = data.inviteSubject,
        inviteSMSMessage = data.inviteSMSMessage;

      var mdmChecked = data.mdmChecked,
        clipChecked = data.clipChecked,
        smsChecked = data.smsChecked,
        emailChecked = data.emailChecked,
        absMessageChecked = data.absMessageChecked;

      var vppRecordId = data.vppRecordId,
        vppUniqueId = data.vppUniqueId;

      this.setProperties({
        inviteMessage: inviteMessage,
        oldInviteMessage: inviteMessage,

        inviteSubject: inviteSubject,
        oldInviteSubject: inviteSubject,

        inviteSMSMessage: inviteSMSMessage,
        oldInviteSMSMessage: inviteSMSMessage,

        mdmChecked: mdmChecked,
        oldMdmChecked: mdmChecked,

        clipChecked: clipChecked,
        oldClipChecked: clipChecked,

        smsChecked: smsChecked,
        oldSmsChecked: smsChecked,

        emailChecked: emailChecked,
        oldEmailChecked: emailChecked,

        absMessageChecked: absMessageChecked,
        oldAbsMessageChecked: absMessageChecked,

        vppRecordId: vppRecordId,
        oldVppRecordId: vppRecordId,

        vppUniqueId: vppUniqueId
      });
    }
  });
});
