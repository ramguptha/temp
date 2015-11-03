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
      uuid: { labelResource: 'amData.installedProvisioningProfileSpec.uuid', format: Format.StringOrNA },
      name: { labelResource: 'amData.installedProvisioningProfileSpec.name', format: Format.StringOrNA },
      expiry: { labelResource: 'amData.installedProvisioningProfileSpec.expiry', format: Format.TimeLocal }
    },

    resource: [
      {
        attr: 'id',
        guid: '1BD646BC-536B-47B4-9A53-C985351501B8',
        type: Number
      },
      {
        attr: 'uuid',
        guid: '589D9FDB-220C-43B5-8E28-5FF912A50AB8',
        type: String
      },
      {
        attr: 'name',
        guid: '72FFA4ED-6D4D-4B83-ABFF-45455160855C',
        type: String
      },
      {
        attr: 'expiry',
        guid: '27901C79-C923-464A-A96E-D73185FA7956',
        type: Date
      }
    ]
  }).create();
});
