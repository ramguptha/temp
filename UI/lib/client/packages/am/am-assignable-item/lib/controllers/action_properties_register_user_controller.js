define([
  'ember',

  './action_properties_register_related_base_controller',
  '../views/action_item_base_modal_view',

  'text!../templates/action_properties_register_user.handlebars'
], function (
  Em,

  ActionsPropertiesRegisterRelatedBaseController,
  ActionBaseModalView,

  template
) {

  // Action Properties Register User Controller
  // ==================================
  //

  return ActionsPropertiesRegisterRelatedBaseController.extend({
    propertiesView: ActionBaseModalView.extend({
      defaultTemplate: Em.Handlebars.compile(template)
    }),

    helpId: 1069,

    registerOption: '1',
    oldRegisterOption: '1',
    isRegisterOnlyChecked: Em.computed.equal('registerOption', '0'),

    messageMaxLength: function() {
      var maxSize = this.get('MAX_TEXT_SIZE');

      // adjust the maxSize for webkit browsers since they represent the new line character as two characters
      if(navigator.userAgent.indexOf('AppleWebKit') != -1 && !Em.isNone(this.get('inviteSMSMessage'))) {
        maxSize += this.get('inviteSMSMessage').split(/\r\n|\r|\n/).length - 1;
      }

      return maxSize;
    }.property('inviteSMSMessage'),

    registerOptions: function() {
      return Em.A([
        {
          value: '0',
          label: this.get('tRegisterOnly'),
          class: 'is-radio-checked-on',
          hasDescription: true
        },
        {
          value: '1',
          label: this.get('tRegisterAndInvite'),
          class: 'is-radio-checked-off'
        }
      ]);
    }.property(),

    // If the 'register only' options has been selected,
    // un-check all the invite options, otherwise
    // use either the available old values or the default values
    registerOptionChanged: function() {
      var oldMdmChecked = this.get('oldMdmChecked'),
        oldClipChecked = this.get('oldClipChecked'),
        oldSmsChecked = this.get('oldSmsChecked'),
        oldEmailChecked = this.get('oldEmailChecked'),
        oldAbsMessageChecked = this.get('oldAbsMessageChecked');

      if (this.get('isRegisterOnlyChecked')) {
        this.setProperties({
          mdmChecked: false,
          clipChecked: false,
          smsChecked: false,
          emailChecked: false,
          absMessageChecked: false
        })
      } else {
        this.setProperties({
          mdmChecked: !Em.isEmpty(oldMdmChecked) ? oldMdmChecked : true,
          clipChecked: !Em.isEmpty(oldClipChecked) ? oldClipChecked : false,
          smsChecked: !Em.isEmpty(oldSmsChecked) ? oldSmsChecked : false,
          emailChecked: !Em.isEmpty(oldEmailChecked) ? oldEmailChecked : true,
          absMessageChecked: !Em.isEmpty(oldAbsMessageChecked) ? oldAbsMessageChecked : false
        })
      }
    }.observes('isRegisterOnlyChecked').on('init'),

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
      'vppRecordId',
      'isRegisterOnlyChecked'),

    getIsEmpty: function() {
      var isRegisterOnlyChecked = this.get('isRegisterOnlyChecked');

      var isInviteOptionsEmpty = false;

      // When Register Only option is not selected,
      // 1- We can not have all the invite checkboxes un-checked.
      // 2- Some of the invite options should not have empty related required fields
      if (!isRegisterOnlyChecked) {
        isInviteOptionsEmpty = this.getIsInviteOptionsEmpty();
       }

      return this.getBasicIsEmpty() || isInviteOptionsEmpty;
    },

    getIsDirty: function() {
      var isInvitePropertiesDirty = false;

      if (!this.get('isRegisterOnlyChecked')) {
        isInvitePropertiesDirty = this.getIsInvitePropertiesDirty();
      }

      return this.getBasicIsDirty() || isInvitePropertiesDirty ||
        this.get('registerOption') !== this.get('oldRegisterOption') ||
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

      // If all the invite options are unchecked:
      // 1- We keep the old value of each option as null
      // 2- We set the register option as 'register only'.
      var inviteOptionsUnchecked = !(mdmChecked || clipChecked || smsChecked || emailChecked || absMessageChecked);
      var registerOption = inviteOptionsUnchecked ? '0' : '1';

      this.setProperties({
        inviteMessage: inviteMessage,
        oldInviteMessage: inviteMessage,

        inviteSubject: inviteSubject,
        oldInviteSubject: inviteSubject,

        inviteSMSMessage: inviteSMSMessage,
        oldInviteSMSMessage: inviteSMSMessage,

        mdmChecked: mdmChecked,
        oldMdmChecked: inviteOptionsUnchecked ? null : mdmChecked,

        clipChecked: clipChecked,
        oldClipChecked: inviteOptionsUnchecked ? null : clipChecked,

        smsChecked: smsChecked,
        oldSmsChecked: inviteOptionsUnchecked ? null : smsChecked,

        emailChecked: emailChecked,
        oldEmailChecked: inviteOptionsUnchecked ? null : emailChecked,

        absMessageChecked: absMessageChecked,
        oldAbsMessageChecked: inviteOptionsUnchecked ? null : absMessageChecked,

        vppRecordId: vppRecordId,
        oldVppRecordId: vppRecordId,

        vppUniqueId: vppUniqueId,

        registerOption: registerOption,
        oldRegisterOption: registerOption
      });
    }
  });
});
