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
      command: { labelResource: 'amData.commandMobileQueueSpec.command', format: Format.StringOrNA },
      deviceId: Format.ID,
      deviceName: { labelResource: 'amData.commandMobileQueueSpec.deviceName', format: Format.StringOrNA },
      deviceModel: { labelResource: 'amData.commandMobileQueueSpec.deviceModel', format: Format.StringOrNA },
      osVersion: { labelResource: 'amData.commandMobileQueueSpec.osVersion', format: Format.OSVersion },
      status: { labelResource: 'amData.commandMobileQueueSpec.status', format: Format.ShortStringOrNA },
      timeIssued: { labelResource: 'amData.commandMobileQueueSpec.timeIssued', format: Format.TimeLocal }
    },

    resource: [
      {
        attr: 'commandRecordId',
        guid: '1D258A42-7B3C-4D77-8CEC-41B0F8662F6C',
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
        attr: 'status',
        guid: 'C4FE7BF4-CBC0-4A26-8402-34423684C630',
        type: String
      },
      {
        attr: 'timeIssued',
        guid: '621C7271-7F20-41BB-8ACC-54906673D050',
        type: Date
      }
    ],

    searchableNames: 'command deviceName deviceModel osVersion status timeIssued'.w()
  }).create();
});
