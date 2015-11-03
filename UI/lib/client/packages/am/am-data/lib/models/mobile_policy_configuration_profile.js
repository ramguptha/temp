define([
  'ember',
  'packages/platform/data',
  '../specs/configuration_profile_from_policy_spec'
], function (
  Em,
  AbsData,
  MobilePolicyConfigProfileSpec
) {
  'use strict';

  return AbsData.get('Model').extend({
    Spec: MobilePolicyConfigProfileSpec
  });
});
