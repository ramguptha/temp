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
    idNames: 'name'.w(),

    format: {
      name: { labelResource: 'amData.certificateSpec.name', format: Format.StringOrNA },
      isIdentity: { labelResource: 'amData.certificateSpec.isIdentity', format: Format.BooleanOrNA }
    },

    resource: [
      {
        attr: 'name',
        guid: 'D0F8C0AC-5080-4C75-A127-FC1C62EE8FC9',
        type: String
      },
      {
        attr: 'isIdentity',
        guid: '61786DBB-CBA3-4C37-837A-E2E203A1A0DF',
        type: Boolean
      }

    ]
  }).create();
});
