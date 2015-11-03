define([
  'packages/platform/paged-table-component',
  'packages/platform/is-data-presenter'
], function(
  PagedTableComponent,
  IsDataPresenter
) {
  'use strict';

  // Data Column Config
  // ==================
  //
  // A specialization of PagedTableComponent.FieldConfig, for data tables.

  return PagedTableComponent.FieldConfig.extend(IsDataPresenter, {
    valueComponent: function() {
      return this.get('isOneToMany') ? 'data-table-aggregate-value' : null;
    }.property('isOneToMany'),

    labelIconClass: function() {
      var options = this.getFormatOptions(this.get('spec'), this.get('name'));
      return options && options.labelIconClass;
    }.property('spec', 'name')
  });
});
