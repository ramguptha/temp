define([
  'ember',
  'am-desktop',
  'packages/am/am-data',

  'am-multi-select',

  'packages/am/availability-time-component'
], function (
  Em,
  AmDesktop,
  AmData,

  AmMultiSelect,

  AvailabilityTimeComponent
) {

  var PolicySelectionController = AmMultiSelect.MobilePolicyMultiSelectController.extend({

    // @Override
    getFilteredData: function(data) {
      var filteredData = data.filter(function (data) {
        return 255 !== data.get('data.filterType');
      });

      return this.excludeIds(filteredData, this.get('excludedIds'));
    }
  });

  var ContentSelectionController = AmMultiSelect.ContentMultiSelectController.extend({

    // @Override
    getFilteredData: function(data) {
      return this.excludeIds(data, this.get('excludedIds'));
    }
  });

  // Content Policy Assignment Base Controller
  // ==================================
  //
  // This controller contains the common properties/methods between
  // Controllers that Add Content and edit Policy Assignments.

  return Em.Mixin.create({

    AvailabilityTimeComponent: AvailabilityTimeComponent,

    tAutoInstall: 'amMobilePolicies.shared.assignmentRuleOptions.autoInstall'.tr(),
    tOnDemand: 'amMobilePolicies.shared.assignmentRuleOptions.onDemand'.tr(),
    tAutoInstallAutoRemove: 'amMobilePolicies.shared.assignmentRuleOptions.autoInstallAutoRemove'.tr(),
    tOnDemandAutoRemove: 'amMobilePolicies.shared.assignmentRuleOptions.onDemandAutoRemove'.tr(),
    tAddToPolicyButton: 'amMobilePolicies.shared.buttons.addToPolicy'.tr(),
    tAddToPolicies: 'amMobilePolicies.shared.buttons.addToPolicies'.tr(),

    // Used for selecting policies/contents to do assignment of contents/policies
    SelectionView: AmDesktop.AmSelectionListView,
    selectionController: null,
    ContentSelectionController: ContentSelectionController,
    PolicySelectionController: PolicySelectionController,

    // Properties and functions associated with assigning to policies
    // =====================
    //
    contentIds: null,
    policyIds: null,

    isActionBtnDisabled: null,

    isSelectionEmpty: function() {
      var selectionController = this.get('selectionController');
      if (!selectionController) {
        return false;
      }

      return selectionController.get('selections').length === 0;
    }.property('selectionController.selections.[]'),

    // Controls the visibility of Availability Time Component
    showAvailabilityTime: false,
    originalShowAvailabilityTime: null,

    availabilitySelector: 0,

    isAvailabilityTimeValid: false,
    availabilityTimeChanged: null,

    // Set if availability time is available on the server
    assignedTime: null,

    // Format that server would accept for availabilityTime
    formattedTime: null,

    selectedAssignmentType: Em.Object.create({ value: 1 }),
    originalAssignmentType: null,
    assignmentTypes: null,

    allowSaveOnNoSelection: true,

    onShowModal: function() {
      this.setProperties({
        paused: true,
        isAvailabilityTimeValid: false,

        assignmentTypes: Em.A([
          Em.Object.create({ name: this.get('tAutoInstall'), type: 3 }),
          Em.Object.create({ name: this.get('tOnDemand'), type: 4 }),
          Em.Object.create({ name: this.get('tAutoInstallAutoRemove'), type: 2 }),
          Em.Object.create({ name: this.get('tOnDemandAutoRemove'), type: 1 })
        ]),

        availabilitySelector: 0,
        showAvailabilityTime: false
      });
    },

    // Pre-populate the selections with the current values of the first policy to be edited
    populateExistingSettings: function(firstAssignment) {
      var assignmentType = firstAssignment.get('mediaFileAssignmentType');
      var availabilitySelector = firstAssignment.get('mediaFileAssignmentAvailability');

      this.setProperties({
        selectedAssignmentType: Ember.Object.create({ value: assignmentType }),
        availabilitySelector: availabilitySelector
      });

      this.setProperties({
        showAvailabilityTime: availabilitySelector !== 0,
        assignedTime: {
          startTime: firstAssignment.get('mediaFileAssignmentStartTime'),
          endTime: firstAssignment.get('mediaFileAssignmentEndTime')
        },
        availabilityTimeChanged: false
      });
    },

    // Handlers for policy selection and radio button changes
    // ==========
    //

    updateActionBtnDisabled: function() {
      if (this.get('paused')) { return; }

      var assignmentTypeChanged = this.get('selectedAssignmentType.value') !== this.get('originalAssignmentType'),
        showAvailabilityTimeChanged = this.get('showAvailabilityTime') !== this.get('originalShowAvailabilityTime');

      var showAvailabilityTime = this.get('showAvailabilityTime'),
        isAvailabilityTimeValid = this.get('isAvailabilityTimeValid'),
        availabilityTimeChanged = this.get('availabilityTimeChanged');

      var isActionBtnDisabled = true;

      // make sure we don't have a case where we display the availability time view
      // but the selector is 0 ( no time availability selection )
      if(showAvailabilityTime && this.get('availabilitySelector') === 0) {
        this.set('availabilitySelector', 1);
      }

      // If we are in editing mode we don't have a selection list
      if (!this.get('selectionController')) {
        if (showAvailabilityTime) {
          if (isAvailabilityTimeValid) {
            isActionBtnDisabled = availabilityTimeChanged ? false : !assignmentTypeChanged;
          }
        } else {
          isActionBtnDisabled = !assignmentTypeChanged && !showAvailabilityTimeChanged;
        }

      } else {
        var selectionsLength = this.get('selectionController.selections').length;

        if (selectionsLength === 0) {
          if(this.get('allowSaveOnNoSelection')) {
            isActionBtnDisabled = false;
          }

          this.set('showAvailabilityTime', false);
        } else {
          isActionBtnDisabled = showAvailabilityTime ? !isAvailabilityTimeValid : false;
        }
      }

      this.set('isActionBtnDisabled', isActionBtnDisabled);

    }.observes('isAvailabilityTimeValid',
      'availabilitySelector',
      'showAvailabilityTime',
      'availabilityTimeChanged',
      'selectedAssignmentType.value',
      'selectionController.selections.[]'),

    buildAvailabilityActionData: function () {
      var ActionData = AmData.get('actions.AmMobilePolicyToContentMapActionData');

      var time = this.get('showAvailabilityTime') ? this.get('formattedTime') : null;

      return ActionData.ContentAvailabilityDateRange.create({
        startDate: time ? time.startTime : null,
        endDate: time ? time.endTime : null,
        availabilitySelector: Number(this.get('availabilitySelector'))
      });
    },

    clear: function () {
      var controller = this.get('selectionController');

      if (controller) {
        controller.resetController();
      }

      this.setProperties({
        'selectedAssignmentType.value': 1,
        availabilityTimeChanged: null,
        isAvailabilityTimeValid: null
      });
    }
  });
});
