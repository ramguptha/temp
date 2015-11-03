define([
  'ember',
  './value_component',
  'text!../templates/aggregate_value.handlebars'
], function(
  Em,
  ValueComponent,
  template
) {
  'use strict';

  // Link to aggregate data modal
  return ValueComponent.extend({
    layout: Em.Handlebars.compile(template),

    items: function() {
      var name = this.get('presenter.name');
      return name ? this.get('nodeData.data.' + name) : null;
    }.property('nodeData.data', 'presenter.name'),

    // We disabled if there are no aggregates to show, or if the action for showing them is not bound
    disabled: function() {
      return (0 === this.get('items.length')) || !this.get('component.showAggregateIfAny');
    }.property('items.length', 'component.showAggregateIfAny')
  });
});
