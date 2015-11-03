define([
  'ember',
  'formatter',

  './action_item_base_controller',
  '../views/action_item_base_modal_view',

  'text!../templates/action_properties_attention_mode.handlebars'
], function (
  Em,
  Formatter,

  ActionItemBaseController,
  ActionBaseModalView,

  template
) {

  // Action Properties Attention Mode Controller
  // ==================================
  //
  return ActionItemBaseController.extend({

    hasDynamicProperties: true,
    propertiesView: ActionBaseModalView.extend({
      defaultTemplate: Em.Handlebars.compile(template)
    }),

    tEnable: 'amAssignableItem.modals.actionProperties.enable'.tr(),
    tDisable: 'amAssignableItem.modals.actionProperties.disable'.tr(),

    helpId: 1067,

    isIosAndroidSupported: true,

    attentionMode: 'true',
    oldAttentionMode: null,

    lockScreenMessage: null,
    oldLockScreenMessage: null,
    isMessageDisabled: Em.computed.equal('attentionMode', 'false'),

    attentionModeOptions: function() {
      return Em.A([
        {
          value: 'true',
          label: this.get('tEnable'),
          class: 'is-radio-checked-enable'
        }, {
          value: 'false',
          label: this.get('tDisable'),
          class: 'is-radio-checked-disable'
        }
      ]);
    }.property(),

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
      'lockScreenMessage',
      'attentionMode'),

    getIsEmpty: function() {
      return this.getBasicIsEmpty() ||
            ( this.get('attentionMode') === 'true' && this.getIsEmptyField(this.get('lockScreenMessage')) );
    },

    getIsDirty: function() {
      return this.getBasicIsDirty() ||
        this.get('lockScreenMessage') !== this.get('oldLockScreenMessage') ||
        this.get('attentionMode') !== this.get('oldAttentionMode');
    },

    setDynamicProperties: function(data) {
      var attentionMode = data.attentionMode,
        lockScreenMessage = data.lockScreenMessage;

      this.setProperties({
        attentionMode: attentionMode,
        oldAttentionMode: attentionMode,

        lockScreenMessage: lockScreenMessage,
        oldLockScreenMessage: lockScreenMessage
      });
    },

    resetDynamicProperties: function() {
      var properties = {
        attentionMode: 'true',
        lockScreenMessage: null
      };

      this.setDynamicProperties(properties);
    },

    getFormattedPropertyList: function() {
      return {
        attentionMode: Formatter.stringToBoolean(this.get('attentionMode')),
        lockScreenMessage: this.get('lockScreenMessage')
      };
    }
  });
});
