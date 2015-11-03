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

    return AmSpec.extend({
        format: {
            Value: Format.String,
        },

        resource: [
          {
              attr: 'Value',
              guid: 'E652CD7A-909C-465D-AE76-B97428711222',
              type: String
          }
        ],
        // Override in order to pass the flat list of options
        mapRawSingletonData: function (query, rawData) {
            return {
                value: rawData,
                map: function (par) {
                    return rawData;
                }
            };
        }
    }).create();
});
