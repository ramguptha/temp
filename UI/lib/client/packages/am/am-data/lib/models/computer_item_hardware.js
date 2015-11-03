define([
  'ember',
  'packages/platform/data',
  '../specs/computer_item_hardware_spec'
], function(
  Em,
  AbsData,
  ComputerHardwareSpec
) {
  'use strict';

  return AbsData.get('Model').extend({
    Spec: ComputerHardwareSpec
  });
});
