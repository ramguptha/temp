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
      locationAccuracyTitle: Format.String
    },

    resource: [
      {
        attr: 'id',
        type: Number
      },
      {
        attr: 'locationAccuracyTitle',
        type: String
      }
    ],

    names: 'id locationAccuracyTitle'.w()

  }).create();
});
