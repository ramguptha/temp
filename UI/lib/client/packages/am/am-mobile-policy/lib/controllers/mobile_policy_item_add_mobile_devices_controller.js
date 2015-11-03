define([
  'ember',
  'help',
  '../views/mobile_policy_add_devices_view',
  'am-multi-select',
  'am-data',
  'packages/platform/query',
  'desktop',
  'am-desktop'
], function (Em,
             Help,
             MobilePolicyAddDevicesView,
             AmMultiSelect,
             AmData,
             Query,
             Desktop,
             AmDesktop) {
  'use strict';

  var DeviceSelectionController = AmMultiSelect.MobileDeviceMultiSelectController.extend(Desktop.ChildController, Desktop.TransientController, {
    hasRowClick: true,
    selectOnRowClick: true,

    getFilteredData: function (data) {
      var filteredData = data.filter(function (data) {
        return 1 === data.get('data.isManaged');
      });

      return this.excludeIds(filteredData, this.get('excludedIds'));
    }
  });

  return AmDesktop.ModalActionController.extend({
    heading: 'amMobilePolicies.modals.addMobileDevices.heading'.tr('policyName'),
    headingIconClass: "icon-plus",

    actionDescription: '',
    actionButtonLabel: 'amMobilePolicies.shared.buttons.addToPolicy'.tr(),

    inProgressMsg: 'shared.modals.inProgressMessage'.tr(),
    successMsg: 'shared.modals.successMessage'.tr(),
    errorMsg: 'shared.modals.errorMessage'.tr(),

    addModalClass: 'add-mobile-device-to-policy-window',

    confirmationView: MobilePolicyAddDevicesView,
    DeviceSelectionView: AmDesktop.AmSelectionListView,

    DeviceSelectionController: DeviceSelectionController,
    deviceSelectionController: null,

    isActionBtnDisabled: true,
    endPoint: '/api/policy_mobiledevice/',
    policyId: null,
    policyName: null,

    urlForHelp: null,

    initProperties: function () {
      var policyId = this.get('model.id'), policyName = this.get('model.name');

      var self = this, query = Query.Search.create({
        context: {
          mobilePolicyId: policyId
        }
      });

      this.setProperties({
        policyId: policyId,
        urlForHelp: Help.uri(1027),
        policyName: policyName,
        deviceSelectionController: this.DeviceSelectionController.create({
          parentController: this,
          policyId: policyId
        })
      });

      AmData.get('stores.mobileDeviceFromMobilePolicyStore').acquire(null, query, function (dataSource) {
        self.set('deviceSelectionController.excludedIds', dataSource.get('content').map(function (data) {
          return data.get('.id');
        }));
      }, self);
    },

    onDeviceSelectionChanged: function () {
      // The Add Devices Action Button is only enabled if one or more devices is selected
      this.set('isActionBtnDisabled', this.get('deviceSelectionController.selections').length === 0);
    }.observes('deviceSelectionController.selections.[]'),

    buildAction: function () {
      this.set('urlForHelp', null);
      return AmData.get('actions.AmMobilePolicyToMobileDeviceMapCreateAction').create({
        mobileDeviceIds: this.get('deviceSelectionController.selections'),
        mobilePolicyIds: Em.A([this.get('policyId')])
      });
    }
  });
});