define([
  'ember',
  'packages/platform/data',
  '../am_formats'
], function(
  Em,
  AbsData,
  Format
) {
  'use strict';

  return AbsData.Spec.extend({

    format: {
      id: Format.ID,
      name:  { labelResource: 'amData.customFieldsEnumerationListStore.name', format: Format.LongString }
    },

    resource: [
      {
        attr: 'id',
        type: Number
      },
      {
        attr: 'name',
        type: String
      }
    ],

    names: 'id name'.w()

  }).create();
});
