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
      intervalPeriodTitle: Format.String
    },

    resource: [
      {
        attr: 'id',
        type: Number
      },
      {
        attr: 'intervalPeriodTitle',
        type: String
      }
    ],

    names: 'id intervalPeriodTitle'.w()

  }).create();
});
