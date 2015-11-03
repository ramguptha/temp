define([
  'ember',
  'packages/platform/data',
  '../specs/custom_field_spec'
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
