define([
  'ember',
  'packages/platform/data',
  '../specs/mobile_device_assigned_apps_spec'
], function(
  Em,
  AbsData,
  MobileDeviceAssAppsSpec
) {
  'use strict';

  return AbsData.get('Model').extend({
      Spec: MobileDeviceAssAppsSpec
  });
});
