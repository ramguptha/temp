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
  'text!../templates/mobile_device_install_application.handlebars'
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
  MobileDeviceInstallApplicationTemplate
) {
  'use strict';

    var InHouseAppSelectionController = AmMultiSelect.InHouseAppMultiSelectController.extend(Desktop.ChildController, Desktop.TransientController, {
    targetOs: null,
    minTargetOsVersion: null,

    // load the installed applications for these devices
    loadExistingApplications: function (devices) {
      var mobileDeviceInstalledAppStore = AmData.get('stores.mobileDeviceInstalledAppStore');
      this.set('paused', true);
      var self = this;

      for (var i = 0; i < devices.length; i++) {
        var deviceId = devices[i].get('id');

        var query = Query.Search.create({ context: { mobileDeviceId: deviceId } });

        mobileDeviceInstalledAppStore.acquire(Guid.generate(), query, function(dataSource) {
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

    getFilteredData: function (data) {
      var minTargetOsVersion = this.get('minTargetOsVersion');
      var excludedNames = this.get('excludedNames');

      var apps = data.filterBy('data.osPlatformEnum', this.get('targetOs'));

      var filteredApps = apps.filter(function (app) {
        return minTargetOsVersion >= app.get('data.minOsVersion');
      });

      if (Em.isEmpty(excludedNames)) { return filteredApps; }

      return EnumUtil.exclude(filteredApps, 'name', excludedNames, null);
    }
  });

  var ThirdPartyAppSelectionController = AmMultiSelect.ThirdPartyAppMultiSelectController.extend(Desktop.ChildController, Desktop.TransientController, {
    targetOs: null,
    minTargetOsVersion: null,

    // load the installed applications for these devices
    loadExistingApplications: function (devices) {
      var mobileDeviceInstalledAppStore = AmData.get('stores.mobileDeviceInstalledAppStore');
      this.set('paused', true);
      var self = this;

      for (var i = 0; i < devices.length; i++) {
        var deviceId = devices[i].get('id');

        var query = Query.Search.create({ context: { mobileDeviceId: deviceId } });

        mobileDeviceInstalledAppStore.acquire(Guid.generate(), query, function(dataSource) {
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

    getFilteredData: function (data) {
      var minTargetOsVersion = this.get('minTargetOsVersion');
      var excludedNames = this.get('excludedNames');

      var apps = data.filterBy('data.osPlatformEnum', this.get('targetOs'));

      // Also set the icon url based on the app id
      for (var i = 0; i < apps.get('length') ; i++) {
        var app = (apps.objectAt(i)).get('data');
        var id = app.id;
        var iconUrl = AmData.get('urlRoot') + '/api/thirdpartyapps/' + id + '/icon';
        (apps.objectAt(i)).set('data.icon', iconUrl);
      }

      var filteredApps = apps.filter(function (app) {
        return minTargetOsVersion >= app.get('data.minOsVersion');
      });

      if (Em.isEmpty(excludedNames)) { return filteredApps; }

      return EnumUtil.exclude(filteredApps, 'name', excludedNames, null);
    }
  });

  return AmDesktop.ModalActionController.extend({

    tHeaderOneDevice: 'amMobileDevice.modals.installApplication.headingOneDevice'.tr('deviceName'),
    tHeaderManyDevices: 'amMobileDevice.modals.installApplication.headingManyDevices'.tr(),

    headingIconClass: 'icon-install-app',
    modalActionWindowClass: 'modal-action-window summary-list',

    actionButtonLabel: 'amMobileDevice.modals.installApplication.buttons.actionButtonLabel'.tr(),

    inProgressMsg: 'amMobileDevice.modals.installApplication.inProgressMsg'.tr(),
    successMsg: 'amMobileDevice.modals.installApplication.successMsg'.tr(),

    modalActionErrorMsgClass: 'modal-action-error-fullwidth',
    modalActionErrorDetailsClass: 'modal-action-details-fullwidth',

    devices: null,
    deviceName: null,

    tErrorMsg: 'amMobileDevice.modals.installApplication.errorMsg'.tr(),
    tErrorMsgMultiplePlatforms: 'amMobileDevice.modals.installApplication.errorMsgMultiplePlatforms'.tr(),
    tErrorMsgMultiplePlatformsDetails: 'amMobileDevice.modals.installApplication.errorMsgMultiplePlatformsDetails'.tr(),

    MobileDevicePropertiesView: Em.View.extend({
      defaultTemplate: Em.Handlebars.compile(MobileDeviceInstallApplicationTemplate)
    }),

    confirmationView: MobileDeviceCommandsContainerView,
    InHouseAppSelectionView: AmDesktop.AmSelectionListView,
    ThirdPartyAppSelectionView: AmDesktop.AmSelectionListView,

    InHouseAppSelectionController: InHouseAppSelectionController,
    ThirdPartyAppSelectionController: ThirdPartyAppSelectionController,

    inHouseAppSelectionController: null,
    thirdPartyAppSelectionController: null,

    urlForHelp: null,

    initProperties: function () {
      var devices = this.get('model');
      var deviceIds = '';

      var targetOs = devices[0].get('data.osPlatformEnum');
      var minTargetOsVersion = devices[0].get('data.osVersion');
      var multipleOs = false;
      for (var i = 0; i < devices.length; i++) {
        if (i > 0) { deviceIds += ','; }
        deviceIds += devices[i].get('id');
        if (devices[i].get('data.osVersion') < minTargetOsVersion) {
          minTargetOsVersion = devices[i].get('data.osVersion');
        }
        if (devices.length > 1 && devices[i].get('data.osPlatformEnum') !== targetOs) {
          multipleOs = true;
        }
      }

      var actionFailed = false;
      var showOkBtn = null;
      var errorMsg = this.get('tErrorMsg');
      var errorDetails = null;
      var confirmationView = this.get('confirmationView');

      if (multipleOs) {
        confirmationView = Desktop.ModalActionStatusView;

        actionFailed = true;
        showOkBtn = true;
        errorMsg = this.get('tErrorMsgMultiplePlatforms');
        errorDetails = this.get('tErrorMsgMultiplePlatformsDetails');
      }

      var inHouseAppSelectionController = this.InHouseAppSelectionController.create({
        parentController: this,

        targetOs: targetOs,
        minTargetOsVersion: minTargetOsVersion
      });

      var thirdPartyAppSelectionController = this.ThirdPartyAppSelectionController.create({
        parentController: this,

        targetOs: targetOs,
        minTargetOsVersion: minTargetOsVersion
      });

      // 10 = magic number
      // Basically we don't want to spam the server with too many requests when the user
      // selects a lot of devices
      if (devices.length <= 10) {
        inHouseAppSelectionController.loadExistingApplications(devices);
        thirdPartyAppSelectionController.loadExistingApplications(devices);
      }

      this.setProperties({
        devices: devices,
        urlForHelp: Help.uri(1010),

        confirmationView: confirmationView,

        actionFailed: actionFailed,
        showOkBtn: showOkBtn,
        errorMsg: errorMsg,
        errorDetails: errorDetails,

        isActionBtnDisabled: true,

        inHouseAppSelectionController: inHouseAppSelectionController,
        thirdPartyAppSelectionController: thirdPartyAppSelectionController
      });
    },

    heading: function () {
      var template;
      this.get('deviceOneCount') ? template = this.get('tHeaderOneDevice') : template = this.get('tHeaderManyDevices');
      return template;
    }.property('deviceOneCount'),

    deviceOneCount: function () {
      if (this.get('devices').length === 1) {
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

    onInHouseAppSelectionChanged: function (router, event) {
      // The Install Action Button is only enabled if one or more applications is selected
      this.set('isActionBtnDisabled', this.get('inHouseAppSelectionController.selections').length === 0 &&
        this.get('thirdPartyAppSelectionController.selections').length === 0);

    }.observes('inHouseAppSelectionController.selections.[]'),

    onThirdPartyAppSelectionChanged: function (router, event) {
      // The Install Action Button is only enabled if one or more applications is selected
      this.set('isActionBtnDisabled', this.get('inHouseAppSelectionController.selections').length === 0 &&
        this.get('thirdPartyAppSelectionController.selections').length === 0);

    }.observes('thirdPartyAppSelectionController.selections.[]'),

    buildAction: function () {
      var self = this;
      this.set('urlForHelp', null);

      return AmData.get('actions.AmMobileDeviceInstallApplicationAction').create({
        mobileDeviceIds: self.get('devices').mapBy('id'),
        inHouseAppIds: self.get('inHouseAppSelectionController.selections'),
        thirdPartyAppIds: self.get('thirdPartyAppSelectionController.selections')
      });
    }
  });
});
