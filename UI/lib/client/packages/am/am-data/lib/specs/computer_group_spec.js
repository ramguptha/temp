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
    // Need to pick a group to land on? It is this one.
    DEFAULT_ID: 1,
    DEFAULT_NAME: 'all',

    format: {
      id: Format.ID,
      name: Format.LongString,
      endPointName: Format.String
    },

    resource: [
      { attr: 'id' },
      { attr: 'name' },
      { attr: 'endPointName' }
    ],

    names: 'id name endPointName'.w()
  }).create();
});
