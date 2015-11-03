define([
  'ember',
  'packages/platform/data',
  '../specs/command_computer_history_spec'
], function(
  Em,
  AbsData,
  CommandHistorySpec
) {
  'use strict';

  return AbsData.get('Model').extend({
    Spec: CommandHistorySpec
  });
});
