define([
  'ember',
  'help',
  'jquery',
  'query',
  '../namespace',
  'guid',

  'desktop',
  'am-desktop',
  'am-data',
  'am-multi-select',
  'packages/platform/enum-util',

  '../views/mobile_device_commands_container_view',
  'text!../templates/mobile_device_install_config_profile.handlebars'
], function (
  Em,
  Help,
  $,
  Query,
  AmMobileDevice,
  Guid,

  Desktop,
  AmDesktop,
  AmData,
  AmMultiSelect,
  EnumUtil,

  MobileDeviceCommandsContainerView,
  MobileDeviceInstallConfigProfileTemplate
) {
  'use strict';

  var ConfigProfileSelectionController = AmMultiSelect.MobileConfigProfileMultiSelectController.extend(Desktop.ChildController, Desktop.TransientController, {
    targetOs: null,
    installedConfigurationProfiles: null,

    // load the installed applications for these devices
    loadExistingProfiles: function (devices) {
      var mobileDeviceInstalledConfigProfileStore = AmData.get('stores.mobileDeviceInstalledConfigProfileStore');
      this.set('paused', true);
      var self = this;

      for (var i = 0; i < devices.length; i++) {
        var deviceId = devices[i].get('id');

        var query = Query.Search.create({ context: { mobileDeviceId: deviceId } });

        mobileDeviceInstalledConfigProfileStore.acquire(Guid.generate(), query, function(dataSource) {
          var data = dataSource.get('content');

          // Exclude the already installed applications.
          var excludedNames = data.map(function(content) {
            return content.get('content.data.name');
          });

          self.setProperties({
            excludedNames: excludedNames,
            paused: false
          });
        });
      }
    },

    // Filter out config profiles which don't match the target OS platform
    getFilteredData: function (data) {
      var excludedNames = this.get('excludedNames');

      var filteredApps = data.filterBy('data.osPlatformEnum', this.get('targetOs'));

      if (Em.isEmpty(excludedNames)) { return filteredApps; }

      return EnumUtil.exclude(filteredApps, 'name', excludedNames, null);
    }
  });

  return AmDesktop.ModalActionController.extend({
    ConfigProfileSelectionView: AmDesktop.AmSelectionListView,
    ConfigProfileSelectionController: ConfigProfileSelectionController,

    tHeaderOneDevice: 'amMobileDevice.modals.installConfigurationProfile.headingOneDevice'.tr('deviceName'),
    tHeaderManyDevices: 'amMobileDevice.modals.installConfigurationProfile.headingManyDevices'.tr(),

    tErrorMsg: 'amMobileDevice.modals.installConfigurationProfile.errorMsg'.tr(),
    tErrorMsgMultiplePlatforms: 'amMobileDevice.modals.installConfigurationProfile.errorMsgMultiplePlatforms'.tr(),
    tErrorMsgMultiplePlatformsDetails: 'amMobileDevice.modals.installConfigurationProfile.errorMsgMultiplePlatformsDetails'.tr(),

    actionButtonLabel: 'amMobileDevice.modals.installConfigurationProfile.buttons.actionButtonLabel'.tr(),
    inProgressMsg: 'amMobileDevice.modals.installConfigurationProfile.inProgressMsg'.tr(),
    successMsg: 'amMobileDevice.modals.installConfigurationProfile.successMsg'.tr(),
    errorDetailsMsg:'amMobileDevice.modals.installConfigurationProfile.errorDetailsMsg'.tr(),

    headingIconClass: 'icon-install-config',
    modalActionErrorMsgClass: 'modal-action-error-fullwidth',
    modalActionErrorDetailsClass: 'modal-action-details-fullwidth',

    devices: null,

    configProfileSelectionController: null,

    // The ModalActionController tests the truthiness of this property when deciding which screen to
    // show in onShowModal(). We over-write it in initProperties().
    MobileDevicePropertiesView: Em.View.extend({
      defaultTemplate: Em.Handlebars.compile(MobileDeviceInstallConfigProfileTemplate)
    }),

    confirmationView: MobileDeviceCommandsContainerView,

    urlForHelp: null,

    initProperties: function () {
      var devices = this.get('model');
      var deviceIds = '';

      var targetOs = devices[0].get('data.osPlatformEnum');
      var multipleOs = false;
      for (var i = 0; i < devices.length; i++) {
        deviceIds += devices[i].get('id') + ', ';
        if (devices.length > 1 && devices[i].get('data.osPlatformEnum') !== targetOs) {
          multipleOs = true;
        }
      }

      var confirmationView = this.get('confirmationView');
      var actionFailed = false;
      var showOkBtn = null;
      var errorMsg = this.get('tErrorMsg');
      var errorDetails = null;

      if (multipleOs) {
        confirmationView = Desktop.ModalActionStatusView;
        actionFailed = true;
        showOkBtn = true;
        errorMsg = this.get('tErrorMsgMultiplePlatforms');
        errorDetails = this.get('tErrorMsgMultiplePlatformsDetails');
      }

      var configProfileSelectionController = this.ConfigProfileSelectionController.create({
        parentController: this,
        targetOs: targetOs
      });

      // 10 = magic number
      // Basically we don't want to spam the server with too many requests when the user
      // selects a lot of devices
      if (devices.length <= 10) {
        configProfileSelectionController.loadExistingProfiles(devices);
      }

      this.setProperties({
        devices: devices,
        urlForHelp: Help.uri(1011),

        modalActionWindowClass: this.get('modalActionWindowClass') + ' summary-list',
        confirmationView: confirmationView,

        actionFailed: actionFailed,
        showOkBtn: showOkBtn,
        errorMsg: errorMsg,
        errorDetails: errorDetails,

        isActionBtnDisabled: true,

        configProfileSelectionController: configProfileSelectionController
      });
    },

    heading: function () {
      var template;
      this.get('deviceOneCount') ? template = this.get('tHeaderOneDevice') : template = this.get('tHeaderManyDevices');
      return template;
    }.property('deviceOneCount'),

    deviceOneCount: function () {
      if (this.get('devices.length') === 1) {
        var devices = this.get('devices');
        this.set('deviceName', $('<textarea />').html(devices[0].get('name').toString()).val());
        return true;
      } else {
        this.set('deviceName', '');
        return false;
      }
    }.property('devices.[]'),

    deviceCountDetails: function() {
      var count = this.get('devices.length');
      return (count > 1) ? count : null;
    }.property('devices.[]'),

    onConfigProfileSelectionChanged: function (router, event) {
      // The Install Action Button is only enabled if one or more provisioning profiles is selected
      this.set('isActionBtnDisabled', this.get('configProfileSelectionController.selections').length === 0);
    }.observes('configProfileSelectionController.selections.[]'),

    buildAction: function () {
      this.set('urlForHelp', null);
      return AmData.get('actions.AmMobileDeviceInstallConfigProfileAction').create({
        mobileDeviceIds: this.get('devices')
          .mapBy('id'),
        configurationProfileIds: this.get('configProfileSelectionController.selections')
      });
    }
  });
});
