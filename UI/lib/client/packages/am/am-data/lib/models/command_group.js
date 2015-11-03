define([
  'ember',
  'packages/platform/data',
  '../specs/command_group_spec'
], function(
  Em,
  AbsData,
  MobileDeviceCommandGroupSpec
) {
  'use strict';

  return AbsData.get('Model').extend({
    Spec: MobileDeviceCommandGroupSpec
  });
});
