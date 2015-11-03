define([
  'ember',
  'packages/platform/paged-component'
], function(
  Em,
  PagedComponent
) {
  'use strict';

  // Table Metrics
  // =============
  //
  // Encapsulates DOM and virtual measurements for the AbsTable component.

  return PagedComponent.VirtualContainerMetrics.extend({

    // DOM Metrics
    // -----------

    defaultColumnWidth: Em.computed.oneWay('component.defaultColumnWidth'),
    controlColumnWidth: Em.computed.oneWay('component.controlColumnWidth'),

    // Set on didInsertElement()
    scrollbarWidth: 0,
    scrollbarHeight: 0,
    bodyClientHeight: 0,
    bodyClientWidth: 0,

    // Base Class Overrides
    // --------------------

    containerClientHeight: Em.computed.alias('bodyClientHeight')
  });
});
