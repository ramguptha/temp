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
        labelResource: 'amData.commandComputerQueueSpec.status',
        labelIconClass: 'icon-settings-check',
        format: Format.IconLabel
      },
      statusNumber: { labelResource: 'amData.commandComputerQueueSpec.statusNumber', format: Format.Number },
      commandName: { labelResource: 'amData.commandComputerQueueSpec.commandName',  format: Format.StringOrNA },
      commandUUID: { labelResource: 'amData.commandComputerQueueSpec.commandUUID',  format: Format.StringOrNA },
      administratorName: { labelResource: 'amData.commandComputerQueueSpec.administratorName',  format: Format.StringOrNA },
//      computerEsn:  { labelResource: 'amData.commandComputerQueueSpec.computerEsn', format: Format.StringOrNA },
      agentName: { labelResource: 'amData.commandComputerQueueSpec.agentName', format: Format.StringOrNA },
      scheduledTime: { labelResource: 'amData.commandComputerQueueSpec.scheduledTime', format: Format.TimeLocal }
    },

    resource: [
      {
        attr: 'computerId',
        guid: 'A0E856A5-5ACB-4F91-8FDF-8478AC7C6294',
        type: Number
      },
      {
        attr: 'commandName',
        guid: 'FA033DA9-CD32-11D9-86E2-000D93B66ADA',
        type: String
      },
      {
        attr: 'status',
        guid: 'BC27E38E-CD33-11D9-86E2-000D93B66ADA',
        type: String
      },
      {
        attr: 'statusNumber',
        guid: '22587D45-F027-467C-B8E7-6D9C015D6BC8',
        type: Number
      },
      {
        attr: 'commandRecordId',
        guid: '99D81A1A-7B99-4D9F-990F-6F8824383147',
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
        attr: 'computerEsn',
        guid: '2B9A31CA-8578-4517-B804-325EAE4E3C5D',
        type: String
      },
      {
        attr: 'agentName',
        guid: '5148916D-C9FF-11D9-83AD-000D93B66ADA',
        type: String
      },
      {
        attr: 'scheduledTime',
        guid: 'BC26890D-CD33-11D9-86E2-000D93B66ADA',
        type: Date
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

    searchableNames: 'status commandName administratorName agentName scheduledTime'.w()
  }).create();
});
