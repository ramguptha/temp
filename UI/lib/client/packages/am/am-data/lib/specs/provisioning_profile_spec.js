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
      name: { labelResource: 'amData.provisioningProfileSpec.name', format: Format.StringOrNA },
      expiry: { labelResource: 'amData.provisioningProfileSpec.expiry', format: Format.TimeLocal },
      uuid: { labelResource: 'amData.provisioningProfileSpec.uuid', format: Format.StringOrNA }
    },

    resource: [
      {
        attr: 'id',
        guid: '78782863-898D-4321-A622-E241DB81EE97',
        type: Number
      },
      {
        attr: 'name',
        guid: '843A6257-A254-42CF-BA7E-BDFAF5323774',
        type: String
      },
      {
        attr: 'expiry',
        guid: '790B88EA-9539-482A-B41C-3B1D9966B508',
        type: Date
      },
      {
        attr: 'uuid',
        guid: '5653BBDE-8465-41D4-A105-73CF4EB2E93C',
        type: String
      }
    ],

    searchableNames: 'name expiry uuid'.w()
  }).create();
});
