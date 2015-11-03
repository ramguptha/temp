define([
  'jquery',
  'ember',
  './hierarchy_controller',
  '../views/breadcrumbs_view',
  './advanced_filter_controller'
], function(
  $,
  Em,
  HierarchyController,
  BreadcrumbsView,
  AdvancedFilterController
) {
  'use strict';

  var FilterNodeController = HierarchyController.NodeController.extend({
    advancedFilterController: null,

    title: null,
    filter: null,
    deviceGroupType: null,
    deviceCount: null,
    isFixedGroup: function() {
      return this.get('deviceGroupType') === 'Fixed';
    }.property('deviceGroupType'),

    init: function() {
      this._super();
      this.set('advancedFilterController', AdvancedFilterController.create({
        parentController: this,
        filterBinding: 'parentController.searchFilter',
        dataStoreSpecBinding: 'parentController.hierarchyController.parentController.dataStore.Spec'
      }));
    }
  });

  var FilterHierarchyController = HierarchyController.extend({
    NodeController: FilterNodeController,
    BreadcrumbsView: BreadcrumbsView,

    dataStoreSpec: null
  });

  return FilterHierarchyController.reopenClass({ NodeController: FilterNodeController });
});
