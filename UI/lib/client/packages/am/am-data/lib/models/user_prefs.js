define([
  'ember',
  'packages/platform/data',
  '../specs/user_prefs_spec'
], function(
  Em,
  AbsData,
  UserPrefsSpec
) {
  'use strict';

  return AbsData.get('Model').extend({
      Spec: UserPrefsSpec
  });
});
