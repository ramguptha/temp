define([
  'ember',

  './action_item_base_controller',
  '../views/action_item_base_modal_view',

  'text!../templates/action_properties_set_activation_lock.handlebars'
], function (
  Em,

  ActionItemBaseController,
  ActionBaseModalView,

  template
) {

  // Action Properties Set Activation Lock Controller
  // ==================================
  //
  return ActionItemBaseController.extend({

    hasDynamicProperties: true,
    propertiesView: ActionBaseModalView.extend({
      defaultTemplate: Em.Handlebars.compile(template)
    }),

    helpId: 1062,

    tAllow: 'amAssignableItem.modals.actionProperties.allow'.tr(),
    tDisallow: 'amAssignableItem.modals.actionProperties.disallow'.tr(),

    isIosSupported: true,

    lock: null,
    oldLock: null,

    lockOptions: function() {
      return Em.A([
        {
          value: '0',
          label: this.get('tDisallow'),
          class: 'is-radio-checked-disallow'
        }, {
          value: '1',
          label: this.get('tAllow'),
          class: 'is-radio-checked-allow'
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
      'lock'),

    getIsEmpty: function() {
      return this.getBasicIsEmpty();
    },

    getIsDirty: function() {
      return this.getBasicIsDirty() ||
        this.get('lock') !== this.get('oldLock');
    },

    setDynamicProperties: function(data) {
      var lock = data.lock;

      this.setProperties({
        lock: lock,
        oldLock: lock
      });
    },
    resetDynamicProperties: function() {
      var properties = { lock: '0' };

      this.setDynamicProperties(properties);
    },

    getFormattedPropertyList: function() {
      return {
        lock: parseInt(this.get('lock'))
      };
    }
  });
});
