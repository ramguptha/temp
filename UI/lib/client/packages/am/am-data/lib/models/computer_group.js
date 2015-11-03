define([
  'ember',
  'packages/platform/data',
  '../specs/computer_group_spec'
], function(
  Em,
  AbsData,
  ComputerGroupSpec
) {
  'use strict';

  return AbsData.get('Model').extend({
    Spec: ComputerGroupSpec
  });
});
