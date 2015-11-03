define([
  'ember',
  'help',
  'jquery',
  '../namespace',
  'desktop',
  'am-desktop',
  'am-data',

  './mobile_device_summary_list_controller',
  '../views/mobile_device_send_message_view'
], function(
  Em,
  Help,
  $,
  AmMobileDevice,
  Desktop,
  AmDesktop,
  AmData,

  MobileDeviceSummaryListController,
  MobileDeviceSendMessageView
) {
  'use strict';

  return AmDesktop.ModalActionController.extend({

    tHeaderOneDevice: 'amMobileDevice.modals.sendMessage.headingOneDevice'.tr('deviceName'),
    tHeaderManyDevices: 'amMobileDevice.modals.sendMessage.headingManyDevices'.tr(),
    tUnsupportedDevicesMessage: 'amMobileDevice.modals.sendMessage.unsupportedDevicesMessage'.tr('unsupportedDeviceCount', 'deviceCount'),
    tMaxMessageSize: 'amMobileDevice.modals.sendMessage.messageSizeNote'.tr('maxMessageSize'),

    devices: null,
    deviceName: null,

    headingIconClass: 'icon-message',
    addModalClass: "device-command-window",

    actionButtonLabel: 'amMobileDevice.modals.sendMessage.buttons.actionButtonLabel'.tr(),

    inProgressMsg: 'amMobileDevice.modals.sendMessage.inProgressMsg'.tr(),
    successMsg: 'amMobileDevice.modals.sendMessage.successMsg'.tr(),
    errorMsg: 'amMobileDevice.modals.sendMessage.errorMsg'.tr(),
    errorDetailsMsg: 'amMobileDevice.modals.sendMessage.errorDetailsMsg'.tr(),

    isActionBtnDisabled: true,

    confirmationView: MobileDeviceSendMessageView,

    mobileDeviceListController: Em.inject.controller('AmMobileDeviceGroupsShowGroup'),

    urlForHelp: null,

    initProperties: function()  {
      var devices = this.get('model');

      var deviceListCtrl = this.get('mobileDeviceListController');
      var unsupportedDeviceList = Em.A([]);
      var deviceIds = '';
      for (var i=0; i < devices.length; i++) {
        deviceIds += devices[i].get('id') + ', ';
        if (!deviceListCtrl.supportsIOSManagedCommands(devices[i])) {
          unsupportedDeviceList.pushObject(devices[i]);
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
        modalActionWindowClass: modalActionWindowClass,
        isActionBtnDisabled: true,
        message: null,
        devices: devices,
        urlForHelp: Help.uri(1007),

        unsupportedDevices: unsupportedDeviceList.length > 0,
        unsupportedDevicesMessage: unsupportedDevicesMessage,
        unsupportedDeviceListController: unsupportedDeviceListController
      });
    },

    heading: function () {
      return this.get('deviceOneCount') ? this.get('tHeaderOneDevice') : this.get('tHeaderManyDevices');
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

    maxMessageSize: function() {
      return 30000;
    }.property(),

    onMessageChanged: function(router, event) {
      // The send Message Action Button is only enabled if a message is entered
      var message = this.get('message');
      this.set('isActionBtnDisabled', Em.isEmpty(message));

      var maxMessageSize = this.get('maxMessageSize');

      if (!Em.isEmpty(message) && message.length > maxMessageSize) {
        this.set('actionWarning', this.get('tMaxMessageSize'));
        this.set('message', message.substring(0, maxMessageSize));
      }

    }.observes('message'),

    buildAction: function() {
      this.set('urlForHelp', null);
      var deviceListCtrl = this.get('mobileDeviceListController');
      return AmData.get('actions.AmMobileDeviceSendMessageAction').create({
        mobileDeviceIds: this.get('devices')
          .filter(function(device) { return deviceListCtrl.supportsIOSManagedCommands(device); })
          .mapBy('id'),
        message: this.get('message')
      });
    }
  });
});
