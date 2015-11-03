define([
  'ember',
  'packages/platform/data',
  '../specs/user_self_help_privileges_spec'
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
