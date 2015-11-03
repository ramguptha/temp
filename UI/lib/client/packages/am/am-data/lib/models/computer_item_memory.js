define([
  'ember',
  'packages/platform/data',
  '../specs/computer_item_memory_spec'
], function(
  Em,
  AbsData,
  ComputerMemorySpec
) {
  'use strict';

  return AbsData.get('Model').extend({
    Spec: ComputerMemorySpec
  });
});
