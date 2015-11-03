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

  return AmSpec.extend({
    format: {
      id: Format.ID,
      name: Format.StringOrNA
    },

    resource: [
      {
        // The VPP Account Id
        attr: 'id',
        guid: 'A711C1E5-5C35-4B38-BB58-375EF0BC4DBE',
        type: String
      },
      {
        // The VPP Account Name
        attr: 'name',
        guid: '7A0C30BE-3986-40BE-A023-77838F152B48',
        type: String
      },
      {
        // The VPP Account Unique Id
        attr: 'uuid',
        guid: 'BFFE290D-E9D5-441B-B4C4-2679F1D7E2C9',
        type: String
      }
    ]
  }).create();
});
