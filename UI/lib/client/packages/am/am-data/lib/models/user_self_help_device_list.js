define([
  'ember',
  'packages/platform/data',
  '../specs/user_self_help_device_list_spec'
], function(
  Em,
  AbsData,
  UserSelfHelpSpec
) {
  'use strict';

  return AbsData.get('Model').extend({
    Spec: UserSelfHelpSpec
  });
});
