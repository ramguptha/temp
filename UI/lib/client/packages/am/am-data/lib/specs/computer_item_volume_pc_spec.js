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

      // PC
      displayName: { labelResource: 'amData.computerVolumeSpec.displayName', format: Format.StringOrNA },
      driveLetter: { labelResource: 'amData.computerVolumeSpec.driveLetter', format: Format.StringOrNA },
      volumeSerialNumber: { labelResource: 'amData.computerVolumeSpec.volumeSerialNumber', format: Format.StringOrNA }
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
        attr: 'displayName',
        guid: 'B4865CC9-CAB5-11D9-B9B4-000D93B66ADA',
        type: String
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
        attr: 'driveLetter',
        guid: 'B47F1570-CAB5-11D9-B9B4-000D93B66ADA',
        type: String
      },
      {
        attr: 'volumeSerialNumber',
        guid: 'B4852621-CAB5-11D9-B9B4-000D93B66ADA',
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
      }
    ],

    searchableNames: 'volumeName size format displayName freeSpace freeSpacePercent driveLetter volumeSerialNumber bootVolume compressed'.w()
  }).create();
});
