define([
  'ember',
  'packages/platform/data',
  '../specs/mobile_policy_spec'
], function(
  Em,
  AbsData,
  MobilePolicySpec
) {
  'use strict';

  return AbsData.get('Model').extend({
    Spec: MobilePolicySpec
  });
});
