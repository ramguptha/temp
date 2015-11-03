define([
  'ember',
  'packages/platform/data',
  '../specs/computer_item_agent_information_spec'
], function(
  Em,
  AbsData,
  ComputerAgentInformationSpec
) {
  'use strict';

  return AbsData.get('Model').extend({
    Spec: ComputerAgentInformationSpec
  });
});
