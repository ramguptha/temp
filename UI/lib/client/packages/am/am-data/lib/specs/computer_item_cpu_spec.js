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
      computerEsn: { labelResource: 'amData.computerCpuSpec.computerEsn', format: Format.StringOrNA },
      processorType: { labelResource: 'amData.computerCpuSpec.processorType', format: Format.StringOrNA },
      processorVendor: { labelResource: 'amData.computerCpuSpec.processorVendor', format: Format.StringOrNA },
      processorSpeed: { labelResource: 'amData.computerCpuSpec.processorSpeed', format: Format.ClockSpeed },
      busSpeed: { labelResource: 'amData.computerCpuSpec.busSpeed', format: Format.ClockSpeed },
      activeCores: { labelResource: 'amData.computerCpuSpec.activeCores', format: Format.NumberOrNA },
      physicalCores: { labelResource: 'amData.computerCpuSpec.physicalCores', format: Format.NumberOrNA },
      processorL1DataCache: { labelResource: 'amData.computerCpuSpec.processorL1DataCache', format: Format.BytesOrNA },
      processorL1InformationCache: { labelResource: 'amData.computerCpuSpec.processorL1InformationCache', format: Format.BytesOrNA },
      processorL2DataCache: { labelResource: 'amData.computerCpuSpec.processorL2DataCache', format: Format.BytesOrNA },
      processorL2InstructionCache: { labelResource: 'amData.computerCpuSpec.processorL2InstructionCache', format: Format.BytesOrNA },
      processorL3Cache: { labelResource: 'amData.computerCpuSpec.processorL3Cache', format: Format.BytesOrNA },
      processorSupportsHyperthreading: { labelResource: 'amData.computerCpuSpec.processorSupportsHyperthreading', format: Format.BooleanOrNA },
      processorHyperthreadingEnabled: { labelResource: 'amData.computerCpuSpec.processorHyperthreadingEnabled', format: Format.BooleanOrNA },
      osPlatform: { labelResource: 'amData.computerCpuSpec.osPlatform', format: Format.ShortStringOrNA },
      osPlatformNumber: Format.Number,

      // PC only
      processorHasMMX: { labelResource: 'amData.computerCpuSpec.processorHasMMX', format: Format.BooleanOrNA },
      processorHas3dNow: { labelResource: 'amData.computerCpuSpec.processorHas3dNow', format: Format.BooleanOrNA },
      processorHasSSE: { labelResource: 'amData.computerCpuSpec.processorHasSSE', format: Format.BooleanOrNA },
      processorHasSSE2: { labelResource: 'amData.computerCpuSpec.processorHasSSE2', format: Format.BooleanOrNA },
      processorHasSSE3: { labelResource: 'amData.computerCpuSpec.processorHasSSE3', format: Format.BooleanOrNA },
      processorFamily: { labelResource: 'amData.computerCpuSpec.processorFamily', format: Format.NumberOrNA },
      processorModel: { labelResource: 'amData.computerCpuSpec.processorModel', format: Format.NumberOrNA },
      processorStepping: { labelResource: 'amData.computerCpuSpec.processorStepping', format: Format.NumberOrNA },

      // MAC only
      coresPerProcessor: { labelResource: 'amData.computerCpuSpec.coresPerProcessor', format: Format.NumberOrNA }
    },

    resource: [
      {
        attr: 'id',
        guid: 'A0E856A5-5ACB-4F91-8FDF-8478AC7C6294',
        type: Number
      },
      {
        attr: 'computerEsn',
        guid: '2B9A31CA-8578-4517-B804-325EAE4E3C5D',
        type: String
      },
      {
        attr: 'processorType',
        guid: 'A1738830-63B1-4B57-85B2-A58C7FDE3363',
        type: String
      },
      {
        attr: 'processorVendor',
        guid: 'D95397E3-6785-4B18-B3D6-83B66BC098B7',
        type: String
      },
      {
        attr: 'processorSpeed',
        guid: '7085F00C-CA08-11D9-B1B7-000D93B66ADA',
        type: Number
      },
      {
        attr: 'busSpeed',
        guid: '7085623E-CA08-11D9-B1B7-000D93B66ADA',
        type: Number
      },
      {
        attr: 'activeCores',
        guid: '246E4422-9150-4694-B16E-917AE4EE1419',
        type: Number
      },
      {
        attr: 'processorL1DataCache',
        guid: '70867508-CA08-11D9-B1B7-000D93B66ADA',
        type: Boolean
      },
      {
        attr: 'processorL1InformationCache',
        guid: '7086C2B2-CA08-11D9-B1B7-000D93B66ADA',
        type: Boolean
      },
      {
        attr: 'processorL2DataCache',
        guid: '708732BE-CA08-11D9-B1B7-000D93B66ADA',
        type: Boolean
      },
      {
        attr: 'processorL2InstructionCache',
        guid: '70879285-CA08-11D9-B1B7-000D93B66ADA',
        type: Boolean
      },
      {
        attr: 'processorL3Cache',
        guid: '7087E281-CA08-11D9-B1B7-000D93B66ADA',
        type: Boolean
      },
      {
        attr: 'processorSupportsHyperthreading',
        guid: '6A7B9583-1712-45C5-BD35-A9D0F59B76C4',
        type: Boolean
      },
      {
        attr: 'processorHyperthreadingEnabled',
        guid: 'C38B288E-D0D1-4786-9E8F-00681A75EF2F',
        type: Boolean
      },
      {
        attr: 'processorHasMMX',
        guid: 'FFD4D578-6A28-4863-BC56-F771321DB5D7',
        type: Boolean
      },
      {
        attr: 'processorHas3dNow',
        guid: '71C09B3E-1889-4E77-95E2-F46A18DA8D41',
        type: Boolean
      },
      {
        attr: 'processorHasSSE',
        guid: '0DFAD5C9-7380-4FEE-B056-8E60175A6457',
        type: Boolean
      },
      {
        attr: 'processorHasSSE2',
        guid: 'CEDEEC2F-7DE9-4DD3-BDE0-D2FCB359940C',
        type: Boolean
      },
      {
        attr: 'processorHasSSE3',
        guid: '1CBF267A-12A6-422F-A0E0-0189FA3B9FE2',
        type: Boolean
      },
      {
        attr: 'processorFamily',
        guid: '1FEA5014-7658-4128-ABF3-CE5D6066069A',
        type: Number
      },
      {
        attr: 'processorModel',
        guid: '32491D3E-40F0-4199-AA2D-B836EB4296A4',
        type: Number
      },
      {
        attr: 'processorStepping',
        guid: '66CC1ABE-41CA-4D82-A4CC-60929E4CE0E9',
        type: Number
      },
      {
        attr: 'physicalCores',
        guid: '7085A9F2-CA08-11D9-B1B7-000D93B66ADA',
        type: Number
      },
      {
        attr: 'coresPerProcessor',
        guid: '70FB39A9-BF12-4A7F-935E-94633A8D95CC',
        type: Number
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
      }
    ],

    searchableNames: 'computerEsn processorType processorVendor processorSpeed busSpeed activeCores processorL1DataCache processorL1InformationCache processorL2DataCache processorL2InstructionCache processorL3Cache processorSupportsHyperthreading processorHyperthreadingEnabled processorHasMMX processorHas3dNow processorHasSSE processorHasSSE2 processorHasSSE3 processorFamily processorModel processorStepping physicalCores coresPerProcessor osPlatform'.w()

  }).create();
});
