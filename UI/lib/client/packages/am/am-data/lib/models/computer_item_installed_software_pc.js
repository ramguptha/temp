define([
  'ember',
  'packages/platform/data',
  '../specs/computer_item_installed_software_pc_spec'
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
