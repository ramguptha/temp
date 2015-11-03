define([
  'ember',

  './action_item_base_controller'
], function (
  Em,

  ActionItemBaseController
) {

  // Action Properties Register Related Base Controller
  // ==================================
  //
  // Register User action and Send VPP Invitation action have many properties
  // and methods in common that are all put here.

  return ActionItemBaseController.extend({
    hasDynamicProperties: true,

    helpId: null,

    isIosSupported: true,

    tRegisterOnly: 'amAssignableItem.modals.actionProperties.registerOnly'.tr(),
    tRegisterAndInvite: 'amAssignableItem.modals.actionProperties.registerAndInvite'.tr(),

    tDefaultEmailSubject: 'amAssignableItem.modals.actionProperties.registerEmailSubject'.tr(),
    tDefaultSmsText: 'amAssignableItem.modals.actionProperties.registerSmsText'.tr(),

    tEmailDisplayTitle: 'amAssignableItem.modals.actionProperties.registerEmailDisplayTitle'.tr(),
    tEmailDisplayText: 'amAssignableItem.modals.actionProperties.registerEmailDisplayText'.tr(),
    tEmailUrlTitle: 'amAssignableItem.modals.actionProperties.registerEmailUrlTitle'.tr(),
    tEmailUrlText: 'amAssignableItem.modals.actionProperties.registerEmailUrlText'.tr(),

    tDefaultEmailText: function() {
      return this.get('tEmailDisplayTitle') + '\n\n' + this.get('tEmailDisplayText') +
        '\n\n' + this.get('tEmailUrlTitle')  + '\n\n' + this.get('tEmailUrlText');
    }.property(),

    // AmAgent does not seem to be used. Always false for the moment.
    amAgentChecked: false,

    mdmChecked: true,
    oldMdmChecked: null,

    clipChecked: false,
    oldClipChecked: null,

    smsChecked: false,
    oldSmsChecked: null,
    isSmsDisabled: Em.computed.not('smsChecked'),

    emailChecked: true,
    oldEmailChecked: null,
    isEmailDisabled: Em.computed.not('emailChecked'),

    absMessageChecked: false,
    oldAbsMessageChecked: null,
    isAbsDisabled: Em.computed.not('absMessageChecked'),

    isEmailMessageDisabled: function() {
      return !this.get('emailChecked') && !this.get('absMessageChecked');
    }.property('emailChecked', 'absMessageChecked'),

    inviteMessage: null,
    oldInviteMessage: null,

    inviteSubject: null,
    oldInviteSubject: null,

    inviteSMSMessage: null,
    oldInviteSMSMessage: null,

    messageLengthRemaining: function() {
      return this.getLengthRemaining(this.get('inviteSMSMessage'));
    }.property('inviteSMSMessage'),

    vppRecordId: null,
    oldVppRecordId: null,
    vppUniqueId: null,

    initialize: function(model) {
      this._super(model);

      this.loadVppAccounts();
    },

    getIsInviteOptionsEmpty: function() {
      var isInviteFieldInvalid = false;
      var isRequiredFieldInvalid = this.get('isListOfOptionsEmpty') || Em.isEmpty(this.get('vppRecordId'));

      if (!isRequiredFieldInvalid) {
        // We can not leave all the invite options un-checked.
        var isInviteOptionUnchecked = !(this.get('mdmChecked') || this.get('clipChecked') ||
        this.get('smsChecked') || this.get('emailChecked') || this.get('absMessageChecked'));

        var isSmsFieldInvalid = this.get('smsChecked') && this.getIsEmptyField(this.get('inviteSMSMessage'));
        var isEmailFieldInvalid = this.get('emailChecked') && this.getIsEmptyField(this.get('inviteSubject'));
        var isAbsFieldInvalid = this.get('absMessageChecked') && this.getIsEmptyField(this.get('inviteMessage'));

        isInviteFieldInvalid = isSmsFieldInvalid || isEmailFieldInvalid || isAbsFieldInvalid;
      }

      return isInviteOptionUnchecked || isInviteFieldInvalid || isRequiredFieldInvalid;
    },

    getIsInvitePropertiesDirty: function() {
      return this.get('inviteMessage') !== this.get('oldInviteMessage') ||
        this.get('inviteSubject') !== this.get('oldInviteSubject') ||
        this.get('inviteSMSMessage') !== this.get('oldInviteSMSMessage') ||
        this.get('mdmChecked') !== this.get('oldMdmChecked') ||
        this.get('clipChecked') !== this.get('oldClipChecked') ||
        this.get('smsChecked') !== this.get('oldSmsChecked') ||
        this.get('emailChecked') !== this.get('oldEmailChecked') ||
        this.get('absMessageChecked') !== this.get('oldAbsMessageChecked');
    },

    resetDynamicProperties: function() {
      var properties = {
        mdmChecked: true,
        clipChecked: false,
        smsChecked: false,
        emailChecked: true,
        absMessageChecked: false,

        vppRecordId: null,
        vppUniqueId: null,

        inviteMessage: this.get('tDefaultEmailText').toString(),
        inviteSubject: this.get('tDefaultEmailSubject').toString(),
        inviteSMSMessage: this.get('tDefaultSmsText').toString()
      };

      this.setDynamicProperties(properties);
      this.loadVppAccounts();
    },

    getFormattedPropertyList: function() {
      return this.getProperties('inviteMessage inviteSubject inviteSMSMessage mdmChecked clipChecked smsChecked emailChecked absMessageChecked vppRecordId vppUniqueId'.w());
    }
  });
});
