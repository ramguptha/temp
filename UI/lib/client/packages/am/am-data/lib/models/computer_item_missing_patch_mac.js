define([
  'ember',
  'packages/platform/data',
  '../specs/computer_item_missing_patch_mac_spec'
], function(
  Em,
  AbsData,
  Spec
) {
  'use strict';

  return AbsData.get('Model').extend({
    Spec: Spec
  });
});
