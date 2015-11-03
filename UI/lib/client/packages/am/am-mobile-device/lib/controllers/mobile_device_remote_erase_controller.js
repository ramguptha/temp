define([
  'ember',
  'help',
  '../namespace',
  'desktop',
  'am-desktop',
  'am-data',

  './mobile_device_summary_list_controller',
  '../views/mobile_device_commands_container_view',
  'text!../templates/mobile_device_remote_erase.handlebars'
], function(
  Em,
  Help,
  AmMobileDevice,
  Desktop,
  AmDesktop,
  AmData,

  MobileDeviceSummaryListController,
  MobileDeviceCommandsContainerView,
  MobileDeviceRemoteEraseTemplate
) {
  'use strict';

  return AmDesktop.ModalActionController.extend({
    heading: 'amMobileDevice.modals.remoteDataDelete.heading'.tr(),
    actionButtonLabel: 'amMobileDevice.modals.remoteDataDelete.buttons.actionButtonLabel'.tr(),

    tEraseInternal: 'amMobileDevice.modals.remoteDataDelete.deleteInternalStorageOnly'.tr(),
    tEraseInternalSDCard: 'amMobileDevice.modals.remoteDataDelete.deleteInternalStorageSDCard'.tr(),

    inProgressMsg: 'amMobileDevice.modals.remoteDataDelete.inProgressMsg'.tr(),
    successMsg: 'amMobileDevice.modals.remoteDataDelete.successMsg'.tr(),
    errorMsg: 'amMobileDevice.modals.remoteDataDelete.errorMsg'.tr(),
    errorDetailsMsg: 'amMobileDevice.modals.remoteDataDelete.errorDetailsMsg'.tr(),

    tConfirmPromptOneDevice: 'amMobileDevice.modals.remoteDataDelete.confirmPromptOneDevice'.tr(),
    tConfirmPromptManyDevices: 'amMobileDevice.modals.remoteDataDelete.confirmPromptManyDevices'.tr(),
    tActionWarning: 'amMobileDevice.modals.remoteDataDelete.actionWarning'.tr(),

    actionWarning: null,
    actionDescription: null,

    tUnsupportedDevicesMessage: 'amMobileDevice.modals.remoteDataDelete.unsupportedDevicesMessage'.tr('unsupportedDeviceCount', 'deviceCount'),

    headingIconClass: 'icon-square-attention1',

    devices: null,

    androidIds: null,
    iOsIds: null,

    promptForEraseSDCard: null,

    unsupportedDevices: false,
    unsupportedDevicesMessage: null,

    MobileDevicePropertiesView: Em.View.extend({
      defaultTemplate: Em.Handlebars.compile(MobileDeviceRemoteEraseTemplate)
    }),

    confirmationView: MobileDeviceCommandsContainerView,

    unsupportedDeviceListController: null,

    mobileDeviceListController: Em.inject.controller('AmMobileDeviceGroupsShowGroup'),

    unsupportedDeviceCount: null,
    deviceCount: null,

    urlForHelp: null,

    eraseOption: '0',
    eraseOptions: function() {
      return Em.A([
        {
          value: '0',
          label: this.get('tEraseInternal'),
          class: 'is-radio-checked-erase-internal'
        }, {
          value: '1',
          label: this.get('tEraseInternalSDCard'),
          class: 'is-radio-checked-erase-internal-sd'
        }
      ]);
    }.property(),

    initProperties: function()  {
      var devices = this.get('model');

      var androidIds = Em.A([]);
      var iOsIds = Em.A([]);

      var deviceListCtrl = this.get('mobileDeviceListController');
      var unsupportedDeviceList = Em.A([]);

      for (var i = 0; i < devices.length; i++) {
        if (deviceListCtrl.supportsIOSManagedCommands(devices[i]) ||
          deviceListCtrl.supportsWinPhoneManagedCommands(devices[i])) {
          if (devices[i].get('data.osPlatformEnum') == 11) {
            androidIds.pushObject(devices[i].get('id'));
          }
          // Windows Phone devices are currently also added to the 'iOSIds' list as well as iOS devices
          else if (devices[i].get('data.osPlatformEnum') == 10 || devices[i].get('data.osPlatformEnum') == 12) {
            iOsIds.pushObject(devices[i].get('id'));
          }
        } else {
          unsupportedDeviceList.pushObject(devices[i]);
        }
      }
      var promptForEraseSDCard = androidIds.length > 0;

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
        modalActionWindowClass: modalActionWindowClass,
        urlForHelp: Help.uri(1006),

        devices: devices,
        androidIds: androidIds,
        iOsIds: iOsIds,
        eraseOption: '0',

        promptForEraseSDCard: promptForEraseSDCard,
        unsupportedDevices: unsupportedDeviceList.length > 0,
        unsupportedDevicesMessage: unsupportedDevicesMessage,
        unsupportedDeviceListController: unsupportedDeviceListController,

        actionWarning: this.get('tActionWarning'),
        actionDescription: null
      });
    },

    confirmPrompt: function() {
      return this.get('deviceCountDetails') ? this.get('tConfirmPromptOneDevice') : this.get('tConfirmPromptManyDevices');
    }.property('devices.[]'),

    deviceCountDetails: function() {
      var count = this.get('devices.length');
      return (count > 1) ? count : null;
    }.property('devices.[]'),

    buildAction: function() {
      var self = this;
      this.set('urlForHelp', null);

      return AmData.get('actions.AmMobileDeviceRemoteEraseAction').create({
        iosDeviceIds: self.get('iOsIds'),
        androidDeviceIds: self.get('androidIds'),
        includeSDCard: self.get('eraseOption') === '1'
      });
    }
  });
});
