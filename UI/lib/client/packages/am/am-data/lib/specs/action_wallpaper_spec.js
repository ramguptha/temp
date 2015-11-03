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
      wallpaper: Format.String
    },

    resource: [
      {
        attr: 'wallpaper',
        guid: 'EE990E6-835D-4A04-8404-91EE2612DCDA',
        type: String
      }
    ],

    mapRawResultSetData: function(query, rawData) {
      // Inject the context into the result set.
      var result = this._super(query, rawData);

      result.forEach(function(raw) {
        var propertyList = raw.wallpaper;

        if (propertyList) {
          raw.wallpaper = "data:image/png;base64," + propertyList.WallpaperPicture;
        }
      });

      return result;
    }
  }).create();
});
