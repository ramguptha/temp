define([
  'ember',
  'packages/platform/locale',
  'desktop',
  'text!./lib/templates/adhoc_search.handlebars'
], function(
  Em,
  Locale,
  Desktop,
  template
) {
  'use strict';

  // Ad-hoc Search
  // =============
  //
  // A clearable text field, bound to a query.
  //
  // Bindable properties:
  //
  // - nameOptions
  // - nameOptionsLabelPath
  // - nameOptionsValuePath
  // - searchQuery
  // - isSearchCaseSensitive
  // - valuePlaceholder
  //
  // Bindable resource paths:
  //
  // - rNamePlaceholder
  // - rValuePlaceholder

  var AdhocSearchComponent = Em.Component.extend({
    SearchableSelectView: Desktop.SearchableSelectView,

    layout: Em.Handlebars.compile(template),

    classNameBindings: ':is-adhoc-search nameOptions:has-searchable-attributes:no-searchable-attributes'.w(),

    searchQuery: null,

    nameOptions: Em.A(),
    nameOptionValuePath: 'content.name',
    nameOptionLabelPath: 'content.label',

    tSearch: 'shared.search.placeHolder'.tr(),
    tSearchCaseSensitive: 'shared.search.caseSensitivePlaceHolder'.tr(),

    isSearchCaseSensitive: false,

    defaultValuePlaceholder: function() {
      var placeHolder = '';

      if (this.get('isSearchCaseSensitive')) {
        placeHolder = this.get('tSearchCaseSensitive');
      } else {
        placeHolder = this.get('tSearch');
      }

      return placeHolder;
    }.property('isSearchCaseSensitive', 'tSearch', 'tSearchCaseSensitive'),

    valuePlaceholder: Em.computed.or('tValuePlaceholder', 'defaultValuePlaceholder'),

    showSelectionText: true,

    hasSearchableColumnSpecs: function() {
      return this.get('nameOptions.length') > 0;
    }.property('nameOptions.length'),

    nameOptionsWithPlaceholder: function() {
      var pathIndex = 'content.'.length;
      var valuePath = this.get('nameOptionValuePath').substring(pathIndex);
      var labelPath = this.get('nameOptionLabelPath').substring(pathIndex);

      var placeholderOption = {};
      placeholderOption[valuePath] = null;
      placeholderOption[labelPath] = this.get('tNamePlaceholder');

      var columns = this.get('nameOptions').slice(0);
      columns.unshift(placeholderOption);
      return columns;
    }.property('nameOptions.[]'),

    // Strings
    // -------

    rNamePlaceholder: 'shared.search.options.all',
    tNamePlaceholder: Locale.translated({ property: 'rNamePlaceholder' }),

    rValuePlaceholder: null,
    tValuePlaceholder: Locale.translated({ property: 'rValuePlaceholder' })
  });

  return AdhocSearchComponent.reopenClass({
    appClasses: {
      AdhocSearchComponent: AdhocSearchComponent
    }
  });
});
