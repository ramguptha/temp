define([
  'ember',
  'packages/platform/data',
  '../specs/mobile_policy_from_content_spec'
], function(
  Em,
  AbsData,
  MobilePolicyFromContentSpec
) {
  'use strict';

  return AbsData.get('Model').extend({
    Spec: MobilePolicyFromContentSpec
  });
});

