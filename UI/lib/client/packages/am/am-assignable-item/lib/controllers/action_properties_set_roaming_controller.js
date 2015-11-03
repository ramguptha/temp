define([
  'ember',

  './action_item_base_controller',
  '../views/action_item_base_modal_view',

  'text!../templates/action_properties_set_roaming.handlebars'
], function (
  Em,

  ActionItemBaseController,
  ActionBaseModalView,

  template
) {

  // Action Properties Set Roaming Controller
  // ==================================
  //
  return ActionItemBaseController.extend({

    hasDynamicProperties: true,
    propertiesView: ActionBaseModalView.extend({
      defaultTemplate: Em.Handlebars.compile(template)
    }),

    helpId: 1061,

    tOn: 'amAssignableItem.modals.actionProperties.on'.tr(),
    tOff: 'amAssignableItem.modals.actionProperties.off'.tr(),
    tLeaveAsIs: 'amAssignableItem.modals.actionProperties.leaveAsIs'.tr(),

    isIosSupported: true,

    voiceRoaming: null,
    oldVoiceRoaming: null,

    dataRoaming: null,
    oldDataRoaming: null,

    isRoamingOptionInvalid: function() {
      return this.get('voiceRoaming') === '-1' && this.get('dataRoaming') === '-1';
    }.property('voiceRoaming', 'dataRoaming'),

    roamingOptions: function() {
      return Em.A([
        {
          value: '-1',
          label: this.get('tLeaveAsIs'),
          class: 'is-radio-checked-as-is'
        }, {
          value: '0',
          label: this.get('tOff'),
          class: 'is-radio-checked-off'
        }, {
          value: '1',
          label: this.get('tOn'),
          class: 'is-radio-checked-on'
        }
      ]);
    }.property(),

    dynamicPropertiesChanged: function() {
      if (!this.get('isInitializationDone')) { return; }

      var isInvalid = this.getBasicIsEmpty() || this.get('isRoamingOptionInvalid');

      this.setProperties({
        isActionBtnDisabled: isInvalid || !this.getIsDirty(),
        isSaveAndAssignActionBtnDisabled: isInvalid
      });
    }.observes('name',
      'description',
      'isNameDuplicate',
      'voiceRoaming',
      'dataRoaming',
      'isRoamingOptionInvalid'),

    getIsDirty: function() {
      return this.getBasicIsDirty() ||
        this.get('voiceRoaming') !== this.get('oldVoiceRoaming') ||
        this.get('dataRoaming') !== this.get('oldDataRoaming');
    },

    setDynamicProperties: function(data) {
      var dataRoaming = data.dataRoaming,
        voiceRoaming = data.voiceRoaming;

      this.setProperties({
        dataRoaming: dataRoaming,
        oldDataRoaming: dataRoaming,

        voiceRoaming: voiceRoaming,
        oldVoiceRoaming: voiceRoaming
      });
    },

    resetDynamicProperties: function() {
      var properties = {
        'voiceRoaming': '0',
        'dataRoaming': '0'
      };

      this.setDynamicProperties(properties);
    },

    getFormattedPropertyList: function() {
      return {
        dataRoaming: parseInt(this.get('dataRoaming')),
        voiceRoaming: parseInt(this.get('voiceRoaming'))
      }
    }
  });
});
