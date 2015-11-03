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
      agentAvailability: {
        labelResource: 'amData.computerListSpec.agentAvailability',
        labelIconClass: 'icon-comp-availability',
        format: Format.IconLabel
      },
      agentName: { labelResource: 'amData.computerListSpec.agentName', format: Format.LongString },
      machineModel: { labelResource: 'amData.computerListSpec.machineModel', format: Format.StringOrNA },
      osPlatform: { labelResource: 'amData.computerListSpec.osPlatform', format: Format.ShortStringOrNA },
      osPlatformNumber: Format.Number,
      osVersion: { labelResource: 'amData.computerListSpec.osVersion', format: Format.OSVersionComputer },
      activeIpAddress: { labelResource: 'amData.computerListSpec.activeIpAddress', format: Format.IPv4Address },
      currentUserName:  { labelResource: 'amData.computerListSpec.currentUserName', format: Format.StringOrNA },
      agentSerialNumber: { labelResource: 'amData.computerListSpec.agentSerialNumber', format: Format.StringOrNA }
//      computerEsn:  { labelResource: 'amData.computerListSpec.computerEsn', format: Format.StringOrNA },
//      deviceFreezeStatus: { labelResource: 'amData.computerListSpec.deviceFreezeStatus', format: Format.StringOrNA }
    },

    resource: [
      {
        attr: 'id',
        guid: 'A0E856A5-5ACB-4F91-8FDF-8478AC7C6294',
        type: Number
      },
      {
        attr: 'agentAvailability',
        guid: '5EF53324-2D40-4547-B7BD-51DACC82CE9C',
        type: String
      },
      {
        attr: 'agentName',
        guid: '5148916D-C9FF-11D9-83AD-000D93B66ADA',
        type: String
      },
      {
        attr: 'machineModel',
        guid: 'CD19617B-CA0B-11D9-AC5B-000D93B66ADA',
        type: String
      },
      {
        attr: 'osPlatform',
        guid: '671D1208-CA16-11D9-839A-000D93B66ADA',
        type: String
      },
      {
        attr: 'osPlatformNumber',
        guid: '5A34E172-BD0F-4AD3-AB80-5C03F344843A',
        type: Number
      },
      {
        attr: 'osVersion',
        guid: '671D7D4A-CA16-11D9-839A-000D93B66ADA',
        type: String,
        presentationType: Format.OSVersion
      },
      {
        attr: 'activeIpAddress',
        guid: '80C4AD10-9D81-4991-8268-571277135E05',
        type: String
      },
      {
        attr: 'currentUserName',
        guid: '671839DC-CA16-11D9-839A-000D93B66ADA',
        type: String
      },
      // This columns is not being used
      //{
      //  attr: 'computerEsn',
      //  guid: '2B9A31CA-8578-4517-B804-325EAE4E3C5D',
      //  type: String
      //},
      {
        attr: 'agentSerialNumber',
        guid: '4DC29689-C9FF-11D9-83AD-000D93B66ADA',
        type: String
      },
      {
        attr: 'deviceFreezeStatus',
        guid: '8D285318-F26B-11E3-BEE9-F8B156E2AB92',
        type: String
      },
      {
        attr: 'deviceFreezeStatusNumber',
        guid: 'BD733C58-92F4-4B31-827E-63ABF694B72B',
        type: Number
      }
    ],

    mapRawResultSetData: function(query, rawData) {
      // Inject the context into the resultset.
      var result = this._super(query, rawData);
      result.forEach(function(raw) {
        raw.osPlatform = raw.osPlatformNumber + '|' +  raw.osPlatform;
      });
      return result;
    },

    searchableNames: 'agentAvailability agentName machineModel osPlatform osVersion activeIpAddress currentUserName'.w()

  }).create();
});
