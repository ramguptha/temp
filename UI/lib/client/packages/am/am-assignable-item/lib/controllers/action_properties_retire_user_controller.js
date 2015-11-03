define([
  'ember',

  './action_item_base_controller',
  '../views/action_item_base_modal_view',

  'text!../templates/action_properties_retire_user.handlebars'
], function (
  Em,

  ActionItemBaseController,
  ActionBaseModalView,

  template
) {

  // Action Properties Retire User Controller
  // ==================================
  //
  return ActionItemBaseController.extend({

    hasDynamicProperties: true,
    propertiesView: ActionBaseModalView.extend({
      defaultTemplate: Em.Handlebars.compile(template)
    }),

    helpId: 1070,

    isIosSupported: true,

    vppRecordId: null,
    oldVppRecordId: null,
    vppUniqueId: null,

    initialize: function(model) {
      this._super(model);

      this.loadVppAccounts();
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
      'vppRecordId'),

    getIsEmpty: function() {
      return this.getBasicIsEmpty() || this.get('isListOfOptionsEmpty') || Em.isEmpty(this.get('vppRecordId'));
    },

    getIsDirty: function() {
      return this.getBasicIsDirty() ||
        this.get('vppRecordId') !== this.get('oldVppRecordId');
    },

    setDynamicProperties: function(data) {
      var vppRecordId = parseInt(data.vppRecordId),
        vppUniqueId = data.vppUniqueId;

      this.setProperties({
        vppRecordId: vppRecordId,
        oldVppRecordId: vppRecordId,

        vppUniqueId: vppUniqueId
      });
    },

    resetDynamicProperties: function() {
      var properties = {
        vppRecordId: null,
        vppUniqueId: null
      };

      this.setDynamicProperties(properties);
      this.loadVppAccounts();
    },

  getFormattedPropertyList: function() {
      return this.getProperties('vppRecordId vppUniqueId'.w());
    }
  });
});
