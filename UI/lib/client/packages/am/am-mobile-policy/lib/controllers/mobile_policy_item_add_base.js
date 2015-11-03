define([
  'ember',
  'radioButtonGroup',
  'am-desktop',

  'help',
  'jquery',
  '../namespace',
  'formatter',
  'guid',

  'desktop',
  'packages/am/am-data',
  'packages/platform/enum-util',

  'packages/am/am-assignable-item-foundation',
  'packages/am/availability-time-component'
], function (
  Em,
  RadioButtonGroup,
  AmDesktop,

  Help,
  $,
  AmMobilePolicy,
  Formatter,
  Guid,

  Desktop,
  AmData,
  EnumUtil,

  AmAssignableItemFoundation,
  AvailabilityTimeComponent
) {
  'use strict';

  return Em.Mixin.create({
    actions: {
      onConfirmAction: function() {
        this.set('urlForHelp', null);

        this.sendActionRequest();
      },

      cancel: function() {
        this.clearAssignments();
        this.send('closeModal');
      }
    },

    AvailabilityTimeComponent: AvailabilityTimeComponent,

    RadioButton: RadioButtonGroup.RadioButton,

    tAutoInstall: 'amMobilePolicies.shared.assignmentRuleOptions.autoInstall'.tr(),
    tOnDemand: 'amMobilePolicies.shared.assignmentRuleOptions.onDemand'.tr(),
    tAutoInstallAutoRemove: 'amMobilePolicies.shared.assignmentRuleOptions.autoInstallAutoRemove'.tr(),
    tOnDemandAutoRemove: 'amMobilePolicies.shared.assignmentRuleOptions.onDemandAutoRemove'.tr(),
    tForbidden: 'amMobilePolicies.shared.assignmentRuleOptions.forbidden'.tr(),

    actionButtonLabel: 'amMobilePolicies.shared.buttons.addToPolicy'.tr(),

    headingIconClass: 'icon-install-config',
    addModalClass: 'add-app-to-policy-window',

    modalActionErrorMsgClass: 'modal-action-error-fullwidth',
    modalActionErrorDetailsClass: 'modal-action-details-fullwidth',

    errorDetails: null,
    actionFailed: false,

    showOkBtn: null,
    isActionBtnDisabled: true,

    policies: null,
    policyName: null,

    // The selection list related properties
    // ===============================================
    //
    selectionController: null,
    SelectionView: AmDesktop.AmSelectionListView,

    isSelectionEmpty: function() {
      var selectionController = this.get('selectionController');
      if (!selectionController) {
        return false;
      }

      return selectionController.get('selections').length === 0;
    }.property('selectionController.selections.[]'),

    // The "availability time component" related properties
    // ===============================================
    //
    // Controls the visibility of Availability Time Component
    showAvailabilityTime: false,
    availabilitySelector: 0,

    // Set if availability time is available on the server
    assignedTime: null,

    // Format that server would accept for availabilityTime
    formattedTime: null,

    isAvailabilityTimeValid: true,

    // The "Assignment Type" related properties
    // ===============================================
    //
    assignmentTypes: null,
    selectedAssignmentType: Ember.Object.create({ value: 1 }),

    defaultAssignmentTypes: function() {
      return Em.A([
        Em.Object.create({ name: this.get('tAutoInstall'), type: 1 }),
        Em.Object.create({ name: this.get('tOnDemand'), type: 2 }),
        Em.Object.create({ name: this.get('tAutoInstallAutoRemove'), type: 3 }),
        Em.Object.create({ name: this.get('tOnDemandAutoRemove'), type: 4 }),
        Em.Object.create({ name: this.get('tForbidden'), type: 0 })
      ]);
    }.property(),

    isAddingThirdPartyApp: false,

    initSharedProperties: function(policies) {
      this.setProperties({
        modalActionWindowClass: this.get('modalActionWindowClass') + ' summary-list',

        assignmentTypes: this.get('defaultAssignmentTypes'),
        availabilitySelector: 0,
        showAvailabilityTime: false,

        policies: policies ? policies : null,
        policyName: policies ? policies[0].get('model.name').toString() : null
      });
    },

    getPolicyAssignments: function(time) {
      var self = this;

      var policyAssignments = [];
      this.get('policies').mapBy('id').forEach(function (id) {
        var row = {};

        row.policyId = Number(id);
        row.assignmentType = Number(self.get('selectedAssignmentType.value'));
        row.availabilitySelector = Number(self.get('availabilitySelector'));

        if (time) {
          row.startTime = time.startTime;
          row.endTime = time.endTime;
        }

        policyAssignments.push(row);
      });

      return policyAssignments;
    },

    updateActionBtnDisabled: function () {
      // Add Third Party Application has its own version of observers
      if (this.get('isAddingThirdPartyApp')) { return; }

      this.set('isActionBtnDisabled', this.getActionBtnStatus());

    }.observes('isAvailabilityTimeValid',
      'availabilitySelector',
      'selectedAssignmentType.value',
      'selectionController.selections.[]'),

    updateSelector: function() {
      this.set('availabilitySelector', this.get('showAvailabilityTime') ? 1 : 0)
    }.observes('showAvailabilityTime'),

    getActionBtnStatus: function() {
      if (this.get('paused')) { return; }

      var isActionBtnDisabled = true;

      var assignmentTypeChanged = this.get('selectedAssignmentType.value') !== this.get('originalAssignmentType');
      var showAvailabilityTime = this.get('showAvailabilityTime');
      var isAvailabilityTimeValid = this.get('isAvailabilityTimeValid');

      var selectionController = this.get('selectionController');

      if (!selectionController) {
        isActionBtnDisabled = showAvailabilityTime ? !isAvailabilityTimeValid : !assignmentTypeChanged;

      } else {
        if (selectionController.get('selections').length === 0) {
          isActionBtnDisabled = true;
          this.set('showAvailabilityTime', false);

        } else {
          isActionBtnDisabled = showAvailabilityTime ? !isAvailabilityTimeValid : false;
        }

        if (typeof(this.updateAssignmentType) === 'function') {
          this.updateAssignmentType();
        }
      }

      return isActionBtnDisabled;
    },

    clearAssignments: function() {
      this.set('selectedAssignmentType.value', 1);
    }
  });
});
