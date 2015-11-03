define([
  'ember',

  './action_item_base_controller',
  '../views/action_item_base_modal_view',

  'text!../templates/action_properties_freeze_device.handlebars'
], function (
  Em,

  ActionItemBaseController,
  ActionBaseModalView,

  template
) {

  // Action Properties Freeze Device Controller
  // ==================================
  //
  return ActionItemBaseController.extend({

    hasDynamicProperties: true,
    propertiesView: ActionBaseModalView.extend({
      defaultTemplate: Em.Handlebars.compile(template)
    }),

    helpId: 1068,

    isAndroidSupported: true,

    passphrase: null,
    oldPassphrase: null,
    passphraseVerification: null,
    hashedPassphrase: null,

    passphraseErrorMessage: null,
    tPassphraseNotMatched: 'amAssignableItem.modals.actionProperties.passphraseNotMatched'.tr(),
    tPassphraseErrorMessage: 'amAssignableItem.modals.actionProperties.passphraseErrorMessage'.tr(),

    initialize: function(model) {
      this._super(model);

      this.resetDynamicProperties();
    },

     dynamicPropertiesChanged: function() {
      if (!this.get('isInitializationDone')) { return; }

      this.setProperties({
        isActionBtnDisabled: this.getIsEmpty() || !this.getIsDirty(),
        isSaveAndAssignActionBtnDisabled: this.getIsEmpty()
      });
    }.observes('name',
       'description',
       'isNameDuplicate',
       'isInitializationDone',
       'passphrase',
       'passphraseVerification'),

    getIsEmpty: function() {
      var isPassphraseValid = this.validatePassphrase();

      return this.getBasicIsEmpty() || !isPassphraseValid ||
        Em.isEmpty(this.get('passphrase')) || Em.isEmpty(this.get('passphraseVerification'));
    },

    getIsDirty: function() {
      return this.getBasicIsDirty() ||
        this.get('passphrase') !== this.get('passphraseVerification') ||
        this.get('passphrase') !== this.get('oldPassphrase');
    },

    validatePassphrase: function() {
      var isValid = true;

      var errorMessage;
      var passphrase = this.get('passphrase');
      var verification = this.get('passphraseVerification');

      // Do not display any error for an empty passphrase
      var passCodeLengthIsWrong = passphrase ? passphrase.length < 4 || passphrase.length > 16 : false;

      if(passCodeLengthIsWrong) {
        errorMessage = this.get('tPassphraseErrorMessage');
        isValid = false;
      } else if (passphrase !== verification) {
        errorMessage = this.get('tPassphraseNotMatched');
        isValid = false;
      }

      this.set('passphraseErrorMessage', errorMessage);

      return isValid;
    },

    setDynamicProperties: function(data) {
      var hashedPassphrase = this.get('isDuplicateMode') ? null : data.passphrase;

      this.setProperties({
        hashedPassphrase: hashedPassphrase,
        passphraseVerification: hashedPassphrase ? '    ' : null,

        passphrase: hashedPassphrase ? '    ' : null,
        oldPassphrase: hashedPassphrase ? '    ' : null
      });
    },

    resetDynamicProperties: function() {
      var properties = {
        passphrase: null,
        passphraseVerification: null
      };

      this.setDynamicProperties(properties);
      this.set('passphraseErrorMessage', null);
    },

    getFormattedPropertyList: function() {
      var passphrase = this.get('passphrase');

      // User did not change the password. Cannot be 4 spaces password right now, can be fixed later by flag: password was changed
      // Need to pass an empty string for no password changes
      return {
        passphrase: passphrase === '    ' ? '' : passphrase
      };
    }
  });
});
