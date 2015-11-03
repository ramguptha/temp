define([
  'ember',
  'packages/platform/data',
  '../specs/computer_list_spec'
], function(
  Em,
  AbsData,
  ComputerSpec
) {
  'use strict';

  return AbsData.get('Model').extend({
    Spec: ComputerSpec
  });
});
