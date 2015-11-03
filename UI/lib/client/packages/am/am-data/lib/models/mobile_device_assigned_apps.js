define([
  'ember',
  'packages/platform/data',
  '../specs/mobile_device_assigned_in_house_apps_spec'
], function (
  Em,
  AbsData,
  MobileDeviceAssInHouseAppsSpec
) {
  'use strict';

  return AbsData.get('Model').extend({
    Spec: MobileDeviceAssInHouseAppsSpec
  });
});
