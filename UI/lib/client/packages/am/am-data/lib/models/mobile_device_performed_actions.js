define([
  'ember',
  'packages/platform/data',
  '../specs/mobile_device_performed_actions_spec'
], function(
  Em,
  AbsData,
  PerformedActionsSpec
) {
  'use strict';

  return AbsData.get('Model').extend({
      Spec: PerformedActionsSpec
  });
});
