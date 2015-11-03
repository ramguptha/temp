define([
  'ember',
  'packages/platform/data',
  '../specs/third_party_application_spec'
], function(
  Em,
  AbsData,
  ThirdPartyApplicationSpec
) {
  'use strict';

  return AbsData.get('Model').extend({
    Spec: ThirdPartyApplicationSpec
  });
});
