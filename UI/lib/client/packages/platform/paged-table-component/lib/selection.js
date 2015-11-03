define([
  'ember'
], function(
  Em
) {
  'use strict';

  // Selection
  // =========
  //
  // Any and all things related to row selection.

  return Em.Object.extend({
    absTable: null,

    hasSelection: Em.computed.oneWay('absTable.hasSelection'),
    hasSingleSelection: Em.computed.oneWay('absTable.hasSingleSelection'),
    hasMultipleSelection: Em.computed.oneWay('absTable.hasMultipleSelection'),

    selectedIds: Em.computed.oneWay('absTable.selectedIds')
  });
});
