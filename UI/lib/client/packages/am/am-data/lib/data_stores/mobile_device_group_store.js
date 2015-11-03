define([
  'ember',
  'packages/platform/data',
  '../models/mobile_device_group',
  '../specs/mobile_device_group_spec',

  'locale'
], function(
  Em,
  AbsData,
  MobileDeviceGroup,
  MobileDeviceGroupSpec,

  Locale
) {
  'use strict';

  return AbsData.get('MockDataStore').extend({
    Model: MobileDeviceGroup,
    Spec: MobileDeviceGroupSpec,

    MockData: [
      { name: 'amData.mobileDeviceGroupStore.allMobileDevices', endPointName: 'all' },
      { name: 'amData.mobileDeviceGroupStore.allAndroidDevices', endPointName: 'allandroiddevices' },
      { name: 'amData.mobileDeviceGroupStore.allAndroidTablets', endPointName: 'allandroidtablets' },
      { name: 'amData.mobileDeviceGroupStore.allAndroidPhones', endPointName: 'allandroidphones' },
      { name: 'amData.mobileDeviceGroupStore.alliOSDevices', endPointName: 'alliosdevices' },
      { name: 'amData.mobileDeviceGroupStore.alliPhones', endPointName: 'alliphones' },
      { name: 'amData.mobileDeviceGroupStore.alliPads', endPointName: 'allipads' },
      { name: 'amData.mobileDeviceGroupStore.alliPodTouchDevices', endPointName: 'allipodtouchdevices' }//,
      //Commented out until the windows mobile devices are supported
      //{ name: 'amData.mobileDeviceGroupStore.allWindowsPhoneDevices', endPointName: 'allwindowsphonedevices' }
    ].map(function(item){
        return { name: function () {
          return Locale.renderGlobals(item.name).toString();
        }.property(), endPointName: item.endPointName }
      })
  });
});
