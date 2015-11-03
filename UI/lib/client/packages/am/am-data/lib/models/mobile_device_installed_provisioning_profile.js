define([
  'ember',
  'packages/platform/data',
  '../specs/mobile_device_installed_provisioning_profile_spec'
], function(
  Em,
  AbsData,
  InstalledProvProfileSpec
) {
  'use strict';

  return AbsData.get('Model').extend({
      Spec: InstalledProvProfileSpec
  });
});
