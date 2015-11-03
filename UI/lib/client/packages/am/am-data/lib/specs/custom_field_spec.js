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
    format: {
      id: Format.ID,
      name: { labelResource: 'amData.customFieldsStore.name', format: Format.LongString },
      description: { labelResource: 'amData.customFieldsStore.description', format: Format.LongString },
      dataType: { labelResource: 'amData.customFieldsStore.dataType', format: Format.LongString },
      dataTypeNumber: Format.Number,
      // In case of type: Number, there is the additional enumeration in the data
      displayTypeNumber: Format.Number,
      variableName: Format.LongString,
      enumerationList: Format.LongString
    },

    resource: [
      {
        attr: 'id',
        guid: '5ac69f27-881e-4e94-b3f6-ab123a363f9d',
        type: Number
      },
      {
        attr: 'name',
        guid: '7E629F1E-FF19-44EC-ABFA-96BEFBC34371',
        type: String
      },
      {
        attr: 'description',
        guid: 'D04DAEDA-00DB-4280-B527-BF3055ACD701',
        type: String
      },
      {
        // The data type of a custom field item.
        attr: 'dataType',
        guid: 'fe373040-225e-4927-918d-21da979969f6',
        type: String
      },
      {
        // The data type of a custom field item (non-enumerated).
        attr: 'dataTypeNumber',
        guid: 'E41365CF-A1A6-4FBA-913E-122081ABC521',
        type: Number
      },
      {
        // The display type of a custom field for the number type (non-enumerated).
        attr: 'displayTypeNumber',
        guid: 'A1E55BB0-901A-4B59-AFE4-2645A4323F04',
        type: Number
      },
      {
        // The variable name associated with a custom field item.
        attr: 'variableName',
        guid: '1f3b77c7-0cba-4ec1-808f-e27d8d6cda2d',
        type: String
      },
      {
        // The enumeration list of a custom field item.
        attr: 'enumerationList',
        guid: '659a110c-06fc-4606-9140-391e9d188009',
        type: String
      }
    ],

    searchableNames: 'name description dataType'.w()
  }).create();
});
