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
      name: { labelResource: 'amData.assignableBookstoreBookSpec.name', format: Format.String },
      category: { labelResource: 'amData.assignableBookstoreBookSpec.category', format: Format.ShortString},
      shortDescription: { labelResource: 'amData.assignableBookstoreBookSpec.shortDescription', format: Format.LongString },
      icon: { labelResource: 'amData.assignableBookstoreBookSpec.icon', format: Format.SearchableIcon }
    },

    resource: [
      {
        attr: 'id',
        guid: 'A2481FC7-7D81-4AB3-8ADF-F7CE02B5FD9F',
        type: Number
      },
      {
        attr: 'name',
        guid: '26FB5889-902E-4118-AF96-691FCC85914C',
        type: String
      },
      {
        attr: 'category',
        guid: '74613CC3-15B6-4822-9A6C-28BDC17A2BF0',
        type: String
      },
      {
        attr: 'shortDescription',
        guid: '16F29C2C-AE38-4D85-B054-4DB79F87B819',
        type: String
      },
      {
        attr: 'icon',
        guid: 'XXX',
        type: String
      }
    ],

    searchableNames: 'icon name category shortDescription'.w()
  });
});
