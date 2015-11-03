define([
  'ember',
  'packages/platform/data',
  '../specs/command_computer_detail_spec'
], function(
  Em,
  AbsData,
  CommandDetailSpec
) {
  'use strict';

  return AbsData.get('Model').extend({
    Spec: CommandDetailSpec
  });
});
