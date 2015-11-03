define([
  'ember'
], function(
  Em
) {
  'use strict';

  return Em.Component.extend({
    classNames: 'is-loading'.w(),
    layout: Em.Handlebars.compile('<p>{{tLoadingMore}}</p> <span class="spinner"></span>'),

    pagedComponent: null,
    tLoadingMore: Em.computed.oneWay('pagedComponent.tLoadingMore'),

    // Under normal cases, the LoadingComponent is also rendered when the component is in a paused state.
    // Because the paused state isn't linked to a particular node, node and nodeData will be unavailable under
    // such circumstances.
    node: null,
    nodeData: function() {
      return this.get('node').nodeData;
    }.property('node')
  });
});
