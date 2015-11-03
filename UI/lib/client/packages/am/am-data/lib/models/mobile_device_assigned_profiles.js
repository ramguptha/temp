define([
  'ember',
  'packages/platform/data',
  '../specs/mobile_device_assigned_profiles_spec'
], function(
  Em,
  AbsData,
  MobileDeviceAssProf
) {
  'use strict';

  return AbsData.get('Model').extend({
      Spec: MobileDeviceAssProf
  });
});
