define([
  'ember',
  'guid',
  'query',
  'logger',

  '../views/tree_view'
], function(
  Em,
  Guid,
  Query,
  logger,

  TreeView
) {
  'use strict';

  var TreeNode = Em.Object.extend({
    id: null,
    automationId: null,
    label: null,
    description: null,
    iconClasses: null,
    model: null,
    children: null,
    isSelectable: false
  });

  var TreeController = Em.Controller.extend({
    Search: Query.Search,
    TreeView: TreeView,
    TreeNode: TreeNode,

    lock: null,

    selectedItemId: null,
    scrollPosition: 0,

    // To control adding checkboxes to allow selection of multiple nodes
    isMultiSelect: false,
    selectionsList: function() { return Em.A() }.property(),

    // Drill down on the selected node if navigation is enabled
    isNavigationEnabled: false,

    query: function() {
      return this.Search.create();
    }.property(),
    searchAttr: Em.computed.alias('query.searchAttr'),
    searchFilter: Em.computed.alias('query.searchFilter'),
    hasSearchableColumnSpecs: false,

    searchableColumnNames: function() {
      return ['name'];
    }.property(),

   // Returns the structure of the tree in the format of an array of built nodes
    tree: function() { throw 'Implement me'; }.property(),

    init: function() {
      this.setProperties({
        lock: Guid.generate()
      });

      // Observers
      this.get('selectedItemId');
    },

    // Array representation of tree, via depth first walk.
    flattenedTree: function() {
      var nodes = [];

      var depthFirst = function(tree) {
        if (!Em.isEmpty(tree)) {
          tree.forEach(function(branch) {
            depthFirst(Em.get(branch, 'children'));
            nodes.push(branch);
          });
        }
      };

      depthFirst(this.get('tree'));

      return Em.A(nodes);
    }.property('tree'),

    buildNode: function(model, children) {
      return this.TreeNode.create({
        id: model.get('id'),
        automationId: model.get('automationId'),
        description: null,
        iconClasses: null,
        label: model.get('name'),
        model: model,
        children: children,
        isSelectable: true
      });
    },

    select: function(id, selected, node) {
      var selectionsList = this.get('selectionsList');

      if (selected) {
        if (selectionsList.findBy('id', id) || Em.isEmpty(id) || Em.isEmpty(node)) {
          return;
        }
        if (!this.get('isMultiSelect')) {
          while (selectionsList.length) {
            selectionsList.pop();
          }
        }
        selectionsList.pushObject(node);
      } else {
        selectionsList.removeObject(node);
      }

      logger.log('DESKTOP: TREE_CONTROLLER: select', selectionsList);
    },

    reset: function() {
      this.clearSelectionsList();
    },

    clearSelectionsList: function() {
      var selectionsList = this.get('selectionsList');
      if (!Em.isEmpty(selectionsList)) {
        selectionsList.clear();
      }
    },

    clearSearchQuery: function() {
      this.setProperties({
        searchFilter: null,
        searchAttr: null
      });
    }
  });

  return TreeController.reopenClass({
    TreeNode: TreeNode
  });
});
