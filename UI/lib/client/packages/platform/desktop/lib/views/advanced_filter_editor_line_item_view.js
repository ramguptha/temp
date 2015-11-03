define([
  'ember',
  './searchable_select_view',
  'text!../templates/advanced_filter_line_item_editor.handlebars'
], function(
  Em,
  SearchableSelectView,
  template
  ) {
  'use strict';

  return Em.View.extend({
    Select: Em.Select,
    TextField: Em.TextField,

    defaultTemplate: Em.Handlebars.compile(template),
    tagName: 'span',

    ConditionSearchableSelect: SearchableSelectView.extend({
      tPlaceholder: 'desktop.advancedFilterComponent.conditionPrompt'.tr(),
      dropDownSearchClass: 'filter-condition-search-box'
    }),

    EnumSearchableSelect: SearchableSelectView.extend({
      tPlaceholder: 'desktop.advancedFilterComponent.valuePrompt'.tr(),
      dropDownSearchClass: 'filter-enum-search-box'
    })
  });
});
