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

  return AmSpec.create({
    format: {
      id: Format.ID,
      name: Format.LongString,
      ordering: Format.Number,
      route: Format.String
    },

    resource: [
      { attr: 'id' },
      { attr: 'name' },
      { attr: 'ordering' },
      { attr: 'route'}
    ],

    names: 'id name route'.w()
  });
});
