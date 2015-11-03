define([
  'ember',
  'help',
  'jquery',
  'guid',
  '../namespace',
  'desktop',
  'am-desktop',
  'am-data',

  '../views/mobile_device_set_device_name_view'
], function (
  Em,
  Help,
  $,
  Guid,
  AmMobileDevice,
  Desktop,
  AmDesktop,
  AmData,

  MobileDeviceSetDeviceNameView
) {
  'use strict';

  return AmDesktop.ModalActionController.extend({
    heading: 'amMobileDevice.modals.setDeviceName.heading'.tr(),
    headingIconClass: 'icon-square-attention1',

    inProgressMsg: 'amMobileDevice.modals.setDeviceName.inProgressMsg'.tr(),
    successMsg: 'amMobileDevice.modals.setDeviceName.successMsg'.tr(),
    errorMsg: 'amMobileDevice.modals.setDeviceName.errorMsg'.tr(),
    errorDetailsMsg: 'amMobileDevice.modals.setDeviceName.errorDetailsMsg'.tr(),

    tErrorMsgUniqueDeviceName: 'amMobileDevice.modals.setDeviceName.errorMsgUniqueDeviceName'.tr(),

    confirmationView: MobileDeviceSetDeviceNameView,

    isActionBtnDisabled: true,
    paused: true,

    devices: null,
    deviceName: null,
    deviceId: null,

    urlForHelp: null,

    initProperties: function () {
      var devices = this.get('model');
      var device = devices[0];

      this.setProperties({
        devices: devices,
        deviceId: device.get('id'),
        deviceName: this.getSafeName(device.get('name').toString()),
        urlForHelp: Help.uri(1008)
      });
    },

    buildAction: function () {
      this.set('urlForHelp', null);
      return AmData.get('actions.AmMobileDeviceSetDeviceNameAction').create({
        deviceId: this.get('deviceId'),
        name: this.get('deviceName')
      });
    },

    checkNameUniqueness: function (deviceName, self) {
      var mobileDeviceStore = AmData.get('stores.mobileDeviceStore');

      mobileDeviceStore.acquire(Guid.generate(),
        {
          store: mobileDeviceStore, searchAttr: 'name', searchFilter: deviceName.trim(),
          context: { mobileDeviceListName: AmMobileDevice.get('groupStore').materializedObjects[0].data.endPointName }
        },
        function (datasource) {
          for (var i = 0; i < datasource.get('length') ; i++) {
            if (datasource.objectAt(i).get('data.name') === deviceName.trim()) {
              self.set('isActionBtnDisabled', true);
              self.set('errorMessage', self.get('tErrorMsgUniqueDeviceName'));

              return;
            }
          }
        }
      );
      self.set('isActionBtnDisabled', false);
      self.set('errorMessage', '');
    },

    onDeviceNameChanged: function () {
      var deviceName = this.get('deviceName');
      var device = this.get('devices')[0];

      if (this.getSafeName(device.get('name').toString()) === deviceName || '' === deviceName.trim()) {
        this.set('isActionBtnDisabled', true);
        this.set('errorMessage', '');
      } else {
        // Let's not spam the server with requests.
        // This should send a request not more often than once every second.
        if (!Em.isNone(window.deviceNameChangedTimeout)) {
          clearTimeout(window.deviceNameChangedTimeout);
        }

        window.deviceNameChangedTimeout = setTimeout(this.checkNameUniqueness, 500, deviceName, this);
      }
    }.observes('deviceName'),

    // Support names with apostrophe (&#x27) and other encoded symbols
    getSafeName: function(name) {
      return $('<textarea />').html(name).val();
    }


  });
});
