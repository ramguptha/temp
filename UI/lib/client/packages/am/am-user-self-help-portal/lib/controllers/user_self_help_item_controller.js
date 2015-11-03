define([
  'ember',
  'ui',
  'desktop',
  'guid',
  'query',
  'locale',

  'packages/platform/formatter',
  'packages/am/am-user-formatter',
  'packages/am/am-session',
  'packages/platform/storage',

  '../namespace'
], function (
  Em,
  UI,
  Desktop,
  Guid,
  Query,
  Locale,

  Formatter,
  AmUserFormatter,
  AmSession,
  Storage,

  AmUserSelfHelpDevice
) {
  'use strict';

  // Controller displays device details for user-self-help portal
  return Em.Controller.extend({
    deviceListController: Em.inject.controller('AmUserSelfHelpDevice'),
    id: null,
    lock: null,
    // Have to hide controls to avoid some unexpected effects with tooltips and command buttons
    displayControls: false,

    namespace: function () { return AmUserSelfHelpDevice; }.property(),

    init: function () {
      this._super();
      this.setProperties({
        lock: Guid.generate()
      });
    },

    loadOneDevice: function (id, completeLoading) {
      var self = this;

      this.set('id', id);

      var query = Query.Search.create({
        autoRefresh: true
      });

      AmUserSelfHelpDevice.get('store').acquire(this.get('lock'), query, function(data) {
          var id = self.get('id');
          var model = null;
          data.get('content').some(function(item) {
            if( item.get('data.identifier') == id ) {
              model = item;
              return true;
            }
          });

          if(model === null) {
            self.transitionToRoute('am_user_self_help_device_list');
          }

          self.set('model', model);

          if(completeLoading) {
            completeLoading();
          }
        },
        null, null, true);
    },

    // Fields
    name: function() {
      var name = this.get('model.data.name');
      if(!Em.isEmpty(name)) {
        var valueArray = name.toString().split('[****]');
        return valueArray[0];
      }
    }.property('model.data.name'),

    batteryLevel: function() {
      var batteryLevel = null;

      if( this.get('model.data.isComputer') ) {
        batteryLevel = (this.get('model.data.batteryCurrentCapacity') / this.get('model.data.batteryDesignCapacity')) * 100;
      } else {
        batteryLevel = this.get('model.data.batteryLevel');
      }

      if( Em.isNone(batteryLevel) ) {
        return Locale.notAvailable();
      }

      if( isNaN(batteryLevel) ) {
        return null;
      }

      return batteryLevel.toFixed(0);
    }.property('model.data.batteryLevel', 'model.data.isComputer', 'model.data.batteryCurrentCapacity', 'model.data.batteryMaxCapacity'),

    phoneNumber: function() {
      return Em.isNone(this.get('model.data.phoneNumber')) ? Locale.notAvailable() : this.get('model.data.phoneNumber');
    }.property('model.data.phoneNumber'),

    deviceCapacity: function() {
      if(!Em.isEmpty(this.get('model.data.deviceCapacity'))) {
        return Formatter.formatBytes(this.get('model.data.deviceCapacity'));
      }
    }.property('model.data.deviceCapacity'),

    osVersion: function() {
      var osVersion = this.get('model.data.osVersion');
      if(!Em.isEmpty(osVersion)) {
        return this.get('model.data.isComputer') ? Formatter.formatOSVersionComputer(osVersion) : Formatter.formatOSVersion(osVersion);
      }
    }.property('model.osVersion', 'model.data.isComputer'),

    isNotWinPhoneDevice: function () {
      return true;
    }.property('model.deviceType'),

    isPhoneDevice: function () {
      var osPlatform = this.get('model.data.deviceType');
      var model = this.get('model.data.deviceModel');
      return AmUserFormatter.isPhoneDevice(osPlatform, model);
    }.property('model.data.deviceType', 'model.data.deviceModel'),

    isAndroidDevice: function () {
      var osPlatform = this.get('model.data.deviceType');
      return AmUserFormatter.isAndroidDevice(osPlatform);
    }.property('model.data.deviceType'),

    isIOsDevice: function () {
      var osPlatform = this.get('model.data.deviceType');
      return AmUserFormatter.isIOsDevice(osPlatform);
    }.property('model.data.deviceType'),

    isAppleTvDevice: function () {
      var osPlatform = this.get('model.data.deviceType');
      var model = this.get('model.data.deviceModel');
      return AmUserFormatter.isAppleTvDevice(osPlatform, model);
    }.property('model.data.deviceType'),

    isMacDevice: function () {
      var osPlatform = this.get('model.data.deviceType');
      var isComputer = this.get('model.data.isComputer');
      return AmUserFormatter.isMacDevice(osPlatform, isComputer);
    }.property('model.data.deviceType', 'model.data.isComputer'),

    passcodePresentIconClass: function () {
      return (this.get('model.data.passcodePresent')) ? 'icon-locked' : 'icon-unlocked';
    }.property('model.data.passcodePresent'),

    isPasscodePresent: function () {
      return (this.get('model.data.passcodePresent')) ? Locale.renderGlobals('shared.true') : Locale.renderGlobals('shared.false');
    }.property('model.data.passcodePresent'),

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

    batteryLevelDate: function() {
      return Formatter.formatTimeLocal(this.get('model.data.batteryLevelDate'));
    }.property('model.data.batteryLevelDate'),

    platformText: function() {
      var deviceType = this.get('model.data.deviceType');
      var isTablet = this.get('model.data.isTablet');
      var model = this.get('model.data.deviceModel');

      return this.get('model.data.isComputer') ?  this.get('model.data.osPlatform') : AmUserFormatter.getOsText(deviceType, isTablet, model);
    }.property('model.data.deviceType', 'model.data.isTablet', 'model.data.deviceModel','model.data.osPlatform'),

    // Restrictions
    canIssueLockDeviceCommand: function () {
      var controller = this.get('deviceListController');
      var isComputer = this.get('model.data.isComputer');
      // Enable for Mac computer and Mobile devices
      return controller.isCommandEnabled('DeviceLock', this.get('model.data.isComputer')) && (this.get('isMacDevice') || !isComputer) ;
    }.property('model.data.deviceType'),

    canIssueRemoteEraseCommand: function () {
      var controller = this.get('deviceListController');
      var isComputer = this.get('model.data.isComputer');
      // Enable for Mac computer and Mobile devices
      return controller.isCommandEnabled('RemoteErase', this.get('model.data.isComputer')) && (this.get('isMacDevice') || !isComputer) ;
    }.property('model.data.deviceType'),

    canIssueResetTrackingCommand: function () {
      // TODO current version does not support this command
      return false;
    }.property('model.data.deviceType'),

    canIssueClearOrSetPasscodeCommand: function () {
      return (this.get('deviceListController')).isCommandEnabled('ClearPasscode', this.get('model.data.isComputer'));
    }.property('model.data.deviceType'),

    canIssueTrackDeviceCommand: function () {
      // TODO current version does not support this command
      return false;
    }.property('model.data.deviceType'),

    canIssueSendMessageCommand: function () {
      return (this.get('deviceListController')).isCommandEnabled('SendMessage', this.get('model.data.isComputer'));
    }.property('model.data.deviceType'),

    // Icons
    deviceIconPath: function () {
      var osPlatform = this.get('model.data.deviceType');
      var isTablet = this.get('model.data.isTablet');
      var model = this.get('model.data.deviceModel');

      return AmUserFormatter.getOsClass(osPlatform, isTablet, model);
    }.property('model.data.deviceType', 'model.data.isTablet', 'model.data.deviceModel'),

    osSmallIconPath: function () {
      var osPlatform = this.get('model.data.deviceType');

      return AmUserFormatter.getOsSmallIconPath(osPlatform);
    }.property('model.data.deviceType')
  });
});
