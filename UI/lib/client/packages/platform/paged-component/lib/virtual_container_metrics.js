define([
  'ember'
], function(
  Em
) {
  'use strict';

  // Virtual Page Container Metrics
  // ==============================
  //
  // The DOM and virtual measurements.

  return Em.Object.extend({

    component: null,

    // DOM Metrics
    // -----------

    // The height of a single row of content, in pixels. _All rows are expected to have the same height._
    rowHeight: Em.computed.oneWay('component.rowHeight'),

    // The height of the container for virtually paged content, in pixels.
    containerClientHeight: 0,

    scrollTop: 0,
    scrollLeft: 0,

    // Virtual Metrics
    // ---------------

    virtualRows: 0,

    virtualHeight: function() {
      return this.get('virtualRows') * this.get('rowHeight');
    }.property('virtualRows', 'rowHeight'),

    visibleRowCapacity: function() {
      return Math.ceil(this.get('containerClientHeight') / this.get('rowHeight'));
    }.property('containerClientHeight', 'rowHeight'),

    visibleRowStart: function() {
      return Math.floor(this.get('scrollTop') / this.get('rowHeight'));
    }.property('scrollTop', 'rowHeight'),

    visibleRowEnd: function() {
      return this.get('visibleRowStart') + this.get('visibleRowCapacity');
    }.property('visibleRowStart', 'visibleRowCapacity'),

    visibleRange: function() {
      var visibleRowStart = this.get('visibleRowStart');

      return {
        offset: visibleRowStart,
        count: this.get('visibleRowEnd') - visibleRowStart
      };
    }.property('visibleRowStart', 'visibleRowEnd'),

    // The number of rows to render around the visible range, so that there is room for smooth scrolling
    visibleRangePadding: 0,

    paddedVisibleRange: function() {
      var visibleRange = this.get('visibleRange');
      var padding = this.get('visibleRangePadding');

      var offset = Math.max(0, visibleRange.offset - padding);

      return {
        offset: offset,
        count: (visibleRange.offset + visibleRange.count + padding) - offset
      };
    }.property('visibleRange', 'visibleRanglePadding'),

    virtualPageSize: function() {
      return this.get('visibleRange.count') + (2 * this.get('visibleRangePadding'));
    }.property('visibleRange', 'visibleRangePadding')
  });
});
