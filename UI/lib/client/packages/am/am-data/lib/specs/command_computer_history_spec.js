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
    idNames: 'commandRecordId computerId'.w(),

    format: {
      commandRecordId: Format.Number,
      computerId: Format.ID,
      status: {
        labelResource: 'amData.commandComputerHistorySpec.status',
        labelIconClass: 'icon-settings-check',
        format: Format.IconLabel
      },
      statusNumber: { labelResource: 'amData.commandComputerHistorySpec.statusNumber', format: Format.Number },
      commandName: { labelResource: 'amData.commandComputerHistorySpec.commandName',  format: Format.StringOrNA },
      commandUUID: { labelResource: 'amData.commandComputerHistorySpec.commandUUID',  format: Format.StringOrNA },
      administratorName: { labelResource: 'amData.commandComputerHistorySpec.administratorName',  format: Format.StringOrNA },
//      computerEsn:  { labelResource: 'amData.commandComputerHistorySpec.computerEsn', format: Format.StringOrNA },
      agentName: { labelResource: 'amData.commandComputerHistorySpec.agentName', format: Format.StringOrNA },
      startTime: { labelResource: 'amData.commandComputerHistorySpec.startTime', format: Format.TimeLocal },
      commandError: { labelResource: 'amData.commandComputerHistorySpec.commandError', format: Format.MediumStringOrNA },
      commandErrorInfo: { labelResource: 'amData.commandComputerHistorySpec.commandErrorInfo', format: Format.LongStringOrNA }
    },

    resource: [
      {
        attr: 'computerId',
        guid: 'A0E856A5-5ACB-4F91-8FDF-8478AC7C6294',
        type: Number
      },
      {
        attr: 'status',
        guid: '1388CA0C-CD34-11D9-86E2-000D93B66ADA',
        type: String
      },
      {
        attr: 'statusNumber',
        guid: '97438994-33B9-4D9E-A67B-1833E9CCAFB3',
        type: Number
      },
      {
        attr: 'commandName',
        guid: 'FA033DA9-CD32-11D9-86E2-000D93B66ADA',
        type: String
      },
      {
        attr: 'commandRecordId',
        guid: 'C1A01091-8896-4603-8598-588968013B2E',
        type: Number
      },
      {
        attr: 'commandUUID',
        guid: '8C5902F5-B108-4117-8484-6DF7FF402BF5',
        type: String
      },
      {
        attr: 'itemMenu',
        // Command Type
        guid: '48B6ED7F-8A89-4685-A510-51142D88BF48',
        type: Number
      },
      {
        attr: 'administratorName',
        guid: '5E6B6AF1-CB76-40B8-A0D5-D92BEDA50FC8',
        type: String
      },
      {
        attr: 'agentName',
        guid: '5148916D-C9FF-11D9-83AD-000D93B66ADA',
        type: String
      },
      {
        attr: 'startTime',
        guid: '13878116-CD34-11D9-86E2-000D93B66ADA',
        type: Date
      },
      {
        attr: 'computerEsn',
        guid: '2B9A31CA-8578-4517-B804-325EAE4E3C5D',
        type: String
      },
      {
        attr: 'commandError',
        guid: '409FB47D-EFC3-44CA-919F-F571C69C5374',
        type: String
      },
      {
        attr: 'commandErrorInfo',
        guid: '1384F489-CD34-11D9-86E2-000D93B66ADA',
        type: String
      }
    ],

    mapRawResultSetData: function(query, rawData) {
      // Inject the context into the resultset.
      var result = this._super(query, rawData);
      result.forEach(function(raw) {
        raw.status = raw.statusNumber + '|' +  raw.status;
      });
      return result;
    },

    searchableNames: 'status commandName administratorName agentName startTime commandError commandErrorInfo'.w()
  }).create();
});
