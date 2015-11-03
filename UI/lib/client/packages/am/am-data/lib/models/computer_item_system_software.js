define([
  'ember',
  'packages/platform/data',
  '../specs/computer_item_system_software_spec'
], function(
  Em,
  AbsData,
  ComputerSystemSoftwareSpec
) {
  'use strict';

  return AbsData.get('Model').extend({
    Spec: ComputerSystemSoftwareSpec
  });
});
