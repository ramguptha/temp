define([
  'ember',
  'packages/platform/data',
  '../specs/mobile_device_spec'
], function(
  Em,
  AbsData,
  MobileDeviceSpec
) {
  'use strict';

  return AbsData.get('Model').extend({
    Spec: MobileDeviceSpec
  });
});
