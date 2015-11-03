define([
  'ember'
], function(
  Em
) {
  'use strict';

  // Page Metrics
  // ============
  //
  // Measurements for virtual pages.

  return Em.Object.extend({
    virtualContainerMetrics: null,
    virtualPage: null,

    loadedRange: Em.computed.oneWay('virtualPage.loadedRange'),

    scrollTop: Em.computed.oneWay('virtualContainerMetrics.scrollTop'),

    offsetTop: function() {
      var loadedRange = this.get('loadedRange');

      return this.get('virtualContainerMetrics.rowHeight') * (loadedRange ? loadedRange.offset : 0);
    }.property('virtualContainerMetrics.rowHeight', 'loadedRange'),

    visibleRowCapacity: Em.computed.oneWay('virtualContainerMetrics.visibleRowCapacity'),
    visibleRowStart: Em.computed.oneWay('virtualContainerMetrics.visibleRowStart'),
    visibleRowEnd: Em.computed.oneWay('virtualContainerMetrics.visibleRowEnd'),
    visibleRangePadding: Em.computed.oneWay('virtualContainerMetrics.visibleRangePadding'),

    isScrollNearRangeStart: function() {
      var loadedRange = this.get('loadedRange');
      return this.get('visibleRowStart') < (loadedRange.offset + this.get('visibleRangePadding'));
    }.property('loadedRange', 'visibleRowStart', 'visibleRangePadding'),

    isScrollNearRangeEnd: function() {
      var loadedRange = this.get('loadedRange');
      return this.get('visibleRowEnd') > (loadedRange.offset + loadedRange.count - this.get('visibleRangePadding'));
    }.property('loadedRange', 'visibleRowEnd', 'visibleRangePadding')
  });
});
