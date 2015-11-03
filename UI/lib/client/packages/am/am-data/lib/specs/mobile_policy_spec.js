define([
  'ember',
  '../am_spec',
  '../am_formats',

  'locale',
  'packages/platform/locale-config'
], function(
  Em,
  AmSpec,
  Format,

  Locale,
  LocaleConfig
  ) {
  'use strict';

  return AmSpec.extend({
    format: {
      id: Format.ID,
      guid: { labelResource: 'amData.mobilePolicySpec.guid', format: Format.Guid },
      name: { labelResource: 'amData.mobilePolicySpec.name', format: Format.LongString },
      isSmartPolicy: { labelResource: 'amData.mobilePolicySpec.isSmartPolicy', format: Format.Boolean},
      seed: Format.Number
    },

    resource: [
      {
        attr: 'id',
        guid: 'A78A37B9-86B7-4118-84C6-25A15C6F68C8',
        type: Number
      },
      {
        attr: 'guid',
        guid: 'E652CD7A-909C-465D-AE76-B97428711B6B',
        type: String
      },
      {
        attr: 'name',
        guid: '426FBD79-BE65-4FC0-A27F-BAC810C15C6E',
        type: String
      },
      {
        attr: 'isSmartPolicy',
        guid: 'F6917EE8-9F39-43F3-B8C9-D3C461170FE5',
        type: Boolean
      },
      {
        attr: 'seed',
        guid: '65A7E9DC-2026-4178-9950-B0E26A3A8B0A',
        type: Number
      },
      {
        attr: 'filterType',
        guid: '618ee8ca-6e1a-4c47-a761-c43f5b7f5e48',
        type: Number
      }
    ],

    mapRawResultSetData: function(query, rawData) {
      // Put JP translation for Unmanaged devices
      var result = this._super(query, rawData);
      result.forEach(function(raw) {
        if(raw.name === 'Unmanaged devices' && LocaleConfig.locale() !== 'en-us') {
          raw.name = raw.name + '|' + Locale.renderGlobals('amData.mobilePolicySpec.unmanagedDevices').toString();
        }
      });
      return result;
    },


    searchableNames: 'guid isSmartPolicy name'.w()
  }).create();
});
