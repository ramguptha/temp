define([
  'packages/platform/paged-list-component'
], function(
  PagedListComponent
) {
  'use strict';

  // Data List Component
  // ===================
  //
  // A Paged List Component that is sourced by a DataPager.

  var ItemComponent = PagedListComponent.ItemComponent.extend({
    layout: Em.Handlebars.compile('{{node.nodeData.name}}')
  });

  var DataListComponent = PagedListComponent.extend({
    DomIdMapper: PagedListComponent.DomIdMapper.extend({
      getRowIdForNode: function(node) {
        var model = node.nodeData;
        return model.get('id');
      },

      getRowAutomationIdForNode: function(node) {
        var model = node.nodeData;
        return model.get('automationId');
      }
    }),

    itemComponent: 'data-list-item'
  });

  return DataListComponent.reopenClass({
    appClasses: {
      DataListComponent: DataListComponent,
      DataListItemComponent: ItemComponent
    },

    ItemComponent: ItemComponent
  });
});
