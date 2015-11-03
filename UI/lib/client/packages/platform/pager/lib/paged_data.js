define([
  'ember-core',
  'guid',
  './node_types'
], function(
  Em,
  Guid,
  NodeTypes
) {
  'use strict';

  // PagedData
  // =========

  return Em.Object.extend(Em.MutableEnumerable, {

    // We do NOT inject the NodeTypes dependencies, because their constructors are interdependant anyway, and so
    // can't easily be mocked.

    root: function() {
      return new NodeTypes.Root();
    }.property(),

    lastReadEnd: null,

    // Node Traversal
    // --------------

    // Walk a node tree in depth-last order.
    walk: function(start, visit) {
      var stop = visit(start);

      if (start.children) {
        for (var i = 0; !stop && i < start.children.length; i++) {
          stop = this.walk(start.children[i], visit);
        }
      }

      return stop;
    },

    // Walk nodes in depth-last order, skipping the root and children of Groups where isExpanded is not true.
    walkVisible: function(start, visit) {
      var stop = false;

      // Visit the current node, unless it's the root.
      if (!(start instanceof NodeTypes.Root)) {
        stop = visit(start);
      }

      // Visit child nodes, if there are any and the children are expanded.
      if (start.children && start.isExpanded) {
        for (var i = 0; !stop && i < start.children.length; i++) {
          stop = this.walkVisible(start.children[i], visit);
        }
      }

      return stop;
    },

    // Read / Write Methods
    // --------------------

    lookup: function(root, ids) {
      var resultSet = [];
      var unwrap = false;

      if (!Em.isNone(ids)) {
        if (!Em.isArray(ids)) {
          ids = [ids];
          unwrap = true;
        }

        var finder = function(visited) {
          if (-1 !== ids.indexOf(visited.id)) {
            resultSet.push(visited);
          }
        };

        this.walk(root, finder);
      }

      return unwrap ? resultSet[0] : resultSet;
    },

    // Read visible nodes, stopping at the first deferred encountered.
    read: function(root, lastReadEnd, offset, count) {
      var result = [];

      var reader = function(visited) {
        var stop = false;

        if (count <= 0) {
          stop = true;
        } else {
          stop = Boolean(visited.isDeferred && visited.offset > 0);

          if (offset > 0) {
            offset -= 1;
          } else {
            result.push(visited);
            count -= 1;
          }
        }

        return stop;
      };

      this.walkVisible(root, reader);

      return result;
    },

    reset: function(root, nodeData) {
      var oldChildren = root.children.slice(0);
      var unlink = function(nodes) {
        for (var i = 0; i < nodes.length; i++) {
          var node = nodes[i];
          if (node.children && node.children.length > 0) {
            unlink(node.children);
          }

          node.parentNode = null;
          if (!node.isDeferred) {
            Em.set(node, 'nodeData', null);
          }

          nodes.length = 0;
        }
      };
      unlink(oldChildren);

      root.children.length = 0;
      root.appendChild(new NodeTypes.Deferred(nodeData));

      return root;
    },

    getMetrics: function() {

      // The _readLength_ is the number of nodes available for reading (and hence display). Readable nodes
      // stop at the first Deferred node encountered in the expanded tree.
      var readLength = 0;

      // Similarly, _readTail_.
      var readTail = null;

      // Simulate reading - once false we are "finished".
      var readable = true;

      // The _scrollLength_ is the number of nodes the are visible in the expanded tree. This is used to drive
      // the sizing of the scrollable area in related views.
      var scrollLength = 0;

      // If true, there are no deferred nodes in the tree.
      var isFullyLoaded = true;

      this.walkVisible(this.get('root'), function(node) {
        if (readable) {
          readLength += 1;
          readTail = node;
        }

        scrollLength += 1;

        isFullyLoaded = isFullyLoaded && node.isLoaded;
        readable = readable && !(node.isDeferred && 0 !== node.offset);
      });

      return {
        readLength: readLength,
        readTail: readTail,
        scrollLength: scrollLength,
        isFullyLoaded: isFullyLoaded
      };
    },

    getReadLength: function() {
      return this.getMetrics().readLength;   
    },

    getReadTail: function() {
      return this.getMetrics().readTail;
    },

    getScrollLength: function() {
      return this.getMetrics().scrollLength;
    },

    getIsFullyLoaded: function() {
      return this.getMetrics().isFullyLoaded;
    }
  });
});
