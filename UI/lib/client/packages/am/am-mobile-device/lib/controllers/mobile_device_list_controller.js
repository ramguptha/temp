define([
  'ember',
  'help',
  'ui',
  'guid',
  'desktop',
  'am-desktop',
  'am-data',
  'packages/am/am-session',
  'packages/platform/formatter',
  '../namespace'
], function (
    Em,
    Help,
    UI,
    Guid,
    Desktop,
    AmDesktop,
    AmData,
    AmSession,
    Formatter,
    AmMobileDevice
) {
  'use strict';

  return AmDesktop.ListControllerColumns.extend({

    selectionEnabled: true,
    hasRowClick: true,
    isButtonGroupHorizontal: false,

    // Mobile device group id
    id: null,

    urlForHelp: Help.uri(1001),
    name: Em.computed.oneWay('mobileDeviceGroup.data.name'),

    visibleColumnNames: 'name model osPlatform osVersion serialNumber lastContact'.w(),
    userPrefsEndpointName: 'deviceListColumns',

    mobileDeviceGroup: null,
    mobileDeviceGroupLock: Guid.generate(),

    dataStore: function () {
      return AmMobileDevice.get('store');
    }.property(),

    breadcrumb: function() {
      return UI.Breadcrumb.create({
        path: 'am_mobile_device_groups.show_group',

        titleResource: 'amMobileDevice.mobileDevicesListPage.mobileDevicesTitle',
        controller: this,
        contextBinding: 'controller.id'
      });
    }.property(),

    loadMobileDeviceList: function (id) {
      this.resetController();
      this.set('id', id);

      var groupStore = AmMobileDevice.get('groupStore');

      this.set('searchQuery.context',
        { mobileDeviceListName: groupStore.materializedObjects[id - 1].data.endPointName });

      this.set('mobileDeviceGroup', groupStore.acquireOne(this.get('mobileDeviceGroupLock'), id, null, null, false, false));
    },

    selectionActions: function () {
      return this.getActionList();
    }.property('selections.[]'),

    getActionList: function () {
      var actions = Em.A(), context = {};

      var lockDeviceCommandSupported = false;
      var clearPasscodeCommandSupported = false;
      var remoteEraseCommandSupported = false;
      var sendMessageCommandSupported = false;
      var updateDeviceInfoCommandSupported = false;
      var setRoamingOptionsCommandSupported = false;
      var installApplicationCommandSupported = false;
      var installConfigProfileCommandSupported = false;
      var installProvisioningProfileCommandSupported = false;
      var setOrganizationInfoCommandSupported = true;
      var setDeviceNameSupported = true;
      var setActivationLockSupported = false;

      var multipleOsSelected = null;

      var selectedItems = this.get('selections');
      if (selectedItems) {
        context = this.getSelectionActionContext(selectedItems, this.get('listRowData'));
      }

      for (var i = 0; i < selectedItems.length ; i++) {
        var device = context[i];
        device.set('model', device);
        var deviceVersion = device.get('model.data.osVersion') ? (device.get('model.data.osVersion') >> 24) & 0xff : -1;

        if (device.get('model.data.isManaged') === 0 || device.get('model.data.osPlatformEnum') == 10 && ((deviceVersion <= 7) || device.get('model.data.isSupervised') !== 1)) {
          setDeviceNameSupported = false;
        }

        if (Em.isNone(multipleOsSelected)) {
          multipleOsSelected = false;
          var osType = null;

          for (var j = 0; j < selectedItems.length ; j++) {
            if (Em.isNone(osType)) {
              osType = context[j].get('model.data.osPlatformEnum');
            } else {
              if (osType != context[j].get('model.data.osPlatformEnum')) {
                multipleOsSelected = true;
                break;
              }
            }
          }
        }

        if (this.supportsIOS7AndUpManagedCommands(device) && AmSession.hasChangeActivationLockPermission() &&
          device.get('model.data.isSupervised') === 1 && !multipleOsSelected) {
          setActivationLockSupported = true;
        }
        if (!this.supportsIOS7AndUpManagedCommands(device) && AmSession.hasChangeOrganizationInfoPermission()) {
          setOrganizationInfoCommandSupported = false;
        }
        if (this.supportsIOSManagedCommands(device) && AmSession.hasLockDevicePermission()) {
          lockDeviceCommandSupported = true;
        }
        if (this.supportsIOSManagedCommands(device) && AmSession.hasClearPasscodePermission()) {
          clearPasscodeCommandSupported = true;
        }
        if ((this.supportsIOSManagedCommands(device) || this.supportsWinPhoneManagedCommands(device))
          && AmSession.hasRemoteErasePermission()) {
          remoteEraseCommandSupported = true;
        }
        if (this.supportsIOSManagedCommands(device) && AmSession.hasSendMessagePermission()) {
          sendMessageCommandSupported = true;
        }
        if ((this.supportsIOSManagedCommands(device) || this.supportsWinPhoneManagedCommands(device))
          && AmSession.hasUpdateDeviceInfoPermission()) {
          updateDeviceInfoCommandSupported = true;
        }
        if (this.supportsSetRoamingOptions(device) && AmSession.hasSetRoamingOptionsPermission()) {
          setRoamingOptionsCommandSupported = true;
        }
        if (this.supportsIOSManagedCommands(device) && AmSession.hasInstallApplicationPermission()) {
          installApplicationCommandSupported = true;
        }

        if (this.supportsIOSManagedCommands(device) && AmSession.hasInstallConfigProfilePermission()) {
          installConfigProfileCommandSupported = true;
        }

        if (this.supportsInstallProvisioningProfiles(device) && AmSession.hasInstallProvisioningProfilePermission()) {
          installProvisioningProfileCommandSupported = true;
        }
      }

      if(lockDeviceCommandSupported) {
        actions.pushObject(Em.Object.create({ labelResource: 'amMobileDevice.mobileDevicesListPage.commands.lockDevice', actionName: 'lockDevice', context: context }));
      }
      if (setActivationLockSupported) {
        actions.pushObject(Em.Object.create({ labelResource: 'amMobileDevice.mobileDevicesListPage.commands.setActivationLock', actionName: 'setActivationLock', context: context }));
      }

      if (clearPasscodeCommandSupported) {
        var name = 'amMobileDevice.mobileDevicesListPage.commands.clearPasscode';

        if (multipleOsSelected || device.get('model.data.osPlatformEnum') == 11) {
          name = 'amMobileDevice.mobileDevicesListPage.commands.clearPasscodeMultiple';
        }

        actions.pushObject(Em.Object.create({ labelResource: name, actionName: 'clearPasscode', context: context }));
      }

      if (remoteEraseCommandSupported) {
        actions.pushObject(Em.Object.create({ labelResource: 'amMobileDevice.mobileDevicesListPage.commands.remoteErase', actionName: 'remoteErase', context: context }));
      }
      if (sendMessageCommandSupported) {
        actions.pushObject(Em.Object.create({ labelResource: 'amMobileDevice.mobileDevicesListPage.commands.sendMessage', actionName: 'sendMessage', context: context }));
      }
      if (updateDeviceInfoCommandSupported) {
        actions.pushObject(Em.Object.create({ labelResource: 'amMobileDevice.mobileDevicesListPage.commands.updateDeviceInfo', actionName: 'updateDeviceInfo', context: context }));
      }
      if (setRoamingOptionsCommandSupported) {
        actions.pushObject(Em.Object.create({ labelResource: 'amMobileDevice.mobileDevicesListPage.commands.setRoamingOptions', actionName: 'setRoamingOptions', context: context }));
      }

      if (!multipleOsSelected) {
        if (installApplicationCommandSupported) {
          actions.pushObject(Em.Object.create({ labelResource: 'amMobileDevice.mobileDevicesListPage.commands.installApplication', actionName: 'installApplication', context: context }));
        }
        if (installConfigProfileCommandSupported) {
          actions.pushObject(Em.Object.create({ labelResource: 'amMobileDevice.mobileDevicesListPage.commands.installConfigProfile', actionName: 'installConfigProfile', context: context }));
        }
        if (installProvisioningProfileCommandSupported) {
          actions.pushObject(Em.Object.create({ labelResource: 'amMobileDevice.mobileDevicesListPage.commands.installProvisioningProfile', actionName: 'installProvisioningProfile', context: context }));
        }
      }

      if (selectedItems.length === 1 && setDeviceNameSupported) {
        actions.pushObject(Em.Object.create({ labelResource: 'amMobileDevice.mobileDevicesListPage.commands.setDeviceName', actionName: 'setDeviceName', context: context }));
      }
      if (setOrganizationInfoCommandSupported ) {
        actions.pushObject(Em.Object.create({ labelResource: 'amMobileDevice.mobileDevicesListPage.commands.setDeviceOrganizationInfo', actionName: 'setDeviceOrganizationInfo', context: context }));
      }

      actions.pushObject(Em.Object.create({ labelResource: 'amMobileDevice.mobileDevicesListPage.commands.setDeviceOwnership', actionName: 'setDeviceOwnership', context: context }));
      actions.pushObject(Em.Object.create({ labelResource: 'amMobileDevice.mobileDevicesListPage.commands.setDeviceEnrollmentUser', actionName: 'setDeviceEnrollmentUser', context: context }));
      actions.pushObject(Em.Object.create({ labelResource: 'amMobileDevice.mobileDevicesListPage.commands.retryAllCommand', actionName: 'retryAllCommand', context: context }));

      return actions;
    },

    supportsSetRoamingOptions: function (device) {
      var model = device.get('model') ? device.get('model') : device;
      var cellularTechnology = model.get('data.cellularTechnologyNumeric');

      return (model.get('data.isManaged') && model.get('data.osPlatformEnum') == 10 &&
        !Em.isEmpty(cellularTechnology) && cellularTechnology != 0);
    },

    // Besides managed iOS devices, this also applies to devices that have AbsoluteApps installed
    // (e.g., Android devices), which support the commands via AbsoluteApps.
    supportsIOSManagedCommands: function (device) {
      var model = device.get('model') ? device.get('model') : device;

      return (model.get('data.isManaged') && (model.get('data.osPlatformEnum') == 10 ||
        !Em.isNone(model.get('data.absoluteAppsVersion'))));
    },

    supportsIOS7AndUpManagedCommands: function (device) {
      var v = device.get('model.data.osVersion');

      if (v == undefined || v == null) return false;
      var ver = (v >> 24) & 0xff;
      return this.supportsIOSManagedCommands(device) && (ver >= 7);
    },

    supportsWinPhoneManagedCommands: function (device) {
      return (device.get('model.data.isManaged') && (device.get('model.data.osPlatformEnum') == 12));
    },

    supportsInstallProvisioningProfiles: function (device) {
      return (device.get('model.data.isManaged') && device.get('model.data.osPlatformEnum') == 10);
    }
  });
});
