define([
  'ember',
  '../namespace',
  'desktop',
  'am-desktop',
  'am-data',

  './user_self_help_item_summary_list_controller',
  '../views/user_self_help_item_track_device_view'
], function (
  Em,
  AmMobileDevice,
  Desktop,
  AmDesktop,
  AmData,

  UserSelfHelpSummaryListController,
  UserSelfHelpResetTrackDeviceView
) {
  'use strict';

  return AmDesktop.ModalActionController.extend({
    mobileDeviceListController: Em.inject.controller('AmUserSelfHelpDevice'),

    heading: 'amUserSelfServicePortal.modals.trackDevice.heading'.tr(),
    headingIconClass: "icon-passcode",
    addModalClass: "device-command-window",
    actionButtonLabel: 'amUserSelfServicePortal.modals.trackDevice.buttons.actionButtonLabel'.tr(),

    inProgressMsg: 'amUserSelfServicePortal.modals.trackDevice.inProgressMsg'.tr(),
    successMsg: 'amUserSelfServicePortal.modals.trackDevice.successMsg'.tr(),
    errorMsg: 'amUserSelfServicePortal.modals.trackDevice.errorMsg'.tr(),

    tUnsupportedDevicesMessage: 'amUserSelfServicePortal.modals.trackDevice.unsupportedDevicesMessage'.tr('unsupportedDeviceCount', 'deviceCount'),
    tPasscodeErrorMessage: 'amUserSelfServicePortal.shared.passcodeErrorMessage'.tr(),

    devices: null,
    androidIds: null,
    iOsIds: null,
    promptForPasscode: null,
    passcodeErrorMessage: null,

    unsupportedDevices: null,
    unsupportedDevicesMessage: null,

    activationPassphrase: null,

    unsupportedDeviceCount: null,
    deviceCount: null,

    confirmationView: UserSelfHelpResetTrackDeviceView,

    unsupportedDeviceListController: null,

    // minutes
    selectedIntervalPeriodId: 1,

    // Nearest 100 meters
    selectedLocationAccuracyIs: 2,

    trackDeviceEnabled: null,
    trackDeviceDisabled: null,

    initProperties: function () {
      var devices = this.get('model');

      var androidIds = Em.A([]);
      var iOsIds = Em.A([]);

      var deviceListCtrl = this.get('mobileDeviceListController');
      var unsupportedDeviceList = Em.A([]);

      for (var i = 0; i < devices.length; i++) {
        if (deviceListCtrl.supportsIOSManagedCommands(devices[i])) {
          if (devices[i].get('data.osPlatformEnum') == 11) {
            androidIds.pushObject(devices[i].get('id'));
          } else if (devices[i].get('data.osPlatformEnum') == 10) {
            iOsIds.pushObject(devices[i].get('id'));
          }
        } else {
          unsupportedDeviceList.pushObject(devices[i]);
        }
      }

      var modalActionWindowClass = this.get('modalActionWindowClass');
      var unsupportedDevicesMessage = null;
      var unsupportedDeviceListController = null;

      if (unsupportedDeviceList.length > 0) {
        this.setProperties({
          unsupportedDeviceCount: unsupportedDeviceList.length,
          deviceCount: devices.length
        });

        unsupportedDevicesMessage = this.get('tUnsupportedDevicesMessage');

        unsupportedDeviceListController = UserSelfHelpSummaryListController.create({
          dataStore: AmData.get('stores.mobileDeviceStore').createStaticDataStore(unsupportedDeviceList)
        });

        modalActionWindowClass += " summary-list";
      }

      this.setProperties({
        modalActionWindowClass: modalActionWindowClass,

        androidIds: androidIds,
        iOsIds: iOsIds,
        devices: devices,

        trackDeviceEnabled: false,
        trackDeviceDisabled: true,

        unsupportedDevices: unsupportedDeviceList.length > 0,
        unsupportedDevicesMessage: unsupportedDevicesMessage,
        unsupportedDeviceListController: unsupportedDeviceListController,
        isActionBtnDisabled: false
      });
    },

    intervalPeriodList: function() {
      return AmData.get('stores.userSelfHelpTrackingIntervalPeriodStore').materializedObjects;
    }.property(),

    locationAccuracyList: function() {
      return AmData.get('stores.userSelfHelpLocationAccuracyStore').materializedObjects;
    }.property(),

    onTrackDeviceEnabledChanged: function () {
      // enabledBinding does not work from a template
      // custom property (trackDeviceDisabled) does not work as well
      // So, use binding to variable trackDeviceDisabled from a template (Ember bug or feature)
      this.set('trackDeviceDisabled', !this.get('trackDeviceEnabled'))
    }.observes('trackDeviceEnabled'),

    buildAction: function () {
      return AmData.get('actions.AmUserSelfHelpDeviceTrackAction').create({
        iosDeviceIds: this.get('iOsIds'),
        androidDeviceIds: this.get('androidIds'),
        newPasswordForAndroidDevices: this.get('passcode')
      });
    }

  });
});
