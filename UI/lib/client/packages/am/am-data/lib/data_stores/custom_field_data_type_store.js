define([
  'ember',
  'locale',
  'packages/platform/data',
  '../models/custom_field_data_type',
  '../specs/custom_field_data_type_spec'
], function(
  Em,
  Locale,
  AbsData,
  Model,
  Spec
  ) {
  'use strict';

  return AbsData.get('MockDataStore').extend({
    Model: Model,
    Spec: Spec,
    MockData: [
      { id: 1, dataTypeTitle: 'amData.customFieldsDataTypeStore.string' },
      { id: 2, dataTypeTitle: 'amData.customFieldsDataTypeStore.number' },
      { id: 3, dataTypeTitle: 'amData.customFieldsDataTypeStore.boolean' },
      { id: 4, dataTypeTitle: 'amData.customFieldsDataTypeStore.date' },
      { id: 5, dataTypeTitle: 'amData.customFieldsDataTypeStore.fileVersion' },
      { id: 6, dataTypeTitle: 'amData.customFieldsDataTypeStore.ipAddress' },
      { id: 7, dataTypeTitle: 'amData.customFieldsDataTypeStore.enumeration' }
    ].map(function(item){
        return {
          id: item.id,
          dataTypeTitle: function () {
            return Locale.renderGlobals(item.dataTypeTitle).toString();
          }.property(),
          endPointName: item.endPointName }
      })
  });
});
