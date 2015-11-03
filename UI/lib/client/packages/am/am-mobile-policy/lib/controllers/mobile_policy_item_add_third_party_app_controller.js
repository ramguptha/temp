define([
  'ember',
  'help',
  '../namespace',

  'desktop',
  'am-desktop',
  'am-data',
  'am-multi-select',
  'packages/platform/enum-util',

  './mobile_policy_item_add_base',
  '../views/mobile_policy_add_third_party_app_view'
], function (Em,
             Help,
             AmMobilePolicy,
             Desktop,
             AmDesktop,
             AmData,
             AmMultiSelect,
             EnumUtil,
             AmMobilePolicyItemBase,
             MobilePolicyAddAppView) {
  'use strict';

  var ThirdPartyAppSelectionController = AmMultiSelect.ThirdPartyAppMultiSelectController.extend(Desktop.ChildController, Desktop.TransientController, {
      getFilteredData: function (filteredData) {
        return this.excludeIds(filteredData, this.get('excludedIds'));
      }
    }
  );

  return AmDesktop.ModalActionController.extend(AmMobilePolicyItemBase, {

    heading: 'amMobilePolicies.modals.addThirdPartyApplication.headingPolicy'.tr('policyName'),

    inProgressMsg: 'shared.modals.inProgressMessage'.tr(),
    successMsg: 'shared.modals.successMessage'.tr(),
    errorMsg: 'shared.modals.errorMessage'.tr(),

    tSelectionAndroidWarning: 'amMobilePolicies.modals.addThirdPartyApplication.selectionOnDemandAndroidWarning'.tr(),
    tSelectionOnDemandMultipleWarning: 'amMobilePolicies.modals.addThirdPartyApplication.selectionOnDemandMultipleWarning'.tr(),

    confirmationView: MobilePolicyAddAppView,

    ThirdPartyAppSelectionController: ThirdPartyAppSelectionController,

    assignmentWarning: null,

    urlForHelp: Help.uri(1029),

    restrictedAssignmentTypes: function () {
      return Em.A([
        Em.Object.create({name: this.get('tAutoInstall'), type: 1, disabled: true}),
        Em.Object.create({name: this.get('tOnDemand'), type: 2, selected: true}),
        Em.Object.create({name: this.get('tAutoInstallAutoRemove'), type: 3, disabled: true}),
        Em.Object.create({name: this.get('tOnDemandAutoRemove'), type: 4, disabled: true})
      ]);
    }.property(),

    initProperties: function () {
      var policies = this.get('model');

      this.initSharedProperties(policies);

      var thirdPartyApplicationFromPolicyStore = AmData.get('stores.thirdPartyApplicationFromPolicyStore'), self = this;

      self.setProperties({
        isAddingThirdPartyApp: true,
        selectionController: self.ThirdPartyAppSelectionController.create({
          parentController: self,
          policies: policies
        })
      });

      thirdPartyApplicationFromPolicyStore.acquire(
        null,
        {
          store: thirdPartyApplicationFromPolicyStore,
          limit: 1000,
          offset: 0,
          context: {mobilePolicyId: policies[0].id}
        },
        function (dataSource) {
          self.setProperties({
            'selectionController.excludedIds': dataSource.get('content').map(function (data) {
              return data.get('.id');
            })
          });
        }
      );
    },

    buildAction: function () {
      var time = this.get('showAvailabilityTime') ? this.get('formattedTime') : null;
      var policyAssignments = this.getPolicyAssignments(time);

      var selectionsList = this.get('selectionController.selections');

      return AmData.get('actions.AmMobilePolicyAddThirdPartyAppAction').create({
        thirdPartyAppIds: selectionsList.map(Number),
        policyAssignments: policyAssignments
      });
    },

    updateActionBtnDisabled: function () {
      this.set('isActionBtnDisabled', this.getActionBtnStatus());

    }.observes('isAvailabilityTimeValid',
      'availabilitySelector',
      'selectionController.selections.[]'),

    updateAssignmentType: function () {
      var self = this, restrictAssignmentTypes = false, assignmentWarning = null;
      var selectionsList = this.get('selectionController.selections');

      if (selectionsList.length > 1) {
        restrictAssignmentTypes = true;
        assignmentWarning = this.get('tSelectionOnDemandMultipleWarning');

      } else if (selectionsList.length === 1) {
        selectionsList.forEach(function (selectedItem) {
          var data = self.get('selectionController').getRowData([selectedItem])[0].get('data');

          // Android osPlatform === 11
          if (data.osPlatformEnum === 11) {
            restrictAssignmentTypes = true;
            assignmentWarning = self.get('tSelectionAndroidWarning');
          }

          if (Em.isEmpty(data.vppCodesRemaining) || data.vppCodesRemaining === 0) {
            restrictAssignmentTypes = true;
          }
        });
      }

      if (restrictAssignmentTypes) {
        var restrictedAssignmentTypes = this.get('restrictedAssignmentTypes');

        restrictedAssignmentTypes.forEach(function (item) {
          if (item.selected) {
            self.set('selectedAssignmentType', Ember.Object.create({value: item.type}));
          }
        });

        this.set('assignmentTypes', restrictedAssignmentTypes);

      } else {
        this.set('assignmentTypes', this.get('defaultAssignmentTypes'));
      }

      this.set('assignmentWarning', assignmentWarning);
    }
  });
});
