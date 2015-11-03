define([
  'ember',
  'packages/platform/data',
  '../specs/mobile_device_group_spec'
], function(
  Em,
  AbsData,
  MobileDeviceGroupSpec
) {
  'use strict';

  return AbsData.get('Model').extend({
    Spec: MobileDeviceGroupSpec
  });
});
