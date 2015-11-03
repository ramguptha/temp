define([
  'ember',
  'help',
  '../namespace',
  'desktop',
  'am-desktop',
  'am-data',

  './mobile_device_summary_list_controller',
  '../views/mobile_device_set_activation_lock_options_view'
], function(
  Em,
  Help,
  AmMobileDevice,
  Desktop,
  AmDesktop,
  AmData,

  MobileDeviceSummaryListController,
  MobileDeviceSetView
  ) {
  'use strict';

  return AmDesktop.ModalActionController.extend({

    actions: {
      cancel: function() {
        this.send('closeModal');
      },

      allowActivation: function() {
        this.set('isDisallow', true);
        this.sendActionRequest();
      },

      disallowActivation: function() {
        this.sendActionRequest();
      }
    },

    tUnsupportedDevicesMessage: 'amMobileDevice.modals.setActivationLock.unsupportedDevicesMessage'.tr('unsupportedDeviceCount', 'deviceCount'),

    unsupportedDeviceCount: null,
    deviceCount: null,

    isDisallow: false,
    isFactoryDefault: false,
    confirmationView: MobileDeviceSetView,

    heading: 'amMobileDevice.modals.setActivationLock.heading'.tr(),
    confirmPrompt: 'amMobileDevice.modals.setActivationLock.confirmPrompt'.tr(),
    actionDescription0: 'amMobileDevice.modals.setActivationLock.actionDescription0'.tr(),
    actionDescription1: 'amMobileDevice.modals.setActivationLock.actionDescription1'.tr(),
    actionDescription2: 'amMobileDevice.modals.setActivationLock.actionDescription2'.tr(),
    actionButtonLabel: 'amMobileDevice.modals.setActivationLock.actionButtonLabel'.tr(),
    inProgressMsg: 'amMobileDevice.modals.setActivationLock.inProgressMsg'.tr(),
    successMsg: 'amMobileDevice.modals.setActivationLock.successMsg'.tr(),
    errorMsg: 'amMobileDevice.modals.setActivationLock.errorMsg'.tr(),
    errorDetailsMsg: 'amMobileDevice.modals.setActivationLock.errorDetailsMsg'.tr(),

    urlForHelp: null,
    actionWarning: null,
    actionDescription: null,

    initProperties: function(devices) {
      var modalActionWindowClass = this.get('modalActionWindowClass'), deviceIds = Em.A([]);
      var unsupportedDeviceList = Em.A([]), unsupportedDevicesMessage = null, unsupportedDeviceListController = null;

      for (var i = 0; i < devices.length; i++) {
        var v = devices[i].get('data.osVersion'), ver = 0;

        if (v != undefined || v != null) {
          ver = (v >> 24) & 0xff;
        }

        if ( devices[i].get('data.isSupervised') && ver >= 7 ) {
          deviceIds.pushObject(devices[i].get('id'));
        } else {
          unsupportedDeviceList.pushObject(devices[i]);
        }
      }

      if (unsupportedDeviceList.length > 0) {
        this.setProperties({
          unsupportedDeviceCount: unsupportedDeviceList.length,
          deviceCount: devices.length
        });

        unsupportedDevicesMessage = this.get('tUnsupportedDevicesMessage');

        unsupportedDeviceListController = MobileDeviceSummaryListController.create({
          dataStore: AmData.get('stores.mobileDeviceStore').createStaticDataStore(unsupportedDeviceList)
        });

        modalActionWindowClass += " summary-list";
      }

      this.setProperties({
        urlForHelp: Help.uri(1016),
        isDisallow: false,
        isFactoryDefault: false,
        modalActionWindowClass: modalActionWindowClass + " wide",
        headingIconClass: "icon-square-attention1",

        actionWarning: null,
        actionDescription: null,

        deviceIds: deviceIds,
        unsupportedDevices: unsupportedDeviceList,
        unsupportedDeviceListController: unsupportedDeviceListController,
        unsupportedDevicesMessage: unsupportedDevicesMessage
      });
    },

    buildAction: function(param) {
      this.set('urlForHelp', null);
      return AmData.get('actions.AmMobileDeviceSetActivationLockOptionsAction').create({
        mobileDeviceIds: this.get('deviceIds'),
        activationLock: this.isDisallow ? 1 : 0
      });
    }
  });
});
