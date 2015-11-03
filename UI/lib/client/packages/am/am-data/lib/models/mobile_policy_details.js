define([
  'ember',
  'packages/platform/data',
  '../specs/mobile_policy_details_spec'
], function(
  Em,
  AbsData,
  MobilePolicyDetailsSpec
) {
  'use strict';

  return AbsData.get('Model').extend({
    Spec: MobilePolicyDetailsSpec
  });
});
