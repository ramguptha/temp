define([
  'ember',
  '../am_spec',
  '../am_formats'
], function(
  Em,
  AmSpec,
  Format
  ) {
  'use strict';

  return AmSpec.extend({
    idNames: 'memorySlotName memorySize memorySpeed memoryType'.w(),

    format: {
      id: Format.ID,
      memorySlotName: { labelResource: 'amData.computerMemorySpec.memorySlotName', format: Format.StringOrNA },
      memorySize: { labelResource: 'amData.computerMemorySpec.memorySize', format: Format.BytesOrNA },
      memorySpeed: { labelResource: 'amData.computerMemorySpec.memorySpeed', format: Format.StringOrNA },
      memoryType: { labelResource: 'amData.computerMemorySpec.memoryType', format: Format.StringOrNA }
    },

    resource: [
      {
        attr: 'id',
        guid: 'A0E856A5-5ACB-4F91-8FDF-8478AC7C6294',
        type: Number
      },
      {
        attr: 'memorySlotName',
        guid: '86028706-CA14-11D9-AECD-000D93B66ADA',
        type: String
      },
      {
        attr: 'memorySize',
        guid: '8602DC44-CA14-11D9-AECD-000D93B66ADA',
        type: Number
      },
      {
        attr: 'memorySpeed',
        guid: '86033F8C-CA14-11D9-AECD-000D93B66ADA',
        type: String
      },
      {
        attr: 'memoryType',
        guid: '8603A0CA-CA14-11D9-AECD-000D93B66ADA',
        type: String
      }
    ],

    searchableNames: 'memorySlotName memorySize memorySpeed memoryType'.w()

  }).create();
});
