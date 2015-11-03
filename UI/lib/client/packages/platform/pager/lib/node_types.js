define([
  'guid'
], function(
  Guid
) {
  'use strict';

  // Node Types
  // ==========

  // Node
  // ----

  var Node = function(id, nodeData) {
    this.id = id;
    this.nodeData = nodeData;
  };

  Node.prototype = {
    isNode: true,

    id: null,
    parentNode: null,
    depth: 0,
    offset: 0,
    nodeData: null,
    isLoaded: false,

    validate: function() {
      return this.parentNode && this.parentNode.children && (this.parentNode.children[this.offset] === this);
    },

    getDeferred: function() {
      return this;
    }
  };

  // Deferred
  // --------
  //
  // A Deferred node is a placeholder for content that hasn't been loaded yet.

  var Deferred = function(nodeData) {
    Node.call(this, Guid.generate(), nodeData);
  };

  Deferred.prototype = new Node();
  Deferred.prototype.isDeferred = true;

  // Record
  // ------

  var Record = function(id, nodeData) {
    Node.apply(this, arguments);
  };

  Record.prototype = new Node();
  Record.prototype.isRecord = true;
  Record.prototype.isLoaded = true;

  // Parent
  // ------
  //
  // A Parent is the base class for nodes that have children.

  var Parent = function(id, nodeData) {
    Node.apply(this, arguments);
    this.children = [];
  }

  Parent.prototype = new Node();
  Parent.prototype.isParent = true;
  Parent.prototype.isExpanded = true;
  Parent.prototype.children = null,

  // Get the last child of this node
  Parent.prototype.getLastChild = function() {
    var lastChild = null;
    if (this.children && this.children.length > 0) {
      lastChild = this.children[this.children.length - 1];
    }
    return lastChild;
  };

  // Get the closest related deferred to this node, IF it is unloaded. Otherwise return self.
  Parent.prototype.getDeferred = function() {
    var last = this;

    // Walk child hierarchy, looking for the first available deferred
    while (last && !last.isLoaded && !last.isDeferred) {
      last = last.children[last.children.length - 1];
    }

    return last;
  };

  // Append an additional child to this node, removing existing Deferred if there is one
  Parent.prototype.appendChild = function(child) {
    this.removeDeferred();

    child.parentNode = this;
    child.depth = this.depth + 1;
    child.offset = this.children.length;
    this.children.push(child);

    return this;
  };

  // Append multiple children to this node, removing existing Deferred if there is one
  Parent.prototype.appendChildren = function(newChildren) {
    this.removeDeferred();

    // First update length, for efficiency's sake
    var startOffset = this.children.length;
    this.children.length = this.children.length + newChildren.length;

    for (var i = 0; i < newChildren.length; i++) {
      var newChild = newChildren[i];

      newChild.depth = this.depth + 1;
      newChild.offset = startOffset + i;
      newChild.parentNode = this;
      
      this.children[newChild.offset] = newChild;
    }

    return this;
  };

  // Remove the last child of this node, if it is an instance of Deferred.
  Parent.prototype.removeDeferred = function() {
    var lastChild = this.children[this.children.length - 1];

    var deferred = (lastChild instanceof Deferred) ? lastChild : null;
    if (deferred) {
      this.children.length = this.children.length - 1;
    }

    return deferred;
  };

  // Mark self as loaded, then update parent hierarchy.
  Parent.prototype.setLoaded = function() {
    this.isLoaded = true;
  };

  Parent.prototype.setLoadedAndUpdateHierarchy = function() {

    this.setLoaded();

    // Walk parent chain, updating loaded status until we find one that isn't loaded (or run out of parents)
    var parentNode = this.parentNode;
    while (parentNode) {
      var isLoaded = true;
      for (var i = 0; isLoaded && (i < parentNode.children.length); i++) {
        var child = parentNode.children[i];
        isLoaded = isLoaded && child.isLoaded;
      }

      if (isLoaded) {
        parentNode.isLoaded = true;
        parentNode = parentNode.parentNode;
      } else {
        parentNode = null;
      }
    }
  };

  Parent.prototype.remove = function(start, count) {

    // Check bounds
    count = Math.min(count, children.length - start - 1);

    // We will return the removed nodes
    var removed = new Array(count);

    // Shift elements from the end of the children over those being removed
    for (var i = start; i < this.children.length - count; i++) {
      var moved = this.children[i + count];
      moved.offset = i;

      removed[i] = this.children[moved.offset];

      this.children[moved.offset] = moved;
    }

    // Truncate the end
    this.children.length = this.children.length - count;

    return removed;
  };

  Parent.prototype.insert = function(start, newNodes) {

    // Update the size of the children to accomodate newNodes
    this.children.length = this.children.length + newNodes.length;

    // Shift elements from the start point of the insertion to the new end of the array
    for (var i = 0; i < newNodes.length; i++) {
      var moved = this.children[start + i];
      moved.offset = moved.offset + newNodes.length;

      this.children[moved.offset] = moved;

      var newNode = newNodes[i];
      newNode.parentNode = this;
      newNode.depth = this.depth + 1;
      newNode.offset = start + i;

      this.children[newNode.offset] = newNode;
    }

    return this;
  };

  // Root
  // ----  
  //
  // The one and only root node, for a given tree.

  var Root = function(nodeData) {
    Parent.call(this, Guid.generate(), nodeData);
    this.appendChild(new Deferred());
  };

  Root.prototype = new Parent();
  Root.prototype.isRoot = true;
  Root.prototype.isExpanded = true;

  // Group
  // -----

  var Group = function(id, nodeData, isExpanded) {
    Parent.apply(this, arguments);
    this.isExpanded = Boolean(isExpanded);
  };

  Group.prototype = new Parent();
  Group.prototype.isGroup = true;
  Group.prototype.isExpanded = false;

  return {
    Node: Node,
    Parent: Parent,
    Root: Root,
    Group: Group,
    Record: Record,
    Deferred: Deferred
  };
});
