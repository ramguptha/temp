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
      name: { labelResource: 'amData.policyFromActionSpec.policyName', format: Format.StringOrNA },
      initialDelay: { labelResource: 'amData.policyFromActionSpec.initialDelay', format: Format.IntervalInMinutesHoursOrDays },
      repeatInterval: { labelResource: 'amData.policyFromActionSpec.repeatInterval', format: Format.IntervalInMinutesHoursOrDays },
      repeatCount: { labelResource: 'amData.policyFromActionSpec.repeatCount', format: Format.NumberOrNA }
    },

    resource: [
      {
        attr: 'id',
        guid: 'A78A37B9-86B7-4118-84C6-25A15C6F68C8',
        type: Number
      },
      {
        attr: 'name',
        guid: '426FBD79-BE65-4FC0-A27F-BAC810C15C6E',
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

    searchableNames: 'name initialDelay repeatInterval repeatCount'.w()
  }).create();
});
