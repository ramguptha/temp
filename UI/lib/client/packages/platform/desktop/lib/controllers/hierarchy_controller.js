define([
  'ember',
  'guid',
  'packages/platform/child-controller'
], function(
  Em,
  Guid,
  ChildController
) {
  'use strict';

  var NodeController = Em.ObjectProxy.extend(ChildController, {
    hierarchyController: null,

    id: null,
    lock: null,
    dataSource: null,

    // parentNodeId: null,
    parentNode: null,

    content: Em.computed.oneWay('dataSource'),

    init: function() {
      var self = this;

      this.set('lock', Guid.generate());
      this.set('dataSource', this.get('hierarchyController').acquireNodeDataSource(this));

      // Kick off observation of parentNodeId
      this.get('parentNodeId');
    },

    release: function() {
      var parentNode = this.get('parentNode');
      if (!Em.isNone(parentNode)) {
        this.get('parentNode').release();
      }
      this.get('dataSource').release(this.get('lock')); 
    },

    hierarchy: function() {
      var parentHierarchy = this.get('parentNode.hierarchy');
      return Em.isNone(parentHierarchy) ? Em.A([this]) : Em.copy(parentHierarchy).concat(this);
    }.property('parentNode.hierarchy.[]'),

    releaseAndSetParentNode: function(parentNode) {
      var oldParentNode = this.get('parentNode');
      if (!Em.isNone(oldParentNode)) {
        oldParentNode.release();
      }
      this.set('parentNode', parentNode);
    },

    updateParentNodeOnParentNodeIdChange: function() {
      // Set or clear parentNode based on parentNodeId and hierarchyController
      // preference.
      var parentNodeId = this.get('parentNodeId');

      if (!Em.isNone(parentNodeId)) {
        var hierarchyController = this.get('hierarchyController');
        var parentNode = hierarchyController.createParentNode(this);
        if (!Em.isNone(parentNode)) {
          this.releaseAndSetParentNode(parentNode);
        }
      } else {
        this.releaseAndSetParentNode(null);
      }
    }.observes('parentNodeId').on('init')
  });

  var HierarchyController = NodeController.extend({
    NodeController: NodeController,

    // Override and do not call superclass init()
    init: function() {
      this.set('hierarchyController', this);

      // Handle initially set ParentNodeId
      this.updateParentNodeOnParentNodeIdChange();
    },

    count: function() {
      return this.get('hierarchy.length');
    }.property('hierarchy.[]'),

    hierarchy: Em.computed.oneWay('parentNode.hierarchy'),

    defaultNodeCreationSpec: function() { return { hierarchyController: this }; }.property(),

    createRootParentNode: function() { return this.createParentNode(this); },

    createParentNode: function(node) {
      throw 'Implement me';
    },

    acquireNodeDataSource: function(node) {
      throw 'Implement me';
    }
  });

  return HierarchyController.reopenClass({ NodeController: NodeController });
});
