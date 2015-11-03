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
  'text!../templates/mobile_device_install_provisioning_profile.handlebars'
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
  MobileDeviceInstallProvisioningProfileTemplate
  ) {
  'use strict';

  var ProvisioningProfileSelectionController = AmMultiSelect.MobileProvProfileMultiSelectController.extend(Desktop.ChildController, Desktop.TransientController, {
    // load the installed provisioning profiles for these devices
    loadExistingProfiles: function (devices) {
      var mobileDeviceInstalledProvisioningProfileStore = AmData.get('stores.mobileDeviceInstalledProvisioningProfileStore');
      this.set('paused', true);
      var self = this;

      for (var i = 0; i < devices.length; i++) {
        var deviceId = devices[i].get('id');

        var query = Query.Search.create({ context: { mobileDeviceId: deviceId } });

        mobileDeviceInstalledProvisioningProfileStore.acquire(Guid.generate(), query, function(dataSource) {
          var data = dataSource.get('content');

          // Exclude the already installed provisioning profiles
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

    getFilteredData: function (data) {
      var excludedNames = this.get('excludedNames');

      if (Em.isEmpty(excludedNames)) { return data; }
       return EnumUtil.exclude(data, 'name', excludedNames, null);
    }
  });

  return AmDesktop.ModalActionController.extend({
    ProvisioningProfileSelectionView: AmDesktop.AmSelectionListView,
    ProvisioningProfileSelectionController: ProvisioningProfileSelectionController,

    tHeaderOneDevice: 'amMobileDevice.modals.installProvisioningProfile.headingOneDevice'.tr('deviceName'),
    tHeaderManyDevices: 'amMobileDevice.modals.installProvisioningProfile.headingManyDevices'.tr(),

    tErrorMsg: 'amMobileDevice.modals.installProvisioningProfile.errorMsg'.tr(),
    tErrorMsgMultiplePlatforms: 'amMobileDevice.modals.installProvisioningProfile.errorMsgMultiplePlatforms'.tr(),
    tErrorMsgMultiplePlatformsDetails: 'amMobileDevice.modals.installProvisioningProfile.errorMsgMultiplePlatformsDetails'.tr(),

    headingIconClass: 'icon-install-config',

    actionButtonLabel: 'amMobileDevice.modals.installProvisioningProfile.buttons.actionButtonLabel'.tr(),

    inProgressMsg:  'amMobileDevice.modals.installProvisioningProfile.inProgressMsg'.tr(),
    successMsg:  'amMobileDevice.modals.installProvisioningProfile.successMsg'.tr(),

    errorDetailsMsg:  'amMobileDevice.modals.installProvisioningProfile.errorDetailsMsg'.tr(),

    modalActionErrorMsgClass: 'modal-action-error-fullwidth',
    modalActionErrorDetailsClass: 'modal-action-details-fullwidth',

    devices: null,

    provisioningProfileSelectionController: null,

    // The ModalActionController tests the truthiness of this property when deciding which screen to
    // show in onShowModal(). We over-write it in initProperties().
    MobileDevicePropertiesView: Em.View.extend({
      defaultTemplate: Em.Handlebars.compile(MobileDeviceInstallProvisioningProfileTemplate)
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

      var provisioningProfileSelectionController = this.ProvisioningProfileSelectionController.create({
        parentController: this,

        targetOs: targetOs
      });

      // 10 = magic number
      // Basically we don't want to spam the server with too many requests when the user
      // selects a lot of devices
      if (devices.length <= 10) {
        provisioningProfileSelectionController.loadExistingProfiles(devices);
      }

      this.setProperties({
        devices: devices,
        urlForHelp: Help.uri(1012),

        modalActionWindowClass: this.get('modalActionWindowClass') + ' summary-list',
        confirmationView: confirmationView,

        actionFailed: actionFailed,
        showOkBtn: showOkBtn,
        errorMsg: errorMsg,
        errorDetails: errorDetails,

        isActionBtnDisabled: true,

        provisioningProfileSelectionController: provisioningProfileSelectionController
      });
    },

    onProvisioningProfileSelectionChanged: function (router, event) {
      // The Install Action Button is only enabled if one or more provisioning profiles is selected
      this.set('isActionBtnDisabled', this.get('provisioningProfileSelectionController.selections').length === 0);
    }.observes('provisioningProfileSelectionController.selections.[]'),

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

    buildAction: function () {
      this.set('urlForHelp', null);
      return AmData.get('actions.AmMobileDeviceInstallProvisioningProfileAction').create({
        mobileDeviceIds: this.get('devices').mapBy('id'),
        provisioningProfileIds: this.get('provisioningProfileSelectionController.selections')
      });
    }
  });
});
