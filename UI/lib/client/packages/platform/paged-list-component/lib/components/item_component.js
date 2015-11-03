define([
  'ember'
], function(
  Em
) {
  'use strict';

  // Item View
  // =========

  return Em.Component.extend({
    layout: Em.Handlebars.compile('{{node.id}}'),

    // The Paged List Component
    pagedComponent: null,
    componentParentContext: Em.computed.oneWay('pagedComponent.parentView.context'),

    row: null,
    node: null,
    nodeData: function() {
      return this.get('node').nodeData;
    }.property('node'),

    refreshedAt: null,

    style: null
  });
});
