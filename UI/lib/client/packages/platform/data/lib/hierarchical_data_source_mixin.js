define([
  'ember'
], function(
  Em
) {
  'use strict';

  return Em.Mixin.create({
    getParentId: function(obj) {
      return obj.get('parentId');
    },

    getParent: function(obj, byId) {
      return byId[this.getParentId(obj)];
    },

    getChildIds: function(obj) {
      return obj.get('childIds');
    },

    getChildren: function(obj, byId) {
      return this.getChildIds(obj).map(function(id) {
        return byId[id];
      }).filter(function(elt) {
        return !Em.isNone(elt);
      });
    },

    buildTree: function(data, parentId, nodeBuilderScope, nodeBuilder) {
      var self = this;
      var roots = [];
      var nonRoots = [];

      data.forEach(function(obj) {
        if (self.getParentId(obj) === parentId) {
          roots.push(obj);
        } else {
          nonRoots.push(obj);
        }
      });

      if (0 === roots.get('length')) {
        return null;
      }

      roots = self.get('query').performSort(roots);

      return roots.map(function(obj) {
        return nodeBuilder.call(nodeBuilderScope, obj, self.buildTree(nonRoots, obj.get('id'), nodeBuilderScope, nodeBuilder));
      });
    }
  });
});
