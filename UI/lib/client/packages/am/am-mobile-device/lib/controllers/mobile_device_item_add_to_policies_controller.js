define([
  'ember',
  'help',
   'guid',

  'am-multi-select',
  'am-data',
  'packages/platform/query',
  'desktop',
  'am-desktop',

  '../views/mobile_device_commands_container_view',
  'text!../templates/mobile_device_add_policies.handlebars'
], function(
  Em,
  Help,
  Guid,

  AmMultiSelect,
  AmData,
  Query,
  Desktop,
  AmDesktop,

  MobileDeviceCommandsContainerView,
  AmMobileDeviceAddPoliciesTemplate
) {
  'use strict';

  var PolicySelectionController = AmMultiSelect.MobilePolicyMultiSelectController.extend(Desktop.ChildController, Desktop.TransientController, {
    loadExistingPolicies: function(deviceId) {
      var self = this;
      this.set('paused', true);

      var query = Query.Search.create({ context: { mobileDeviceId: deviceId } });

      AmData.get('stores.mobilePolicyFromMobileDeviceStore').acquire(Guid.generate(), query, function(dataSource) {
        var data = dataSource.get('content');

        // Exclude the already installed applications.
        var excludedIds = data.map(function(content) {
          return content.get('content.data.id');
        });

        self.setProperties({
          excludedIds: excludedIds,
          paused: false
        });
      });
    },

    // @Override
    getFilteredData: function(data) {
      var filteredData = data.filter(function (data) {
        return 255 !== data.get('data.filterType') && data.get('data.isSmartPolicy') === 0;
      });

      var excludedIds = this.get('excludedIds');
      if (Em.isEmpty(excludedIds)) { return filteredData; }

      return this.excludeIds(filteredData, excludedIds);
    }
  });

  return AmDesktop.ModalActionController.extend({
    heading: 'amMobileDevice.modals.addMobileDeviceToPolicies.heading'.tr(),
    headingIconClass: "icon-plus",
    addModalClass: "add-device-to-policy-window",

    actionDescription: '',
    actionButtonLabel: 'amMobileDevice.modals.addMobileDeviceToPolicies.buttons.actionButtonLabel'.tr(),

    isActionBtnDisabled: true,

    inProgressMsg: 'shared.modals.inProgressMessage'.tr(),
    successMsg: 'shared.modals.successMessage'.tr(),
    errorMsg: 'shared.modals.errorMessage'.tr(),

    PolicySelectionController: PolicySelectionController,
    policySelectionController: null,

    MobileDevicePropertiesView: Em.View.extend({
      defaultTemplate: Em.Handlebars.compile(AmMobileDeviceAddPoliciesTemplate)
    }),

    confirmationView: MobileDeviceCommandsContainerView,
    PolicySelectionView: AmDesktop.AmSelectionListView,

    deviceId: Em.computed.oneWay('model'),

    urlForHelp: null,

    initProperties: function(deviceId) {
      var policySelectionController = this.PolicySelectionController.create({
        parentController: this
      });

      policySelectionController.loadExistingPolicies(deviceId);

      this.setProperties({
        isActionBtnDisabled: true,
        urlForHelp: Help.uri(1027),
        policySelectionController: policySelectionController
      });
    },

    onPolicySelectionChanged: function(router, event) {
      // The Add Devices Action Button is only enabled if one or more devices is selected
      this.set('isActionBtnDisabled', this.get('policySelectionController.selections.length') === 0);
    }.observes('policySelectionController.selections.[]'),

    buildAction: function() {
      this.set('urlForHelp', null);
      return AmData.get('actions.AmMobilePolicyToMobileDeviceMapCreateAction').create({
        mobileDeviceIds: Em.A([this.get('deviceId')]),
        mobilePolicyIds: this.get('policySelectionController.selections')
      });
    }
  });
});
