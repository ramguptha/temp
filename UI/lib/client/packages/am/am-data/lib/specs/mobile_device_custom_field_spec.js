define([
  'ember',
  '../am_spec',
  '../am_formats',
  'formatter',
  'locale'
], function(
  Em,
  AmSpec,
  Format,
  Formatter,
  Locale
  ) {
  'use strict';

  var TYPE_NUMBER = 2,
    TYPE_BOOLEAN = 3,
    TYPE_DATE = 4;

  return AmSpec.extend({
    format: {
      id: Format.ID,
      information: { labelResource: 'amData.mobileDeviceCustomFieldStore.information', format: Format.StringOrNA },
      dataValue: { labelResource: 'amData.mobileDeviceCustomFieldStore.data', format: Format.MediumStringOrNA },
      dataType: { labelResource: 'amData.mobileDeviceCustomFieldStore.dataType', format: Format.ShortString },
      description: { labelResource: 'amData.mobileDeviceCustomFieldStore.description', format: Format.MediumString },
      dataTypeNumber: Format.Number
    },

    idNames: 'id dataValue'.w(),

    resource: [
      {
        attr: 'id',
        guid: '5ac69f27-881e-4e94-b3f6-ab123a363f9d',
        type: Number
      },
      {
        attr: 'information',
        guid: '7E629F1E-FF19-44EC-ABFA-96BEFBC34371',
        type: String
      },
      {
        attr: 'dataValue',
        guid: '9EF3BEE5-E4E3-4D11-86DB-40630AC47342',
        type: String
      },
      {
        attr: 'dataDateValue',
        guid: '75978CA1-DB51-4E65-922E-087EF097EB98',
        type: String
      },
      {
        attr: 'dataNumberValue',
        guid: '1A79F37B-5AA9-4FD8-A18D-B26DD2DC8040',
        type: Number
      },
      {
        attr: 'dataType',
        guid: 'fe373040-225e-4927-918d-21da979969f6',
        type: String
      },
      {
        attr: 'dataTypeNumber',
        guid: 'E41365CF-A1A6-4FBA-913E-122081ABC521',
        type: Number
      },
      {
        attr: 'displayType',
        guid: 'A1E55BB0-901A-4B59-AFE4-2645A4323F04',
        type: Number
      },
      {
        attr: 'description',
        guid: 'D04DAEDA-00DB-4280-B527-BF3055ACD701',
        type: String
      }
    ],

    mapRawResultSetData: function(query, rawData) {
      // Inject the context into the result set.
      var result = this._super(query, rawData);

      // special formatting rules
      result.forEach(function(item) {
        if(!Em.isNone(item.dataTypeNumber) && !Em.isNone(item.dataValue)) {
          switch(item.dataTypeNumber) {
            case TYPE_DATE:
              item.dataValue = Formatter.formatTimeLocal(new Date(item.dataDateValue));
              break;
            case TYPE_BOOLEAN:
              if(item.dataValue === 'Yes') {
                item.dataValue = Locale.renderGlobals('shared.true').toString();
              } else if(item.dataValue === 'No') {
                item.dataValue = Locale.renderGlobals('shared.false').toString();
              }
              break;
            case TYPE_NUMBER:
              // bytes
              if(item.displayType === 3) {
                // need to translate byte and bytes ( additional translations may be necessary for languages other than Japanese )
                if(item.dataValue.indexOf('bytes') !== -1){
                  item.dataValue = item.dataValue.replace('bytes', Locale.renderGlobals('shared.formatBytes.bytes').toString());
                } else if(item.dataValue.indexOf('byte') !== -1){
                  item.dataValue = item.dataValue.replace('byte', Locale.renderGlobals('shared.formatBytes.byte').toString());
                }
              }
              break;
          }
        }
      });

      return result;
    },

    searchableNames: 'information dataValue dataType description'.w()
  }).create();
});
