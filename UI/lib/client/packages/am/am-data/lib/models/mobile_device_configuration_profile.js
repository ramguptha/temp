define([
  'ember',
  'packages/platform/data',
  '../specs/configuration_profile_spec'
], function(
  Em,
  AbsData,
  MobileDeviceConfigProfileSpec
) {
  'use strict';

  return AbsData.get('Model').extend({
    Spec: MobileDeviceConfigProfileSpec
  });
});
