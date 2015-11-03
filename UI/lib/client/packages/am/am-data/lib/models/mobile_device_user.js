define([
  'ember',
  'packages/platform/data',
  '../specs/mobile_device_user_spec'
], function(
  Em,
  AbsData,
  MobileDeviceUserSpec
) {
  'use strict';

  return AbsData.get('Model').extend({
    Spec: MobileDeviceUserSpec
  });
});
