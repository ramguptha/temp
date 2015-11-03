define([
  'ember',
  'help',
  '../namespace',
  'desktop',
  'am-desktop',
  'am-data',

  './mobile_device_summary_list_controller',
  '../views/mobile_device_commands_container_view',
  'text!../templates/mobile_device_set_roaming_options.handlebars'
], function (
  Em,
  Help,
  AmMobileDevice,
  Desktop,
  AmDesktop,
  AmData,

  MobileDeviceSummaryListController,
  MobileDeviceCommandsContainerView,
  MobileDeviceSetRoamingOptionsTemplate
) {
  'use strict';

  return AmDesktop.ModalActionController.extend({
    mobileDeviceListController: Em.inject.controller('AmMobileDeviceGroupsShowGroup'),

    heading: 'amMobileDevice.modals.setRoamingOptions.heading'.tr(),
    headingIconClass: 'icon-roaming',

    actionButtonLabel: 'amMobileDevice.modals.setRoamingOptions.buttons.actionButtonLabel'.tr(),

    inProgressMsg: 'amMobileDevice.modals.setRoamingOptions.inProgressMsg'.tr(),
    successMsg: 'amMobileDevice.modals.setRoamingOptions.successMsg'.tr(),
    errorMsg: 'amMobileDevice.modals.setRoamingOptions.errorMsg'.tr(),
    errorDetailsMsg: 'amMobileDevice.modals.setRoamingOptions.errorDetailsMsg'.tr(),

    tUnsupportedDevicesMessage: 'amMobileDevice.modals.setRoamingOptions.unsupportedDevicesMessage'.tr('unsupportedDeviceCount', 'deviceCount'),

    devices: null,

    unsupportedDeviceCount: null,
    deviceCount: null,

    deviceCountDetails: function() {
      var count = this.get('devices.length');
      return (count > 1) ? count : null;
    }.property('devices.[]'),

    MobileDevicePropertiesView: Em.View.extend({
      defaultTemplate: Em.Handlebars.compile(MobileDeviceSetRoamingOptionsTemplate)
    }),

    confirmationView: MobileDeviceCommandsContainerView,

    urlForHelp: null,

    initProperties: function () {
      var devices = this.get('model'), deviceListCtrl = this.get('mobileDeviceListController');
      var dataRoamingEnabled = null, voiceRoamingEnabled = null, unsupportedDeviceList = Em.A([]),
        supportedDevices = Em.A([]), deviceIds = '', iOS6andIOS7Selected = false, versionType, versionTypeFirstDevice = null;

      for (var i = 0; i < devices.length; i++) {
        versionType = ((devices[i].get('data.osVersion') >> 24) & 0xff) >= 7;

        if( versionTypeFirstDevice == null ) {
          versionTypeFirstDevice = versionType;
        } else {
          if( versionTypeFirstDevice != versionType) {
            iOS6andIOS7Selected = true;
          }
        }

        deviceIds += devices[i].get('id') + ', ';
        if (!deviceListCtrl.supportsSetRoamingOptions(devices[i])) {
          unsupportedDeviceList.pushObject(devices[i]);
        } else {
          supportedDevices.pushObject(devices[i]);
        }

      }

      if (supportedDevices.length === 1 ||
        AmMobileDevice.isIdenticalDeviceFields(supportedDevices, ['data.dataRoamingEnabled', 'data.voiceRoamingEnabled'])) {
        var dev = supportedDevices[0];
        dataRoamingEnabled = dev.get('data.dataRoamingEnabled');
        voiceRoamingEnabled = dev.get('data.voiceRoamingEnabled');
      } else {
        dataRoamingEnabled = null;
        voiceRoamingEnabled = null;

        for(i =0; i < supportedDevices.length; i++){
          if( supportedDevices[i].get('data.dataRoamingEnabled') != null) {
            dataRoamingEnabled = 0;
          }
          if( supportedDevices[i].get('data.voiceRoamingEnabled') != null) {
            voiceRoamingEnabled = 0;
          }
        }
      }

      var modalActionWindowClass = 'modal-action-window';
      var unsupportedDevicesMessage = null;
      var unsupportedDeviceListController = null;

      if (unsupportedDeviceList.length > 0) {

        this.setProperties({
          unsupportedDeviceCount: unsupportedDeviceList.length,
          deviceCount: devices.length
        });

        unsupportedDevicesMessage = this.get('tUnsupportedDevicesMessage');

        unsupportedDeviceListController = MobileDeviceSummaryListController.create({
          dataStore: AmData.get('stores.mobileDeviceStore').createStaticDataStore(unsupportedDeviceList)
        });

        modalActionWindowClass += ' summary-list';
      }

      this.setProperties({
        devices: devices,
        urlForHelp: Help.uri(1009),

        modalActionWindowClass: modalActionWindowClass,

        voiceRoamingEnabled: voiceRoamingEnabled === 1,
        isVoiceRoamingSelectionDisabled: voiceRoamingEnabled == null,
        dataRoamingEnabled: dataRoamingEnabled === 1,
        isDataRoamingSelectionDisabled: dataRoamingEnabled == null,

        unsupportedDevices: unsupportedDeviceList.length > 0,
        unsupportedDevicesMessage: unsupportedDevicesMessage,
        unsupportedDeviceListController: unsupportedDeviceListController,

        iOS6andIOS7Selected: voiceRoamingEnabled == null ? false : iOS6andIOS7Selected
      });
    },

    buildAction: function () {
      this.set('urlForHelp', null);
      var deviceListCtrl = this.get('mobileDeviceListController');

      return AmData.get('actions.AmMobileDeviceSetRoamingOptionsAction').create({
        mobileDeviceIds: this.get('devices')
          .filter(function (device) { return deviceListCtrl.supportsSetRoamingOptions(device); })
          .mapBy('id'),
        voiceRoamingEnabled: this.get('voiceRoamingEnabled'),
        dataRoamingEnabled: this.get('dataRoamingEnabled')
      });
    }
  });
});
