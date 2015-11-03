define([
  'ember',
  'packages/platform/data',
  '../specs/mobile_device_installed_config_profile_spec'
], function(
  Em,
  AbsData,
  InstalledConfigProfileSpec
) {
  'use strict';

  return AbsData.get('Model').extend({
    Spec: InstalledConfigProfileSpec
  });
});
