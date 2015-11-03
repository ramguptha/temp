define([
  'ember',
  'packages/platform/data',
  '../specs/provisioning_profile_spec'
], function(
  Em,
  AbsData,
  MobileDeviceProvProfileSpec
) {
  'use strict';

  return AbsData.get('Model').extend({
      Spec: MobileDeviceProvProfileSpec
  });
});
