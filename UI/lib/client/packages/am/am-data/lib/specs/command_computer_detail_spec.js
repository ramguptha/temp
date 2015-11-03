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
    idNames: 'id'.w(),

    format: {
      id: Format.ID,
      description: Format.StringOrNA,
      lastModified: Format.StringOrNA,
      adminUUID: Format.StringOrNA,
      commandName: { label: 'Command Name', format: Format.StringOrNA },
      commandType: Format.Number,
      commandUUID: Format.StringOrNA,
      statusNumber: Format.Number,
      details: {
        // Original names are capital, do not rename!

        // Device Freeze command details
        DisplayMessage: Format.StringOrNA,
        LockAgentOptions: Format.BooleanOrNA,
        LockOrUnlockAgent: Format.BooleanOrNA,
        PreSetPassword: Format.StringOrNA,
        PasswordOption: Format.Number,

        // Data Delete command details
        HelpmgrItems: {
          Name: Format.StringOrNA
        },
        Reason: Format.Number,
        Comment: Format.StringOrNA,
        DataDeleteType: Format.Number
      }
    },

    resource: [
      {
        attr: 'id',
        guid: '6C3BB3FD-FFAB-4C75-A00C-385DD902C027',
        type: Number
      },
      {
        attr: 'description',
        guid: '57BC9226-CD33-11D9-86E2-000D93B66ADA',
        type: String
      },
      {
        attr: 'lastModified',
        guid: '7E097045-79C9-4CEE-80DE-6F7DCCD76CD8',
        type: Date
      },
      {
        attr: 'adminUUID',
        guid: 'DD23B640-636F-42C7-882B-5F6AC58076AD',
        type: String
      },
      {
        attr: 'commandUUID',
        guid: '8C5902F5-B108-4117-8484-6DF7FF402BF5',
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
        attr: 'commandType',
        guid: '48B6ED7F-8A89-4685-A510-51142D88BF48',
        type: Number
      },
      {
        attr: 'details',
        guid: '2F674321-46DE-4650-A660-AFD7E67CAD4F',
        type: String
      }
    ],

    searchableNames: 'command description lastModified adminUUID commandName details'.w()
  }).create();
});
