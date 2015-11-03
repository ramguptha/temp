define([
  'ember',
  'packages/platform/data',
  '../specs/computer_item_installed_profile_spec'
], function(
  Em,
  AbsData,
  ComputerInstalledProfileSpec
) {
  'use strict';

  return AbsData.get('Model').extend({
    Spec: ComputerInstalledProfileSpec
  });
});
