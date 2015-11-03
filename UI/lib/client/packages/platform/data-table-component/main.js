define([
  'packages/platform/paged-table-component',
  './lib/data_field_config',
  './lib/components/value_component',
  './lib/components/aggregate_value_component'
], function(
  PagedTableComponent,
  DataFieldConfig,
  ValueComponent,
  AggregateValueComponent
) {
  'use strict';

  // Data Sourced Table
  // ==================
  //
  // The base paged-table component is fairly generic. This sub-class is more tightly bound to our locale and
  // data system.
  //
  // Bindable actions:
  //
  // - showAggregateIfAny(nodeData, presenter, name): show a modal with detail for an aggregate data cell.

  var DataTableComponent = PagedTableComponent.extend({
    AggregateValueComponent: AggregateValueComponent,

    DomIdMapper: PagedTableComponent.DomIdMapper.extend({
      getRowIdForNode: function(node) {
        var id = null;

        if (node.isRecord) {
          var model = node.nodeData;
          id = model.get('id');
        } else {
          id = node.id;
        }

        return id;
      },

      getRowAutomationIdForNode: function(node) {
        var automationId = null;

        if (node.isRecord) {
          var model = node.nodeData;
          automationId = model.get('automationId');
        } else {
          automationId = node.id;
        }

        return automationId;
      }
    }),
    FieldConfig: DataFieldConfig,

    actions: {
      showAggregateIfAny: function(nodeData, presenter, name) {
        this.sendAction('showAggregateIfAny', nodeData, presenter, name);
      }
    },

    valueComponent: 'data-table-value'
  });

  return DataTableComponent.reopenClass({
    appClasses: {
      DataTableComponent: DataTableComponent,
      DataTableValueComponent: ValueComponent,
      DataTableAggregateValueComponent: AggregateValueComponent
    },

    FieldConfig: DataFieldConfig,
    ValueComponent: ValueComponent,
    AggregateValueComponent: AggregateValueComponent
  });
});
