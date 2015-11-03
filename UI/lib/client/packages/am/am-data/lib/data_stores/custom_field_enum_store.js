define([
  'ember',
  'locale',
  'packages/platform/data',
  '../models/custom_field_enum',
  '../specs/custom_field_enum_spec'
], function(
  Em,
  Locale,
  AbsData,
  Model,
  Spec
  ) {
  'use strict';

  return AbsData.get('MockDataStore').extend({
    Model: Model,
    Spec: Spec,
    MockData: []
  });
});
