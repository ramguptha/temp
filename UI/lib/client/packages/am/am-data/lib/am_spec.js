define([
  'ember',
  'packages/platform/data',
  './raw_data_mapping_mixin'
], function(
  Em,
  AbsData,
  RawDataMappingMixin
) {
  'use strict';

  // AmSpec
  // ============
  // An extension of Spec
  return AbsData.Spec.extend(RawDataMappingMixin);
});
