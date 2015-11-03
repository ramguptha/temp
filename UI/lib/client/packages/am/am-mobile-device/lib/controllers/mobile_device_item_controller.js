define([
  'ember',
  'help',
  'ui',
  'desktop',
  'am-desktop',
  'guid',
  'locale',

  'packages/platform/formatter',
  'packages/am/am-session',
  'packages/platform/storage',

  'text!../templates/mobile_device_list_item_warning.handlebars',
  'text!../templates/mobile_device_item_button_block.handlebars',

  '../namespace'
], function (
  Em,
  Help,
  UI,
  Desktop,
  AmDesktop,
  Guid,
  Locale,

  Formatter,
  AmSession,
  Storage,

  warningTemplate,
  buttonBlockTemplate,

  AmMobileDevice
) {
  'use strict';

  return Em.Controller.extend({

    // Navigation
    actions: {
      rowClick: function(row) {
        this.send('gotoDeviceNavItem', row.get('node.id'));
      }
    },

    tAssignedItemsName: 'amMobileDevice.devicePage.tabLabels.assignedItems'.tr(),
    tJailBrokenLabelRooted: 'amMobileDevice.devicePage.aboutDeviceTab.jailBrokenLabelRooted'.tr(),
    tJailBrokenLabelJailBroken: 'amMobileDevice.devicePage.aboutDeviceTab.jailBrokenLabelJailBroken'.tr(),
    tNotAvailable: 'shared.baseline'.tr(),
    tDeviceDescription: 'amMobileDevice.devicePage.deviceDescription'.tr('modelUI','ownershipUI','udidUI','serialNumberUI'),

    amMobileDeviceGroupsShowGroupController: Em.inject.controller('amMobileDeviceGroupsShowGroup'),

    urlForHelp: Help.uri(1002),
    id: null,
    lock: Guid.generate(),
    activeTab: null,

    selectedId: Em.computed.oneWay('amMobileDeviceGroupsShowGroupController.id'),

    namespace: function () { return AmMobileDevice; }.property(),

    tabCompoundItemView: Desktop.TabCompoundItemView,
    tabItemView: Desktop.TabItemView,
    buttonBlockView: AmDesktop.AmNavTabPageView.ButtonBlockView.extend({
      defaultTemplate: Em.Handlebars.compile(buttonBlockTemplate)
    }),

    warningView: AmDesktop.AmNavTabPageView.WarningView.extend({
      defaultTemplate: Em.Handlebars.compile(warningTemplate)
    }),

    mobileDeviceListController: Em.computed.oneWay('amMobileDeviceGroupsShowGroupController'),
    navSizeController: Em.inject.controller('amMobileDeviceNavSize'),

    NavController: AmDesktop.AmNavController.extend({
      content: Em.computed.alias('parentController.amMobileDeviceGroupsShowGroupController'),
      searchQuery: Em.computed.oneWay('parentController.mobileDeviceListController.searchQuery'),

      dataStore: function () {
        return AmMobileDevice.get('store');
      }.property()
    }),

    navController: function() {
      return this.NavController.create({
        parentController: this,
        selectedIdBinding: 'parentController.id'
      });
    }.property('model'),

    init: function () {
      this._super();

      var settings = Storage.read('am.MobileDevices.SlideStateSettings', true);
      if (settings) {
        this.get('slideSettings').setProperties({
          storageCapacity: settings.storageCapacity,
          networking: settings.networking,
          storage: settings.storage,
          hardware: settings.hardware,
          systemMemory: settings.systemMemory,
          cellularInformation: settings.cellularInformation,
          remoteWipe: settings.remoteWipe,
          exchangeServer: settings.exchangeServer,
          organizationalInfo: settings.organizationalInfo,
          lastChangedItems: settings.lastChangedItems
        });
      }
    },

    loadMobileDevice: function (id, force) {
      var self = this;
      this.set('id', id);

      this.set('model', AmMobileDevice.get('itemstore').acquireOne(this.get('lock'), id, function(dataSource) {
        var deviceOsPlatform = dataSource.get('content')[0].get('data.osPlatformEnum'),
          isManaged = dataSource.get('content')[0].get('data.isManaged'),
          activeTab = self.get('activeTab');

        if ((deviceOsPlatform === 11 && (activeTab === 'provisioningProfiles' || activeTab === 'certificates')) ||
          (isManaged === 0 && (activeTab !== 'customFields' && activeTab !== 'settings' && activeTab !== 'applications'
          && activeTab !== 'administrators' && activeTab !== 'actions'))) {
          self.get('activeTab', 'settings');
          self.transitionTo('am_mobile_device_item.settings');
        }
      }, null, Em.isNone(force) ? false : force, false));
    },

    forceUpdate: function (controller, id) {
      if (!Em.isNone(id)) {
        this.set('model', AmMobileDevice.get('itemstore').acquireOne(this.get('lock'), id, function() {
          controller.setProperties({
            actionInProgress: false,
            statusMsg: controller.get('successMsg'),
            showOkBtn: true
          });
        }, null, true, false));
      }
    },

    breadcrumb: function() {
      return UI.Breadcrumb.create({
        parentBreadcrumb: this.get('amMobileDeviceGroupsShowGroupController.breadcrumb'),
        path: 'am_mobile_devices_related_show_device.settings',

        titleResource: 'amMobileDevice.devicePage.title',
        controller: this,
        contextBinding: 'controller.id'
      });
    }.property('amMobileDeviceGroupsShowGroupController.breadcrumb'),

    tabList: function () {
      var deviceListCtrl = this.get('amMobileDeviceGroupsShowGroupController');
      var device = this.get('model.data');
      if (Em.isNone(device)) { // data not yet loaded
        return null;
      }

      var tabList = [
        Em.Object.create({
          labelResource: 'amMobileDevice.devicePage.tabLabels.aboutDevice',
          item: 'settings',
          itemClass: 'is-button-for-tab-about-device'
        })
      ];

      var assignedItems = [];

      if (deviceListCtrl.supportsIOSManagedCommands(this) || deviceListCtrl.supportsWinPhoneManagedCommands(this)) {
        tabList.pushObject(
          Em.Object.create({
            labelResource: 'amMobileDevice.devicePage.tabLabels.mobilePolicies',
            item: 'mobilePolicies',
            itemClass: 'is-button-for-tab-mobile-policies'
          })
        );
      }
      // Installed Applications available on all but Windows Phones
      if (device.get('osPlatformEnum') != 12) {
        tabList.pushObject(
          Em.Object.create({
            labelResource: 'amMobileDevice.devicePage.tabLabels.applications',
            item: 'applications',
            itemClass: 'is-button-for-tab-mobile-applications'
          })
        );
      }
      // Certificates only apply to managed iOS devices
      if (deviceListCtrl.supportsIOSManagedCommands(this) && device.get('osPlatformEnum') == 10) {
        tabList.pushObject(
          Em.Object.create({
            labelResource: 'amMobileDevice.devicePage.tabLabels.certificates',
            item: 'certificates',
            itemClass: 'is-button-for-tab-certificates'
          })
        );
      }
      if (deviceListCtrl.supportsIOSManagedCommands(this)) {
        tabList.pushObject(
          Em.Object.create({
            labelResource: 'amMobileDevice.devicePage.tabLabels.configurationProfiles',
            item: 'configProfiles',
            itemClass: 'is-button-for-tab-config-profiles'
          })
        );
      }
      if (deviceListCtrl.supportsIOSManagedCommands(this) && device.get('osPlatformEnum') == 10) {
        tabList.pushObject(
          Em.Object.create({
            labelResource: 'amMobileDevice.devicePage.tabLabels.provisioningProfiles',
            item: 'provisioningProfiles',
            itemClass: 'is-button-for-tab-provisioning-profiles'
          })
        );
      }
      if (deviceListCtrl.supportsIOSManagedCommands(this)) {
        assignedItems.label = this.get('tAssignedItemsName');
        assignedItems.itemClass = 'is-label-for-tab-assigned-items';
        assignedItems.pushObject(
          Em.Object.create({
            labelResource: 'amMobileDevice.devicePage.tabLabels.assignedItemsThirdPartyApps',
            item: 'assignedThirdPartyApps',
            itemClass: 'is-button-for-tab-assigned-third-party-apps'
          })
        );
        assignedItems.pushObject(
          Em.Object.create({
            labelResource: 'amMobileDevice.devicePage.tabLabels.assignedItemsInHouseApps',
            item: 'assignedInHouseApps',
            itemClass: 'is-button-for-tab-assigned-in-house-apps'
          })
        );
        assignedItems.pushObject(
          Em.Object.create({
            labelResource: 'amMobileDevice.devicePage.tabLabels.assignedItemsContent',
            item: 'content',
            itemClass: 'is-button-for-tab-assigned-content'
          })
        );
        assignedItems.pushObject(
          Em.Object.create({
            labelResource: 'amMobileDevice.devicePage.tabLabels.assignedItemsConfigurationProfiles',
            item: 'assignedProfiles',
            itemClass: 'is-button-for-tab-assigned-profiles'
          })
        );
        tabList.pushObject(assignedItems);
      }
      tabList.pushObject(
        Em.Object.create({
          labelResource: 'amMobileDevice.devicePage.tabLabels.customFields',
          item: 'customFields',
          itemClass: 'is-button-for-tab-custom-fields'
        })
      );
      tabList.pushObject(
        Em.Object.create({
          labelResource: 'amMobileDevice.devicePage.tabLabels.administrators',
          item: 'administrators',
          itemClass: 'is-button-for-tab-administrators'
        })
      );
      if (deviceListCtrl.supportsIOSManagedCommands(this) || deviceListCtrl.supportsWinPhoneManagedCommands(device)) {
        tabList.pushObject(
          Em.Object.create({
            labelResource: 'amMobileDevice.devicePage.tabLabels.user',
            item: 'user',
            itemClass: 'is-button-for-tab-user'
          })
        );
      }
      tabList.pushObject(
        Em.Object.create({
          labelResource: 'amMobileDevice.devicePage.tabLabels.actions',
          item: 'actions',
          itemClass: 'is-button-for-tab-performed-actions'
        })
      );

      return tabList;
    }.property('model.data.osPlatformEnum', 'model.data.isManaged', 'model.data.absoluteAppsVersion'),

    hasWarning: function () {
      return (this.deviceSupportsOneOrMoreCommands() == false);
    }.property('model.data.osPlatformEnum', 'model.data.isManaged', 'model.data.absoluteAppsVersion', 'model.data.cellularTechnology'),

    isAndroidDevice: function () {
      return (this.get('model.data.osPlatformEnum') == 11);
    }.property('model.data.osPlatformEnum'),

    isIOSDevice: function () {
      return (this.get('model.data.osPlatformEnum') == 10);
    }.property('model.data.osPlatformEnum'),

    isWinPhoneDevice: function () {
      return (this.get('model.data.osPlatformEnum') == 12);
    }.property('model.data.osPlatformEnum'),

    isNotWinPhoneDevice: function () {
      return (this.get('model.data.osPlatformEnum') != 12);
    }.property('model.data.osPlatformEnum'),

    hasCellularCapability: function () {
      var cellularTechnology = this.get('model.data.cellularTechnologyNumeric');
      // We currently make a special case for Windows Phone to always return true as it doesn't have a cellular technology field
      return ((!Em.isEmpty(cellularTechnology) && cellularTechnology != 0) || this.get('model.data.osPlatformEnum') == 12);
    }.property('model.data.cellularTechnologyNumeric', 'model.data.osPlatformEnum'),

    warrantyEndDate: function () {
      // String in the database: data.warrantyInfo = AppleWarrantyStatus (varchar(255)). Do not need any localization.
      if (this.get('model.data.warrantyInfo') == 'Out of Warranty') {
        return this.get('tNotAvailable');
      }
      else {
        return Formatter.formatDate(this.get('model.data.warrantyEnd'));
      }
    }.property('model.data.warrantyEnd'),

    absoluteAppsVersion: function () {
      return (Formatter.formatOSVersion(this.get('model.data.absoluteAppsVersion')));
    }.property('model.data.absoluteAppsVersion'),

    absoluteAppsBuildNo: function () {
      return (Formatter.toStringOrNA(this.get('model.data.absoluteAppsBuildNo')));
    }.property('model.data.absoluteAppsBuildNo'),

    canIssueLockDeviceCommand: function () {
      return (this.get('mobileDeviceListController')).supportsIOSManagedCommands(this) &&
        AmSession.hasLockDevicePermission();
    }.property('model.data.osPlatformEnum', 'model.data.isManaged', 'model.data.absoluteAppsVersion'),

    canIssueClearPasscodeCommand: function () {
      return (this.get('mobileDeviceListController')).supportsIOSManagedCommands(this) &&
        AmSession.hasClearPasscodePermission();
    }.property('model.data.osPlatformEnum', 'model.data.isManaged', 'model.data.absoluteAppsVersion'),

    canIssueRemoteEraseCommand: function () {
      return ((this.get('mobileDeviceListController')).supportsIOSManagedCommands(this) ||
        (this.get('mobileDeviceListController')).supportsWinPhoneManagedCommands(this)) &&
        AmSession.hasRemoteErasePermission();
    }.property('model.data.osPlatformEnum', 'model.data.isManaged', 'model.data.absoluteAppsVersion'),

    canIssueSendMessageCommand: function () {
      return (this.get('mobileDeviceListController')).supportsIOSManagedCommands(this) &&
        AmSession.hasSendMessagePermission();
    }.property('model.data.osPlatformEnum', 'model.data.isManaged', 'model.data.absoluteAppsVersion'),

    canIssueUpdateDeviceInfoCommand: function () {
      return ((this.get('mobileDeviceListController')).supportsIOSManagedCommands(this) ||
        (this.get('mobileDeviceListController')).supportsWinPhoneManagedCommands(this)) &&
        AmSession.hasUpdateDeviceInfoPermission();
    }.property('model.data.osPlatformEnum', 'model.data.isManaged', 'model.data.absoluteAppsVersion'),

    canIssueSetRoamingOptions: function () {
      return (this.get('mobileDeviceListController')).supportsSetRoamingOptions(this) &&
        AmSession.hasSetRoamingOptionsPermission();
    }.property('model.data.isManaged', 'model.data.osPlatformEnum', 'model.data.cellularTechnology'),

    canIssueInstallApplication: function () {
      return (this.get('mobileDeviceListController')).supportsIOSManagedCommands(this) &&
        AmSession.hasInstallApplicationPermission();
    }.property('model.data.osPlatformEnum', 'model.data.isManaged', 'model.data.absoluteAppsVersion'),

    canIssueInstallConfigurationProfile: function () {
      return (this.get('mobileDeviceListController')).supportsIOSManagedCommands(this) &&
        AmSession.hasInstallConfigProfilePermission();
    }.property('model.data.osPlatformEnum', 'model.data.isManaged', 'model.data.absoluteAppsVersion'),

    canIssueInstallProvisioningProfile: function () {
      return (this.get('mobileDeviceListController')).supportsInstallProvisioningProfiles(this) &&
        AmSession.hasInstallProvisioningProfilePermission();
    }.property('model.data.osPlatformEnum', 'model.data.isManaged', 'model.data.absoluteAppsVersion'),

    canSetOrgInfo: function () {
      return (this.get('mobileDeviceListController')).supportsIOS7AndUpManagedCommands(this) &&
        AmSession.hasChangeOrganizationInfoPermission();
    }.property('model.data.osPlatformEnum'),

    canSetLockOptions: function () {
      return (this.get('mobileDeviceListController')).supportsIOS7AndUpManagedCommands(this) && AmSession.hasChangeActivationLockPermission() &&
        this.get('model.data.isSupervised') === 1;
    }.property('model.data.osPlatformEnum', 'model.data.isSupervised'),

    canSetName: function () {
      var deviceVersion = this.get('model.data.osVersion') ? (this.get('model.data.osVersion') >> 24) & 0xff : -1;
      // iOS: this.get('model.data.osPlatformEnum') === 10

      return (this.get('model.data.isManaged') === 1 && this.get('model.data.osPlatformEnum') != 10) ||
        (this.get('model.data.osPlatformEnum') === 10 && (deviceVersion > 7) && this.get('model.data.isSupervised') === 1 && this.get('model.data.isManaged') === 1);
    }.property('model.data.osPlatformEnum', 'model.data.isManaged', 'model.data.isSupervised', 'model.data.osVersion'),

    //canSetDeviceOwnership: function () {
    //    return (AmMobileDevice.get('mobileDeviceListController')).supportsIOSManagedCommands(this) &&
    //      AmSession.hasInstallConfigProfilePermission();
    //}.property('osPlatformEnum', 'isManaged', 'absoluteAppsVersion'),


    userHasNoCommandPermissions: function () {
      var canIssueIOSManagedCommands =
        (this.get('mobileDeviceListController')).supportsIOSManagedCommands(this) &&
        (AmSession.hasLockDevicePermission() || AmSession.hasClearPasscodePermission() ||
        AmSession.hasRemoteErasePermission() || AmSession.hasSendMessagePermission() ||
        AmSession.hasUpdateDeviceInfoPermission() || AmSession.hasInstallApplicationPermission() ||
        AmSession.hasInstallConfigProfilePermission());
      var canIssueSetRoamingOptionsCommands =
        (this.get('mobileDeviceListController')).supportsSetRoamingOptions(this) &&
        AmSession.hasSetRoamingOptionsPermission();
      var canIssueWinPhoneCommands =
        (this.get('mobileDeviceListController')).supportsWinPhoneManagedCommands(this) &&
        (AmSession.hasRemoteErasePermission() || AmSession.hasUpdateDeviceInfoPermission());

      return (this.deviceSupportsOneOrMoreCommands() && !canIssueIOSManagedCommands &&
      !canIssueSetRoamingOptionsCommands && !canIssueWinPhoneCommands);
    }.property('model.data.osPlatformEnum', 'model.data.isManaged', 'model.data.absoluteAppsVersion', 'model.data.cellularTechnology'),

    isAndroidAndSupportsNoCommands: function () {
      return (this.get('model.data.osPlatformEnum') == 11 && this.deviceSupportsOneOrMoreCommands() == false);
    }.property('model.data.osPlatformEnum', 'model.data.isManaged', 'model.data.absoluteAppsVersion'),

    isIOSAndSupportsNoCommands: function () {
      return (this.get('model.data.osPlatformEnum') == 10 && this.deviceSupportsOneOrMoreCommands() == false);
    }.property('model.data.osPlatformEnum', 'model.data.isManaged', 'model.data.cellularTechnology'),

    isWinPhoneAndSupportsNoCommands: function () {
      return (this.get('model.data.osPlatformEnum') == 12 && this.deviceSupportsOneOrMoreCommands() == false);
    }.property('model.data.osPlatformEnum', 'model.data.isManaged'),

    deviceSupportsOneOrMoreCommands: function () {
      var deviceListController = this.get('amMobileDeviceGroupsShowGroupController');
      return (deviceListController.supportsIOSManagedCommands(this) ||
      deviceListController.supportsWinPhoneManagedCommands(this) ||
      deviceListController.supportsSetRoamingOptions(this));
    },

    isMdmProfileUpToDate: function () {
      return (this.get('model.data.isManaged') ? Formatter.formatBooleanOrNA(this.get('model.data.isMdmProfileUpToDate')) : this.get('tNotAvailable'));
    }.property('model.data.isManaged', 'model.data.isMdmProfileUpToDate'),

    usedCapacity: function () {
      var deviceCapacity = this.get('model.data.deviceCapacity');
      var availableCapacity = this.get('model.data.availableCapacity');
      return (Formatter.formatBytes(deviceCapacity - availableCapacity));
    }.property('model.data.deviceCapacity', 'model.data.availableCapacity'),

    snapContainerIconPath: function () {
      var osPlatformEnum = this.get('model.data.osPlatformEnum');
      var isTablet = this.get('model.data.isTablet');
      var iconFolder = '../packages/platform/desktop/icons/os-logos/';
      switch (osPlatformEnum) {
        case 1:  // Mac OS X
          return iconFolder + '32-apple-osx.png';
        case 10: // iOS
          return iconFolder + '32-apple-ios-b.png';
        case 2: // Windows (currently assume this will be used for Windows8)
          return iconFolder + '32-windows-8.png';
        case 11: // Android
          return iconFolder + '32-android.png';
        case 12: // Windows Phone
          return iconFolder + '32-windows-8phone.png';
      }
    }.property('model.data.osPlatformEnum', 'model.data.isTablet'),

    name: function() {
      if(!Em.isEmpty(this.get('model.data.name'))) {
        return this.get('model.data.name');
      }
    }.property('model.data.name'),

    modelUI: function() {
      if(!Em.isEmpty(this.get('model.data.model'))) {
        return this.get('model.data.model');
      }
    }.property('model.data.model'),

    ownershipUI: function() {
      if(!Em.isEmpty(this.get('model.data.ownership'))) {
        return this.get('model.data.ownership');
      }
    }.property('model.data.ownership'),

    udidUI: function() {
      if(!Em.isEmpty(this.get('model.data.udid'))) {
        return this.get('model.data.udid');
      }
    }.property('model.data.udid'),

    serialNumberUI: function() {
      if(!Em.isEmpty(this.get('model.data.serialNumber'))) {
        return this.get('model.data.serialNumber');
      }
    }.property('model.data.serialNumber'),

    snapContainerSubTitle: function () {
      var ownershipNumeric = this.get('model.data.ownershipNumeric');
      var ownership = this.get('model.data.ownership');

      if (ownershipNumeric == 0 || ownershipNumeric == null) {
        ownership = this.get('tNotAvailable');
      }

      this.set('ownership', ownership);

      return this.get('tDeviceDescription');
    }.property('model.datamodelUI', 'model.dataownershipUI', 'model.dataudidUI', 'model.dataserialNumberUI'),

    snapContainerContentClass: function () {
      return (this.deviceSupportsOneOrMoreCommands() ? 'snap-container-content' : 'snap-container-content unmanaged-device');
    }.property('model.data.osPlatformEnum', 'model.data.isManaged', 'model.data.absoluteAppsVersion', 'model.data.cellularTechnology'),

    deviceIconPath: function () {
      var osPlatformEnum = this.get('model.data.osPlatformEnum');
      var isTablet = this.get('model.data.isTablet');
      var iconFolder = '../packages/platform/desktop/icons/devices/';
      var model = this.get('model.data.model');
      switch (osPlatformEnum) {
        case 1:  // Mac OS X (currently don't have a specific Mac OS X device icon, but won't get this anyway for Mobile devices?)
        case 10: // iOS
          // TODO localization ???, it works for Jp
          if (model.toLowerCase().search('apple tv') != -1) {
            return iconFolder + 'apple-tv-84.png';
          }
          return iconFolder + (isTablet ? 'ipad-84.png' : 'iphone-84.png');
        case 2: // Windows (currently assume this will be used for Windows8)
          return '../packages/platform/desktop/icons/os-logos/' + '64-windows-8.png';  // TODO update when windows device icons available
        case 11: // Android
          return iconFolder + (isTablet ? 'android-tablet-84.png' : 'android-phone-84.png');
        case 12: // Windows Phone
          return '../packages/platform/desktop/icons/os-logos/' + '64-windows-8phone.png';// TODO update when windows phone device icons available
      }
    }.property('model.data.osPlatformEnum', 'model.data.isTablet'),

    osIconPath: function () {
      var osPlatformEnum = this.get('model.data.osPlatformEnum');
      var iconFolder = '../packages/platform/desktop/icons/os-logos/';
      switch (osPlatformEnum) {
        case 1:  // Mac OS X
          return iconFolder + '16-apple-osx.png';
        case 10: // iOS
          return iconFolder + '16-apple-ios-b.png';
        case 2: // Windows (currently assume this will be used for Windows8)
          return iconFolder + '16-windows-8.png';
        case 11: // Android
          return iconFolder + '16-android.png';
        case 12: // Windows Phone
          return iconFolder + '16-windows-8phone.png';
      }
    }.property('model.data.osPlatformEnum'),

    passcodePresentIconClass: function () {
      return (this.get('model.data.isPasscodePresent')) ? 'icon-locked' : 'icon-unlocked';
    }.property('model.data.isPasscodePresent'),

    isTabletIconClass: function () {
      return (this.get('model.data.isTablet')) ? 'icon-tablet-device' : 'icon-phone-device';
    }.property('model.data.isTablet'),

    jailBrokenLabel: function () {
      var osPlatformEnum = this.get('model.data.osPlatformEnum');
      switch (osPlatformEnum) {
        case 11: // Android
          return this.get('tJailBrokenLabelRooted');
        default:
          return this.get('tJailBrokenLabelJailBroken');
      }
    }.property('model.data.osPlatformEnum'),

    ownership: function () {
      var ownship = this.get('model.data.ownership');

      return {
        isGuest: ownship == '1',
        isCompany: ownship == '2',
        isUser: ownship == '3',
        isUndefined: ownship == '0' || ownship == undefined
      }
    }.property('model.data.ownership'),

    batteryLevel: function() {
      var batteryLevel = this.get('model.data.batteryLevel');

      if( Em.isNone(batteryLevel) ) {
        return Locale.notAvailable();
      }

      if( isNaN(batteryLevel) ) {
        return null;
      }

      return batteryLevel.toFixed(0);
    }.property('model.data.batteryLevel'),

    batteryLevelIconClass: function () {
      var icon = 'icon-battery';
      var level = this.get('batteryLevel');
      // Small level
      if (level <= 15) {
        icon = 'icon-battery-low';
      } else if (level >= 16 && level <= 49) {
        // Middle level
        icon = 'icon-battery-med';
      } else {
        icon = 'icon-battery-high';
      }

      return icon;
    }.property('batteryLevel'),


    // Open-Close slide state structure to save in Local Storage
    slideSettings: Em.Object.create({
      storageCapacity: false,
      networking: false,
      storage: false,
      hardware: false,
      systemMemory: false,
      cellularInformation: false,
      remoteWipe: false,
      exchangeServer: false,
      organizationalInfo: false,
      lastChangedItems: false,
      slideSettingsProperties: function () {
        Storage.write('am.MobileDevices.SlideStateSettings', this, true);
      }.observes('storageCapacity', 'networking', 'storage', 'hardware', 'systemMemory', 'cellularInformation', 'remoteWipe', 'exchangeServer', 'organizationalInfo', 'lastChangedItems')
    })

  });
});
