define([
  'ember',
  '../am_spec',
  '../am_formats'
], function (
  Em,
  AmSpec,
  Format
  ) {
  'use strict';

  return AmSpec.create({
    format: {
      id: Format.ID,
      name: { labelResource: 'amData.actionsSpec.name', format: Format.String },
      description: { labelResource: 'amData.actionsSpec.description', format: Format.LongString},
      osPlatform: { labelResource: 'amData.actionsSpec.osPlatform', format: Format.ShortString },
      type: { labelResource: 'amData.actionsSpec.type', format: Format.ShortString },
      initialDelay: { labelResource: 'amData.policyFromActionSpec.initialDelay', format: Format.IntervalInMinutesHoursOrDays },
      repeatInterval: { labelResource: 'amData.policyFromActionSpec.repeatInterval', format: Format.IntervalInMinutesHoursOrDays },
      repeatCount: { labelResource: 'amData.policyFromActionSpec.repeatCount', format: Format.NumberOrNA }
    },

    resource: [
      {
        attr: 'id',
        guid: '9F4DA532-8D7B-45EB-B44A-38986A014CC5',
        type: Number
      },
      {
        attr: 'uuid',
        guid: '97347DCA-E4E0-4E4A-851F-ABDC5F50C3C7',
        type: String
      },
      {
        attr: 'seed',
        guid: 'BC7F6947-C1F0-4F44-8369-27FC47DEB302',
        type: Number
      },
      {
        attr: 'name',
        guid: '79285092-767A-4868-BEFA-5E4E84D6C97D',
        type: String
      },
      {
        attr: 'description',
        guid: '50A21BF5-A629-471D-9842-14702ED0039F',
        type: String
      },
      {
        attr: 'osPlatform',
        guid: '35A57B5B-D027-49E4-B39E-A729236BCFDD',
        type: String
      },
      {
        attr: 'type',
        guid: 'F6DC6AE5-4416-493A-97BC-95878906CF18',
        type: String
      },
      {
        attr: 'initialDelay',
        guid: '8EB5E664-0E3C-45CB-A0FC-FDFDDFE55B68',
        type: Number
      },
      {
        attr: 'repeatInterval',
        guid: 'DEBC16B7-B0EB-4D46-B6E0-4422768C0F80',
        type: Number
      },
      {
        attr: 'repeatCount',
        guid: 'AD69AAA9-5B05-41F2-A242-B2DFD7E399B5',
        type: Number
      }
    ],

    searchableNames: 'name description osPlatform type initialDelay repeatInterval repeatCount'.w()
  });
});
