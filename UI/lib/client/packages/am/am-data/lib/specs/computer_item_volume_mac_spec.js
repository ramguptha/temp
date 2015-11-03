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
    idNames: 'volumeName volumeSerialNumber freeSpace'.w(),

    format: {
      id: Format.ID,
      volumeName: { labelResource: 'amData.computerVolumeSpec.volumeName', format: Format.StringOrNA },
      size: { labelResource: 'amData.computerVolumeSpec.size', format: Format.BytesOrNA },
      format: { labelResource: 'amData.computerVolumeSpec.format', format: Format.StringOrNA },
      freeSpace: { labelResource: 'amData.computerVolumeSpec.freeSpace', format: Format.Bytes },
      freeSpacePercent: { labelResource: 'amData.computerVolumeSpec.freeSpacePercent', format:  Format.Percent },
      bootVolume: { labelResource: 'amData.computerVolumeSpec.bootVolume', format: Format.BooleanOrNA },
      compressed: { labelResource: 'amData.computerVolumeSpec.compressed', format: Format.BooleanOrNA },

      // MAC
      volumeType: { labelResource: 'amData.computerVolumeSpec.volumeType', format: Format.StringOrNA },
      objectCount: { labelResource: 'amData.computerVolumeSpec.objectCount', format: Format.NumberOrNA },
      folderCount: { labelResource: 'amData.computerVolumeSpec.folderCount', format: Format.NumberOrNA },
      journaled: { labelResource: 'amData.computerVolumeSpec.journaled', format: Format.BooleanOrNA },
      lockedByHardware: { labelResource: 'amData.computerVolumeSpec.lockedByHardware', format: Format.BooleanOrNA },
      lockedBySoftware: { labelResource: 'amData.computerVolumeSpec.lockedBySoftware', format: Format.BooleanOrNA }
    },

    resource: [
      {
        attr: 'id',
        guid: 'A0E856A5-5ACB-4F91-8FDF-8478AC7C6294',
        type: Number
      },
      {
        attr: 'volumeName',
        guid: 'B4831FCC-CAB5-11D9-B9B4-000D93B66ADA',
        type: String
      },
      {
        attr: 'size',
        guid: 'B485AF0A-CAB5-11D9-B9B4-000D93B66ADA',
        type: Number
      },
      {
        attr: 'format',
        guid: 'B47F941C-CAB5-11D9-B9B4-000D93B66ADA',
        type: Number
      },
      {
        attr: 'freeSpace',
        guid: 'B4800711-CAB5-11D9-B9B4-000D93B66ADA',
        type: Number
      },
      {
        attr: 'freeSpacePercent',
        guid: 'B4807B4A-CAB5-11D9-B9B4-000D93B66ADA',
        type: String
      },
      {
        attr: 'bootVolume',
        guid: 'B47D15E1-CAB5-11D9-B9B4-000D93B66ADA',
        type: Boolean
      },
      {
        attr: 'compressed',
        guid: 'B47E1B7A-CAB5-11D9-B9B4-000D93B66ADA',
        type: Boolean
      },
      {
        attr: 'volumeType',
        guid: 'B4865CC9-CAB5-11D9-B9B4-000D93B66ADA',
        type: Number
      },
      {
        attr: 'objectCount',
        guid: 'B4839C10-CAB5-11D9-B9B4-000D93B66ADA',
        type: Number
      },
      {
        attr: 'folderCount',
        guid: 'B484216A-CAB5-11D9-B9B4-000D93B66ADA',
        type: Number
      },
      {
        attr: 'journaled',
        guid: 'B480F01A-CAB5-11D9-B9B4-000D93B66ADA',
        type: Boolean
      },
      {
        attr: 'lockedByHardware',
        guid: 'B4817538-CAB5-11D9-B9B4-000D93B66ADA',
        type: Boolean
      },
      {
        attr: 'lockedBySoftware',
        guid: 'B4820793-CAB5-11D9-B9B4-000D93B66ADA',
        type: Boolean
      }
    ],

    searchableNames: 'volumeName size format freeSpace freeSpacePercent bootVolume compressed volumeType objectCount folderCount journaled lockedByHardware lockedBySoftware'.w()
  }).create();
});
