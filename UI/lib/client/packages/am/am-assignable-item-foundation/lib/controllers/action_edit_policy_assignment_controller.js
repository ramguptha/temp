define([
  'ember',
  'am-data',

  './action_policy_assignment_base_controller'
], function (
  Em,
  AmData,

  ActionPolicyAssignmentBaseController
) {

  // Edit Policy Assignments Controller
  // ==================================
  //
  // This controller controls the assignment details.

  return ActionPolicyAssignmentBaseController.extend({

    tHeading: 'amAssignableItem.modals.action.policyAssignment.edit.heading'.tr('policyName'),
    headingIconClass: 'icon-edit-icon',

    isActionBtnDisabled: true,

    policyName: null,

    actionName: null,
    actionType: null,

    onShowModal: function(model) {
      this.resetSettings();

      var policyData = model.policyData;
      var actionData = model.actionData;

      var selectedData = model.isPolicySelected ? policyData : actionData;
      var intervalSettings = {
        initialDelay: selectedData.initialDelay,
        repeatInterval: selectedData.repeatInterval,
        repeatCount: selectedData.repeatCount
      };

      this.setProperties({
        policyName: policyData.name,

        actionName: actionData.name,
        actionType: actionData.type,

        actionIds: Em.A([ actionData.id ]),
        policyIds: Em.A([ policyData.id ])
      });

      this.populateExistingIntervalSettings(intervalSettings);

      this._super(model);
    },

    getIsIntervalSettingsDirty: function() {
      return this.get('delayChecked') !== this.get('oldDelayChecked') ||
        this.get('initialDelay') !== this.get('oldInitialDelay') ||
        this.get('delaySelectorId') !== this.get('oldDelaySelectorId') ||
        this.get('repeatChecked') !== this.get('oldRepeatChecked') ||
        this.get('repeatInterval') !== this.get('oldRepeatInterval') ||
        this.get('repeatCount') !== this.get('oldRepeatCount') ||
        this.get('repeatSelectorId') !== this.get('oldRepeatSelectorId');
    },

    buildAction: function() {
      var content = {
        actionIds: this.get('actionIds'),
        policyIds: this.get('policyIds')
      };

      return this._super(content);
    }
  });
});
