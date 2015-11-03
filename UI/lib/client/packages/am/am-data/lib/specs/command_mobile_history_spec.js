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
    idNames: 'commandRecordId deviceId'.w(),

    format: {
      commandRecordId: Format.Number,
      deviceId: Format.ID,
      command: { labelResource: 'amData.commandMobileHistorySpec.command', format: Format.StringOrNA },
      deviceName: { labelResource: 'amData.commandMobileHistorySpec.deviceName', format: Format.StringOrNA },
      deviceModel: { labelResource: 'amData.commandMobileHistorySpec.deviceModel', format: Format.StringOrNA },
      osVersion: { labelResource: 'amData.commandMobileHistorySpec.osVersion', format: Format.OSVersion },
      timeIssued: { labelResource: 'amData.commandMobileHistorySpec.timeIssued', format: Format.TimeLocal },
      timeFinished: { labelResource: 'amData.commandMobileHistorySpec.timeFinished', format: Format.TimeLocal },
      commandError: { labelResource: 'amData.commandMobileHistorySpec.commandError', format: Format.MediumStringOrUnknownError },
      commandErrorInfo: { labelResource: 'amData.commandMobileHistorySpec.commandErrorInfo', format: Format.LongStringOrNA }
    },

    resource: [
      {
        attr: 'commandRecordId',
        guid: '95718360-F6CE-4A2A-96D1-EC03B81C92CB',
        type: Number
      },
      {
        attr: 'command',
        guid: '1F4F6126-571F-45C4-A146-0DF36A851AF7',
        type: String
      },
      {
        attr: 'deviceId',
        guid: '39f3f074-b8a2-4df1-ac02-eb1f25f3f98e',
        type: Number
      },
      {
        attr: 'deviceName',
        guid: 'FE5A9F56-228C-4BDA-99EC-8666292CB5C1',
        type: String
      },
      {
        attr: 'deviceModel',
        guid: '61479324-9E16-46FD-85E5-68F9865A7D6D',
        type: String
      },
      {
        attr: 'osVersion',
        guid: '1A9B0255-4BB2-43DC-BDC6-ABB65339BFF0',
        type: String,
        presentationType: Format.OSVersion
      },
      {
        attr: 'timeIssued',
        guid: '621C7271-7F20-41BB-8ACC-54906673D050',
        type: Date
      },
      {
        attr: 'timeFinished',
        guid: '5C0EBF24-2E3D-4993-8F1B-F092D3CF5329',
        type: Date
      },
      {
        attr: 'commandError',
        guid: '99839BA4-99B0-408E-B60F-E2B5525B7466',
        type: String
      },
      {
        attr: 'commandErrorInfo',
        guid: 'C9CA3BBC-7625-464A-ADEB-7C671FA19521',
        type: String
      }
    ],

    searchableNames: 'command deviceName deviceModel osVersion timeIssued timeFinished commandError commandErrorInfo'.w()
  }).create();
});
