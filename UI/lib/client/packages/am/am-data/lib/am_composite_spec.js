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

  // A CompositeSpec is a spec composed of other specs.
  return AbsData.CompositeSpec.extend(RawDataMappingMixin);
});
