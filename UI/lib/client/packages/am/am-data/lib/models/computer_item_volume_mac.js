define([
  'ember',
  'packages/platform/data',
  '../specs/computer_item_volume_mac_spec'
], function(
  Em,
  AbsData,
  ComputerVolumeSpec
) {
  'use strict';

  return AbsData.get('Model').extend({
    Spec: ComputerVolumeSpec
  });
});
