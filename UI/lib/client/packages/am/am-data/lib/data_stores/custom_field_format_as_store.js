define([
  'ember',
  'locale',
  'packages/platform/data',
  '../models/custom_field_format_as',
  '../specs/custom_field_format_as_spec'
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
      { id: 1, title: 'amData.customFieldsFormatAsStore.decimal' },
      { id: 2, title: 'amData.customFieldsFormatAsStore.decimalNoSeparators' },
      { id: 3, title: 'amData.customFieldsFormatAsStore.bytes' }
    ].map(function(item){
        return {
          id: item.id,
          title: function () {
            return Locale.renderGlobals(item.title).toString();
          }.property(),
          endPointName: item.endPointName }
      })
  });
});
