define([
  'ember',
  'packages/platform/data',
  '../specs/custom_field_format_as_spec'
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
