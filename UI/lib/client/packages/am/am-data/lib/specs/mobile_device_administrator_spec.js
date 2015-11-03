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
      name: { labelResource: 'amData.administratorSpec.administratorName', format: Format.StringOrNA }
    },

    resource: [
      {
        attr: 'name',
        guid: '8DB8C146-CD33-11D9-86E2-000D93B66ADA',
        type: String
      }

    ]
  }).create();
});
