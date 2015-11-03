define([
  'ember',
  'packages/platform/data',
  '../specs/mobile_device_installed_application_spec'
], function(
  Em,
  AbsData,
  InstalledApplicationSpec
) {
  'use strict';

  return AbsData.get('Model').extend({
    Spec: InstalledApplicationSpec
  });
});
