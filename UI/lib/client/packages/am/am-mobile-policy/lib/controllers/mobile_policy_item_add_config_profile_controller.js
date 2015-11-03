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
  '../views/mobile_policy_add_config_profile_view'
], function (Em,
             Help,
             AmMobilePolicy,
             Desktop,
             AmDesktop,
             AmData,
             AmMultiSelect,
             EnumUtil,
             AmMobilePolicyItemBase,
             MobilePolicyAddConfigProfileView) {
  'use strict';

  var ConfigProfileSelectionController = AmMultiSelect.MobileConfigProfileMultiSelectController.extend(Desktop.ChildController, Desktop.TransientController, {
    getFilteredData: function (filteredData) {
      return this.excludeIds(filteredData, this.get('excludedIds'));
    },

    hasRowClick: true,
    selectOnRowClick: true,

    createColumns: function (names) {
      var columns = this._super(names);

      columns.forEach(function (column) {
        // We need to show specific icons for content type
        if (column.get('name') === 'osPlatform') {
          column.set('valueComponent', 'am-formatted-os-platform');
        }
      });

      return columns;
    }
  });

  return AmDesktop.ModalActionController.extend(AmMobilePolicyItemBase, {

    heading: 'amMobilePolicies.modals.addConfigurationProfile.heading'.tr('policyName'),

    inProgressMsg: 'shared.modals.inProgressMessage'.tr(),
    successMsg: 'shared.modals.successMessage'.tr(),
    errorMsg: 'shared.modals.errorMessage'.tr(),

    confirmationView: MobilePolicyAddConfigProfileView,

    ConfigProfileSelectionController: ConfigProfileSelectionController,

    urlForHelp: Help.uri(1030),

    initProperties: function () {
      var policies = this.get('model');

      this.initSharedProperties(policies);

      var configurationProfileFromPolicyStore = AmData.get('stores.configurationProfileFromPolicyStore'), self = this;

      this.setProperties({
        selectionController: self.ConfigProfileSelectionController.create({
          parentController: self,
          policies: policies
        })
      });

      configurationProfileFromPolicyStore.acquire(null,
        {store: configurationProfileFromPolicyStore, limit: 1000, offset: 0, context: {mobilePolicyId: policies[0].id}},
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

      return AmData.get('actions.AmMobilePolicyAddConfigProfileAction').create({
        configurationProfileIds: selectionsList.map(Number),
        policyAssignments: policyAssignments
      });
    }
  });
});
