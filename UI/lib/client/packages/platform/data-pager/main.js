define([
  'packages/platform/queried-pager',
  'packages/platform/data'
], function(
  QueriedPager,
  AbsData
) {
  'use strict';

  // Data Pager
  // ==========
  //
  // A Data Pager is a pager that is loaded via our data package.

  return QueriedPager.extend({
    dataStore: null,

    init: function() {
      this._super();

      // Observers
      this.getProperties('dataStore.invalidatedAt dataStore.Spec.enumColumnTypes'.w());
    },

    dataStoreDidChangeOrInvalidate: function() {
      this.reset();
    }.observes('dataStore.invalidatedAt'),

    enumColumnsDidLoad: function() {
      // Update observer trigger
      var enumColumnTypes = this.get('dataStore.Spec.enumColumnTypes');
      if (enumColumnTypes) {
        enumColumnTypes.mapBy('loaded');
      }

      this.touch('contentChangedAt');
    }.observes('dataStore.Spec.enumColumnTypes.@each.loaded'),

    // Loading Nodes
    // -------------

    getNodes: function(deferred, minimumCount, context, successCallback, errorCallback) {
      return this.acquireNodesFromDataStore.apply(this, arguments);
    },

    // Break getNodes() implementation out into a separate method for easy asynchronous invocation by subclasses
    // that need to override it.
    acquireNodesFromDataStore: function(deferred, minimumCount, context, successCallback, errorCallback) {
      var paging = {
        limit: minimumCount,
        offset: this.getReadOffset(deferred)
      };

      var wrappedSuccessCallback = function(dataSource) {
        successCallback(dataSource, !dataSource.get('isLastPage'));
      };

      var wrappedErrorCallback = function(dataSource) {
        errorCallback(dataSource.get('lastLoadError'));
      };

      var searchQuery = this.get('searchQuery');

      if (searchQuery) {
        var pagedSearchQuery = searchQuery.copy();
        pagedSearchQuery.setProperties(paging);
      } else {
        var pagedSearchQuery = paging;
      }

      this.get('dataStore').acquire(null, pagedSearchQuery, wrappedSuccessCallback, wrappedErrorCallback, this);
    },

    updateNodesFromData: function(deferred, parentNode, minimumCount, context, dataSource) {
      var self = this;

      // We queue nodes up here, then append them when "closing" a parent chain.
      var nodesForCurrentParent = [];

      dataSource.forEach(function(model) {

        // Get a valid parent chain for the model
        parentNode = self.ensureMatchingParentChain(parentNode, model, nodesForCurrentParent);

        // Queue node for addition to the current parent
        nodesForCurrentParent.push(new self.NodeTypes.Record(model.get('id'), model));
      });

      // Append any queued nodes to the current parent
      return this.appendNodes(parentNode, nodesForCurrentParent);
    },

    ensureMatchingParentChain: function(parentNode, model, nodesForCurrentParentChain) {
      var groupedAttrNames = this.get('group') || [];

      // Get the portion of the parent chain (from the root) that matches the current record
      var deepestMatchingParentNode = this.getDeepestMatchingParentNode(parentNode, model);

      // Terminate parents while the immediate parent is different from the deepest matching one
      while (deepestMatchingParentNode !== parentNode) {
        if (nodesForCurrentParentChain.length > 0) {
          if (parentNode.depth !== Em.get(groupedAttrNames, 'length')) {
            throw [
              'Should not have nodes to append without a valid parent chain', parentNode, nodesForCurrentParentChain
            ];
          }

          this.appendNodes(parentNode, nodesForCurrentParentChain);
          nodesForCurrentParentChain.length = 0;
        }

        parentNode = this.terminateGroup(parentNode);
      }

      // Create missing portions of the parent chain
      var depth = parentNode.depth;
      while (depth < Em.get(groupedAttrNames, 'length')) {
        parentNode = this.appendGroup(parentNode, this.createGroup(parentNode, model));
        depth += 1;
      }

      return parentNode;
    },

    getDeepestMatchingParentNode: function(parentNode, model) {
      if (parentNode.isRoot) {

        // The root node always matches (and terminates recursion)
        return parentNode;
      }

      var deeperMatchingParentNode = this.getDeepestMatchingParentNode(parentNode.parentNode, model);

      // If the deeper matching node is the immediate parent and we match too, pass current parentNode up
      if (deeperMatchingParentNode === parentNode.parentNode && this.modelMatchesGroup(parentNode, model)) {
        return parentNode;
      }

      // Otherwise, the deeper matching node is the deepest match
      return deeperMatchingParentNode;
    },

    // Grouping Hooks
    // --------------
    //
    // Sub-classes may override _createGroup()_ and _modelMatchesGroup()_ to implement custom grouping
    // behaviour.

    createGroup: function(parentNode, model) {
      return new this.NodeTypes.Group(this.Guid.generate(), model, this.get('expandGroupsByDefault'));
    },

    createRecord: function(parentNode, model) {
      return new this.NodeTypes.Record(model.get('id'), model);
    },

    modelMatchesGroup: function(parentNode, model) {
      var groupedAttrName = this.get('group')[parentNode.depth - 1];

      var groupedValue = Em.get(parentNode.nodeData, 'data.' + groupedAttrName);
      var modelValue = model.get('data.' + groupedAttrName);

      return String(groupedValue) === String(modelValue) && typeof(groupedValue) === typeof(modelValue);
    }
  });
});
