define([
  'ember',
  './item_component'
], function(
  Em,
  ItemComponent
) {
  'use strict';

  // Value Component
  // ===============
  //
  // Encapsulates presentation for a data value.

  return ItemComponent.extend({
    tagName: 'span',
    attributeBindings: 'style name:data-name'.w(),
    classNames: 'is-value'.w(),
    layout: Em.Handlebars.compile('{{value}}'),

    presenter: null,

    style: new Em.Handlebars.SafeString(),
    name: null,

    value: function() {
      var node = this.get('node');
      var presenter = this.get('presenter');

      return (!presenter || node.isDeferred) ? '' : presenter.renderValue(node.nodeData)
    }.property('presenter', 'node', 'refreshedAt')
  });
});
