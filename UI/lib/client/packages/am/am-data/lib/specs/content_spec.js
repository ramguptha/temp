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
    format: {
      id: Format.ID,
      name: { labelResource: 'amData.contentSpec.name', format: Format.LongString },
      description: { labelResource: 'amData.contentSpec.description', format: Format.Text },
      mediaFileName: { labelResource: 'amData.contentSpec.mediaFileName', format: Format.MediumString },
      type: { labelResource: 'amData.contentSpec.type', format: Format.SearchableIcon },
      icon: { format: Format.Icon },
      mediaCategory: { labelResource: 'amData.contentSpec.mediaCategory', format: Format.ShortString },
      modified: { labelResource: 'amData.contentSpec.modified', format: Format.TimeLocal },
      mediaFileSize: { labelResource: 'amData.contentSpec.mediaFileSize', format: Format.Bytes },
      wifiOnly: { labelResource: 'amData.contentSpec.wifiOnly', format: Format.Boolean },
      canLeaveAbsSafe: { labelResource: 'amData.contentSpec.canLeaveAbsSafe', format: Format.Boolean },
      canBeEmailed: { labelResource: 'amData.contentSpec.canBeEmailed', format: Format.Boolean },
      canBePrinted: { labelResource: 'amData.contentSpec.canBePrinted', format: Format.Boolean },
      hashedPassword: { labelResource: 'amData.contentSpec.hashedPassword', format: Format.HashedPassword },
      guid: Format.Guid
    },

    resource: [
      {
        attr: 'id',
        guid: '10413EEE-81C4-4AC7-9C7F-52581699FABB',
        type: Number
      },
      {
        attr: 'name',
        guid: '0AE86506-A5B1-43C7-9037-5BF40C15F18A',
        type: String
      },
      {
        attr: 'description',
        guid: 'AD76A533-712E-4221-9D61-98EEFFE0DD72',
        type: String
      },
      {
        attr: 'mediaFileName',
        guid: 'F775E8A9-DBC6-4873-B9C2-1DCA3AF4369A',
        type: String
      },
      {
        attr: 'icon',
        guid: 'F775E8A9-DBC6-4873-B9C2-1DCA3AF4369A',
        type: String
      },
      {
        attr: 'type',
        guid: '729FD2C9-FDBC-4B6A-96E0-0465CFCC602A',
        type: String
      },
      {
        attr: 'mediaCategory',
        guid: '859F46B9-3710-45B9-B914-109F4F95CC68',
        type: String
      },
      {
        attr: 'modified',
        guid: '410394C2-C903-4223-817C-8AF8125FC74F',
        type: Date
      },
      {
        attr: 'mediaFileSize',
        guid: 'A332932E-DD61-4D8A-BCC6-53CAF45B513E',
        type: Number,
        presentationType: Format.Bytes
      },
      {
        attr: 'wifiOnly',
        guid: '99D10C64-401E-4B93-8C11-E251ADFEC506',
        type: Boolean
      },
      {
        attr: 'canLeaveAbsSafe',
        guid: 'D17156FE-379D-45D3-8E41-A1D77EFEAFA0',
        type: Boolean
      },
      {
        attr: 'canBeEmailed',
        guid: '8C8D7E49-DAEA-47A1-8B91-DB19F6E27578',
        type: Boolean
      },
      {
        attr: 'canBePrinted',
        guid: '0D15DD58-8895-48DD-9C8D-861D2824506B',
        type: Boolean
      },
      {
        attr: 'hashedPassword',
        guid: '68f6bbc2-3b7d-42da-a622-9428b409ac7e',
        type: String
      },
      {
        attr: 'guid',
        guid: '706E2239-53A9-4FE4-B9D8-AC4EA7FF4ACC',
        type: String
      },
      {
        attr: 'seed',
        guid: 'FD185863-6592-4E17-B867-BD0A004C32CF',
        type: Number
      }
    ],

    mapRawResultSetData: function(query, rawData) {
      // Inject the context into the result set.
      var result = this._super(query, rawData);
      result.forEach(function(raw) {
        raw.type = raw.icon + '[****]' +  raw.type;
      });
      return result;
    },

    searchableNames: 'name mediaFileSize canLeaveAbsSafe canBeEmailed canBePrinted wifiOnly type mediaCategory mediaFileName modified hashedPassword'.w()
  }).create();
});
