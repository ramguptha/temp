define([
  'ember',
  'guid',
  'packages/platform/async-status',

  './node_types',
  './paged_data'
], function(
  Em,
  Guid,
  AsyncStatus,

  NodeTypes,
  PagedData
) {
  'use strict';

  // Pager
  // =====
  //
  // Abstracts the loading of asynchronously loaded, paged data. This class, and all platform sub-classes,
  // lives in the Paged package.

  // Load Status
  // -----------

  var LoadStatus = AsyncStatus.extend({
    firstQueuedAt: null,
    lastQueuedAt: null,

    isQueued: Em.computed.equal('state', 'QUEUED'),

    setQueued: function(pager, deferred, context, successCallback, cancelCallback, errorCallback) {
      var firstQueuedAt = this.get('firstQueuedAt');

      this.setProperties({
        state: 'QUEUED',
        firstQueuedAt: firstQueuedAt || new Date(),
        lastQueuedAt: new Date(),
        context: context
      });

      this.get('requests').pushObject({
        successCallback: successCallback,
        cancelCallback: cancelCallback,
        errorCallback: errorCallback
      });

      pager.touch('contentChangedAt');

      return this;
    },

    isLoading: Em.computed.or('isQueued', 'isInvoked'),

    setInvoked: function(pager, deferred) {
      this._super();

      pager.touch('contentChangedAt');

      return this;
    },

    setLoaded: function(pager, deferred) {
      this._super();

      deferred.isLoaded = true;

      pager.touch('structureGrewAt structureChangedAt'.w());

      return this;
    },

    setCancelled: function(pager, deferred) {
      this._super();

      // Often we are cancelled because the pagedData has been reset. In such cases, this deferred is likely
      // no longer valid.
      if (deferred.validate()) {
        pager.touch('contentChangedAt');
      }

      return this;
    },

    setFailed: function(pager, deferred, error) {
      this._super(error);

      pager.touch('contentChangedAt');

      return this;
    }
  });

  // Pager
  // -----

  var Pager = Em.Object.extend({
    Guid: Guid,
    NodeTypes: NodeTypes,
    LoadStatus: LoadStatus,

    init: function() {
      this.get('pagedData').getReadTail().nodeData = this.LoadStatus.create();
    },

    // The Paged Data
    // --------------
    //
    // The PagedData lives in the _pagedData_ property. Consumers are expected _not_ to bind it or mutate it in
    // any way other than via DataPager interfaces.

    pagedData: function() {
      return PagedData.create();
    }.property(),

    root: Em.computed.oneWay('pagedData.root'),

    isFullyLoaded: function() {
      return this.get('pagedData').getIsFullyLoaded();
    }.property('structureChangedAt'),

    metrics: function() {
      return this.get('pagedData').getMetrics();
    }.property('structureChangedAt'),

    readTail: function() {
      return this.get('metrics').readTail;
    }.property('metrics'),

    deferredTail: function() {
      var readTail = this.get('metrics').readTail;
      return readTail ? readTail.getDeferred() : readTail;
    }.property('metrics'),

    readLength: function() {
      return this.get('metrics').readLength;
    }.property('metrics'),

    pageSize: 100,

    // Reading Nodes
    // -------------
    //
    // All reads are implemented via facade Methods for the corresponding methods on PagedData, via the
    // _pagedData_ and _root_ properties.

    walk: function(visit) {
      return this.get('pagedData').walk(this.get('root'), visit);
    },

    walkVisible: function(visit) {
      return this.get('pagedData').walkVisible(this.get('root'), visit);
    },

    // Also works if 'string' === typeof(ids)
    lookup: function(ids) {
      return this.get('pagedData').lookup(this.get('root'), ids);
    },

    read: function(offset, count) {
      var results = this.get('pagedData').read(this.get('root'), this.get('pagedData.lastReadEnd'), offset, count);

      var last = results[results.length - 1];
      if (last && !last.isLoaded) {
        results[results.length - 1] = last.getDeferred();
      }

      return results;
    },

    // Get the offset from the root node
    getReadOffset: function(node) {
      var offset = -1;
      var found = false;

      this.walk(function(visitedNode) {
        if (node.isRoot || node === visitedNode) {
          return found = true;
        }

        offset += 1;
      });

      return found ? offset : undefined;
    },

    // Reading Data
    // ------------

    lookupData: function(ids) {
      return this.lookup(ids).mapBy('nodeData');
    },

    // Observing Changes to Data
    // -------------------------
    //
    // DataPager updates various timestamps when the contents of _pagedData_ changes. Consumers are expected to
    // observe them and react accordingly.

    // Timestamp updated when the data associated with loaded PagedData.Nodes has changed, but the
    // structure of the loaded data has not.
    contentChangedAt: null,

    // Timestamp updated when the page structure has grown.
    structureGrewAt: null,

    // Timestamp updated when the page structure has completely reset.
    structureResetAt: null,

    // Timestamp updated when the page structure has shrunk, but not completely reset.
    structureShrunkAt: null,

    // Timestamp updated when the structure of the node tree has changed in any way.
    structureChangedAt: null,

    // Timestamp updated when the content or structure of the node tree has changed.
    changedAt: null,

    // Update timestamps
    touchers: function() {
      return {};
    }.property(),

    touch: function(names) {
      var self = this;

      if (!Em.isArray(names)) {
        names = [names];
      } else {
        names = names.slice(0);
      }

      if (-1 === names.indexOf('changedAt')) {
        names.push('changedAt');
      }

      var touchers = this.get('touchers');
      names.forEach(function(name) {
        if (!touchers[name]) {
          touchers[name] = function() {
            this.set(name, new Date());
          }.bind(self);
        }

        Em.run.once(this, touchers[name]);
      });
    },

    // Tracking Progress of Loading Data
    // ---------------------------------
    //
    // Loading progress is stored in the _nodeData_ of PagedData.Deferred nodes being loaded, and
    // _pageContentChangedAt_ is updated when it changes (so that consumers can re-render if necessary).

    markLoadStatusAsQueued: function(deferred, context, successCallback, cancelCallback, errorCallback) {
      var loadStatus = deferred.nodeData;

      return loadStatus.setQueued(this, deferred, context, successCallback, cancelCallback, errorCallback);
    },

    markLoadStatusAsInvoked: function(deferred) {
      var loadStatus = deferred.nodeData;

      return loadStatus.setInvoked(this, deferred);
    },

    markLoadStatusAsLoaded: function(deferred) {
      var loadStatus = deferred.nodeData;

      return loadStatus.setLoaded(this, deferred);
    },

    markLoadStatusAsCancelled: function(deferred) {
      var loadStatus = deferred.nodeData;

      return loadStatus.setCancelled(this, deferred);
    },

    markLoadStatusAsFailed: function(deferred, error) {
      var loadStatus = deferred.nodeData;

      return loadStatus.setFailed(this, deferred, error);
    },

    // Client-side Filtering
    // ---------------------
    //
    // Override filter to implement client-side filtering.

    // The total number of filtered items is stored in filteredCount.
    preFilteredCount: 0,

    // Accepts:
    //
    //     {
    //       context: context,
    //       data: data,
    //       filteredCount: 0
    //     }
    //
    // Modify data in place as required. this.preFilteredCount will be incremented by loadingData.filteredCount.
    preFilterData: function(loadingData) {},

    // Loading Data Synchronously
    // --------------------------
    //
    // To load data into the DataPager synchronously, invoke _loadData()_. 
    //
    // _loadData(deferred isA PagedData.Deferred, minimumCount isA Number, context, data, hasMore isA Boolean)_
    //
    // _loadData()_ will synchronously load _data_ into _this.pagedData_. It is invoked from _load()_, but is also
    // suitable for invoking directly.
    //
    // Synchronously loads _data_ into _this.data_.
    //
    // _minimumCount_ is only a (strong) guideline. There is no hard expectation that at most _minimumCount_ nodes
    // will be loaded.
    loadData: function(deferred, minimumCount, context, data, hasMore) {

      // Pre-filter data
      var filtered = { context: context, data: data, filteredCount: 0 };
      this.preFilterData(filtered);
      this.incrementProperty('preFilteredCount', filtered.filteredCount);

      var parentNode = this.updateNodesFromData(
        deferred, deferred.parentNode, minimumCount, filtered.context, filtered.data
      );

      if (hasMore) {
        parentNode.appendChild(new this.NodeTypes.Deferred(this.LoadStatus.create()));
      } else {
        parentNode.setLoadedAndUpdateHierarchy();
      }

      this.touch('structureGrewAt structureChangedAt'.w());
      this.markLoadStatusAsLoaded(deferred);

      return this;
    },

    // Mapping Raw Data to PagedData.Nodes
    // -----------------------------------
    //
    // _loadData()_ delegates updates to the node tree to _updateNodesFromData()_. Sub-classes are expected to
    // override this. The default implementation assumes data is enumerable, mapping each element in it to the
    // _nodeData_ of a corresponding NodeTypes.Record. Implementations of _updateNodesFromData()_ are expected to
    // invoke _appendNodes()_, _terminateGroup()_, and _appendGroup()_ to get their work done.

    updateNodesFromData: function(deferred, parentNode, minimumCount, context, data) {
      var self = this;
      return this.appendNodes(parentNode, data.map(function(obj) {
        return self.createRecord(parentNode, obj);
      }));
    },

    appendNodes: function(parentNode, nodes) {
      parentNode.appendChildren(nodes);
      return parentNode;
    },

    terminateGroup: function(parentNode) {
      parentNode.removeDeferred();
      parentNode.setLoaded(false);
      return parentNode.parentNode;
    },

    appendGroup: function(parentNode, group) {
      parentNode.appendChild(group);
      return group;
    },

    // Node Creation
    // -------------

    expandGroupsByDefault: false,

    createRecord: function(parentNode, obj) {
      return new this.NodeTypes.Record(this.Guid.generate(), obj);
    },

    createGroup: function(parentNode, obj) {
      return new this.NodeTypes.Group(this.Guid.generate(), obj, this.get('expandGroupsByDefault'));
    },

    // Loading Data Asynchronously
    // ---------------------------
    //
    // To load data into a DataPager asynchronously, invoke _load()_ with a PagedData.Deferred, an optional context
    // and callbacks for success and failure. _load()_ will update the node's related _nodeData_ with the details of
    // the request, and then add it to the _loadQueue_. If the provided PagedData.Deferred is already in
    // the _loadQueue_, or is already loading (i.e. is _loadInProgress_) the request will be updated with
    // the provided context and the success and failure callbacks added to a list for invocation when the deferred
    // is loaded.

    load: function(deferred, context, successCallback, cancelCallback, errorCallback) {

      this.markLoadStatusAsQueued(deferred, context, successCallback, cancelCallback, errorCallback);

      var loadQueue = this.get('loadQueue');
      if (!loadQueue.contains(deferred) && this.get('loadInProgress') !== deferred) {
        loadQueue.pushObject(deferred);
      }

      this.ensureLoading();
    },

    // An array of NodeTypes.Deferred instances, for which _load()_ has been invoked.
    loadQueue: function() { return []; }.property(),

    loadInProgress: null,

    // _ensureLoading()_ invokes the next request on the load queue, if none is already in progress.
    ensureLoading: function() {
      var self = this;

      if (this.get('loadInProgress')) {
        return;
      }

      var deferred = this.get('loadQueue').shiftObject();

      if (!deferred || !deferred.validate()) {
        return;
      }

      var loadStatus = this.markLoadStatusAsInvoked(deferred);
      var context = loadStatus.context;
      var minimumCount = this.get('pageSize');

      this.set('loadInProgress', deferred);

      var nextLoad = function() {
        self.set('loadInProgress', null);
        self.ensureLoading();
      };

      var successCallback = function(data, hasMore) {
        if (loadStatus.get('isCancelled')) {
          return;
        }

        self.loadData(deferred, minimumCount, context, data, hasMore);

        loadStatus.get('requests').forEach(function(request) {
          if (request.successCallback) {
            request.successCallback.call(self, context, data);
          }
        });

        nextLoad();
      };

      var errorCallback = function(detail) {
        if (loadStatus.get('isCancelled')) {
          return;
        }

        loadStatus = self.markLoadStatusAsFailed(deferred, detail);

        loadStatus.get('requests').forEach(function(request) {
          if (request.errorCallback) {
            request.errorCallback.call(self, context, detail);
          }
        });

        nextLoad();
      };

      this.getNodes(deferred, minimumCount, context, successCallback, errorCallback);

      return this;
    },

    // Cancel all loads in progress.
    cancelLoading: function() {
      var self = this;

      var cancel = function(deferred) {
        var loadStatus = self.markLoadStatusAsCancelled(deferred);

        // Make a copy of the load requests, in case cancel callbacks alter it (as they are wont to do).
        var loadStatusRequests = loadStatus.get('requests');
        var pendingRequests = loadStatusRequests.slice(0);
        loadStatusRequests.clear();

        pendingRequests.forEach(function(request) {
          if (request.cancelCallback) {
            request.cancelCallback.call(self, loadStatus.context);
          }
        });
      };

      var loadInProgress = this.get('loadInProgress');
      if (loadInProgress) {
        cancel(loadInProgress);
        this.set('loadInProgress', null);
      }

      var loadQueue = this.get('loadQueue');
      loadQueue.forEach(cancel);
      loadQueue.clear();

      return this;
    },

    // Sub-classes implement asynchronous loading by overriding _getNodes()_. 
    //
    // _getNodes()_ is invoked from _nextLoad()_ to retrieve the requested data. On success, _getNodes()_ is
    // expected to invoke the _successCallback(context, data)_, and on failure _getNodes()_ is expected to invoke
    // _errorCallback(context, data)_.
    //
    // _minimumCount_ is set to _pageSize_.

    getNodes: function(deferred, minimumCount, context, successCallback, errorCallback) {
      throw 'Implement me';
    },

    // Other Mutations
    // ---------------

    reset: function() {

      // Order is important, which sucks, but that's what we get for mixing callback and observer event styles.

      // First, reset the loaded data and preFilteredCount
      this.set('preFilteredCount', 0);
      this.get('pagedData').reset(this.get('root'), this.LoadStatus.create());

      // Second, cancel loads in progress, which will often kick off new loads.
      this.cancelLoading();

      // Third, fire observers.
      this.touch('structureResetAt structureChangedAt'.w());

      return this;
    },

    update: function(node, nodeData) {
      node.nodeData = nodeData;
      this.touch('contentChangedAt');

      return this;
    },

    removeOne: function(node) {
      return this.removeMany(node.parentNode, node.offset, 1)[0];
    },

    removeMany: function(parentNode, start, count) {
      parentNode.remove(start, count);
      this.touch('structureShrunkAt structureChangedAt');

      return this;
    },

    insert: function(parentNode, start, newNodes) {
      parentNode.insert(start, newNodes);
      this.touch('structureGrewAt structureChangedAt');

      return this;
    },

    open: function(node) {
      if (!Em.get(node, 'isGroup')) {
        throw ['Can only open groups', node];
      }

      Em.set(node, 'isExpanded', true);

      this.touch('structureGrewAt structureChangedAt'.w());
    },

    close: function(node) {
      if (!Em.get(node, 'isGroup')) {
        throw ['Can only close groups', node];
      }

      Em.set(node, 'isExpanded', false);

      this.touch('structureShrunkAt structureChangedAt'.w());
    },

    toggle: function(node) {
      if (!Em.get(node, 'isGroup')) {
        throw ['Can only toggle groups', node];
      }

      if (Em.get(node, 'isExpanded')) {
        this.close(node);
      } else {
        this.open(node);
      }
    }
  });

  return Pager.reopenClass({
    NodeTypes: NodeTypes,

    Node: NodeTypes.Node,
    Root: NodeTypes.Root,
    Group: NodeTypes.Group,
    Record: NodeTypes.Record,
    Deferred: NodeTypes.Deferred,

    PagedData: PagedData,
    LoadStatus: LoadStatus
  });
});
