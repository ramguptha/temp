define([
  'ember',
  'packages/platform/data',
  '../specs/smart_policy_spec'
], function(
  Em,
  AbsData,
  SmartPolicySpec
  ) {
  'use strict';

  return AbsData.get('Model').extend({
    Spec: SmartPolicySpec
  });
});
