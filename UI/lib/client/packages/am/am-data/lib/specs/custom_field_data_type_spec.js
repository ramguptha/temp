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
      dataTypeTitle: Format.String
    },

    resource: [
      {
        attr: 'id',
        type: Number
      },
      {
        attr: 'dataTypeTitle',
        type: String
      }
    ],

    names: 'id dataTypeTitle'.w()

  }).create();
});
