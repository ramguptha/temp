define([
  'ember',
  'packages/platform/data',
  '../specs/mobile_device_administrator_spec'
], function(
  Em,
  AbsData,
  MobileDeviceAdministratorSpec
) {
  'use strict';

  return AbsData.get('Model').extend({
    Spec: MobileDeviceAdministratorSpec
  });
});
