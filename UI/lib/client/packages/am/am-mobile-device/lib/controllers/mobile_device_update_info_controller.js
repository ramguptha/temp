define([
  'ember',
  '../namespace',
  'desktop',
  'am-desktop',
  'am-data',

  './mobile_device_summary_list_controller',
  '../views/mobile_device_update_info_view'
], function(
  Em,
  AmMobileDevice,
  Desktop,
  AmDesktop,
  AmData,

  MobileDeviceSummaryListController,
  MobileDeviceUpdateInfoView
) {
  'use strict';

  return AmDesktop.ModalActionController.extend({
    heading: 'amMobileDevice.modals.updateDeviceInfo.heading'.tr(),

    actionButtonLabel: null,
    tActionButtonLabel: 'amMobileDevice.modals.updateDeviceInfo.buttons.actionButtonLabel'.tr(),

    inProgressMsg: 'amMobileDevice.modals.updateDeviceInfo.inProgressMsg'.tr(),
    successMsg: 'amMobileDevice.modals.updateDeviceInfo.successMsg'.tr(),
    errorMsg: 'amMobileDevice.modals.updateDeviceInfo.errorMsg'.tr(),
    errorDetailsMsg: 'amMobileDevice.modals.updateDeviceInfo.errorDetailsMsg'.tr(),

    tUnsupportedDevicesMessage: 'amMobileDevice.modals.updateDeviceInfo.unsupportedDevicesMessage'.tr('unsupportedDeviceCount', 'deviceCount'),

    devices: null,
    headingIconClass: 'icon-square-attention1',

    confirmationView: null,

    mobileDeviceListController: Em.inject.controller('AmMobileDeviceGroupsShowGroup'),

    unsupportedDeviceCount: null,
    deviceCount: null,

    initProperties: function()  {
      var devices = this.get('model');

      var deviceListCtrl = this.get('mobileDeviceListController');

      var unsupportedDeviceList = Em.A([]);
      var deviceIds = '';
      for (var i=0; i < devices.length; i++) {
        deviceIds += devices[i].get('id') + ', ';
        if (!deviceListCtrl.supportsIOSManagedCommands(devices[i]) &&
          !deviceListCtrl.supportsWinPhoneManagedCommands(devices[i])) {
          unsupportedDeviceList.pushObject(devices[i]);
        }
      }

      var actionButtonLabel = null;

      var modalActionWindowClass = this.get('modalActionWindowClass');
      var unsupportedDevicesMessage = null;
      var unsupportedDeviceListController = null;

      if (unsupportedDeviceList.length > 0) {
        this.setProperties({
          unsupportedDeviceCount: unsupportedDeviceList.length,
          deviceCount: devices.length,
          confirmationView: MobileDeviceUpdateInfoView
        });

        unsupportedDevicesMessage = this.get('tUnsupportedDevicesMessage');
        actionButtonLabel = this.get('tActionButtonLabel');

        unsupportedDeviceListController = MobileDeviceSummaryListController.create({
          dataStore: AmData.get('stores.mobileDeviceStore').createStaticDataStore(unsupportedDeviceList)
        });

        modalActionWindowClass += ' summary-list';
      } else {
        // TODO The confirmation view logic happened before Init() method. This code need to be refactored to call custom method before showModal() and Init().
        // Custom method: InitConfirmationView(callbackDone) to get unsupportedDeviceList and put confirmationView reference or null instead before Init() method
        // Place to call: updateDeviceInfo: function (selectionsList)
        // Right now there is no confirmation view even with unsupportedDeviceList > 0
        confirmationView: null
      }

      this.setProperties({
        devices: devices,
        modalActionWindowClass: modalActionWindowClass,
        actionButtonLabel: actionButtonLabel,
        unsupportedDevices: unsupportedDeviceList.length > 0,
        unsupportedDevicesMessage: unsupportedDevicesMessage,
        unsupportedDeviceListController: unsupportedDeviceListController
      });
    },

    buildAction: function() {
      var deviceListCtrl = this.get('mobileDeviceListController');
      
      return AmData.get('actions.AmMobileDeviceUpdateInfoAction').create({
        mobileDeviceIds: this.get('devices')
          .filter(function(device) { return (deviceListCtrl.supportsIOSManagedCommands(device) ||
            deviceListCtrl.supportsWinPhoneManagedCommands(device)); })
          .mapBy('id')
      });
    }
  });
});
