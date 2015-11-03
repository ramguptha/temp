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
  '../views/mobile_policy_add_in_house_app_view'
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

  var InHouseAppSelectionController = AmMultiSelect.InHouseAppMultiSelectController.extend(Desktop.ChildController, Desktop.TransientController, {
    getFilteredData: function (filteredData) {
      return this.excludeIds(filteredData, this.get('excludedIds'));
    }
  });

  return AmDesktop.ModalActionController.extend(AmMobilePolicyItemBase, {

    heading: 'amMobilePolicies.modals.addInHouseApplication.headingPolicy'.tr('policyName'),

    inProgressMsg: 'shared.modals.inProgressMessage'.tr(),
    successMsg: 'shared.modals.successMessage'.tr(),
    errorMsg: 'shared.modals.errorMessage'.tr(),

    confirmationView: MobilePolicyAddAppView,

    InHouseAppSelectionController: InHouseAppSelectionController,

    urlForHelp: Help.uri(1028),

    initProperties: function () {
      var policies = this.get('model');

      this.initSharedProperties(policies);

      var inHouseApplicationFromPolicyStore = AmData.get('stores.inHouseApplicationFromPolicyStore'), self = this;

      this.setProperties({
        selectionController: self.InHouseAppSelectionController.create({
          parentController: self,
          policies: policies
        })
      });

      inHouseApplicationFromPolicyStore.acquire(null,
        {store: inHouseApplicationFromPolicyStore, limit: 1000, offset: 0, context: {mobilePolicyId: policies[0].id}},
        function (dataSource) {
          self.setProperties({
            'selectionController.excludedIds': dataSource.get('content').map(function (data) {
              return data.get('.id');
            })
          });
        });
    },

    buildAction: function () {
      var time = this.get('showAvailabilityTime') ? this.get('formattedTime') : null;
      var policyAssignments = this.getPolicyAssignments(time), selectionsList = this.get('selectionController.selections');

      return AmData.get('actions.AmMobilePolicyAddInHouseAppAction').create({
        inHouseAppIds: selectionsList.map(Number),
        policyAssignments: policyAssignments
      });
    }
  });
});
