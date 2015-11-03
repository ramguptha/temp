define([
  'ember'
], function(
  Em
) {
  'use strict';

  // Item Component
  // ==============
  //
  // A component rendered in the context of an item in the collection.

  return Em.Component.extend({
    pagedComponent: null,
    row: null,
    node: null,

    // Touch to force render to invalidate
    refreshedAt: null,

    // Convenience properties for sub-classes
    componentParentContext: Em.computed.oneWay('pagedComponent.parentView.context'),
    nodeData: function() {
      return this.get('node').nodeData;
    }.property('node')
  });
});
