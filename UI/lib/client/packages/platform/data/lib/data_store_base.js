define([
  'jquery',
  'ember',
  'query',
  'packages/platform/aggregate',
  'packages/platform/send-ember-action',

  './data_source',
  './data_store_shared',
  './single_result_proxy',
  'logger'
], function(
  $,
  Em,
  Query,
  Aggregate,
  sendEmberAction,

  DataSource,
  shared,
  SingleResultProxy,
  logger
) {
  'use strict';

  // DataStoreBase
  // =============
  //
  // A DataStore manages singleton instances of all local copies for a single endPoint
  return Em.Object.extend({

    DataSource: DataSource,

    // Model type to materialize
    Model: function() { throw 'required'; }.property(),

    // Model spec
    Spec: function() { throw 'required'; }.property(),

    // Shared data between all data stores
    shared: shared,

    // REQUIRED
    name: null,

    // Map a query to a dataSource
    createDataSourceForQuery: function() { throw 'required'; }.property(),
    
    isSearchCaseSensitive: false,

    // Maps a materialized object to its unique id
    uniqueId: function(attrs) {
      return this.get('Spec.idNames').map(function(name) {
        if (name !== null) {
          var result;
          var attrsTmp = attrs;
          name.split('.').forEach(function(val) {
            attrsTmp = attrsTmp[val];
            result = attrsTmp;
          });
          return result;
        }
      }).join(':');
    },

    // Timestamp when last update happened to all data backed by this store
    invalidatedAt: null,

    init: function() {
      this.setProperties({
        dataSourcesByOwner: {},
        dataSourcesByQuery: {},
        materializedObjectsById: {}
      });
    },

    getFromCache: function(id) {
      return this.get('materializedObjectsById')[id];
    },

    invalidate: function() {
      logger.log('AM_DATA: DATA_STORE: invalidate', this.get('name'), this);
      this.set('invalidatedAt', new Date());
    },

    allInvalidated: function() {
      var allInvalidatedAt = this.get('shared.invalidatedAt');
      var invalidatedAt = this.get('invalidatedAt');

      if (allInvalidatedAt && (!invalidatedAt || allInvalidatedAt > invalidatedAt)) {
        this.invalidate();
      }
    }.observes('shared.invalidatedAt'),

    dataDidInvalidate: function() {
      this.freshenAllOwnedDataSources();
    }.observes('invalidatedAt'),

    freshenAllOwnedDataSources: function() {
      var dataSources = this.get('dataSourcesByQuery');
      for (var uri in dataSources) {
        var dataSource = dataSources[uri];
        if (dataSource.get('query.autoRefresh') && dataSource.get('owners.length') > 0) {
          dataSource.freshen();
        }
      }
    },

    newQuery: function(args) {
      args = args || {};
      return Query.Search.create($.extend(args, { store: this }));
    },

    acquireOne: function(owner, queryOrId, loadedCallback, loadFailedCallback, force, autoRefresh) {
      var query = (!Em.isNone(queryOrId) && queryOrId.get && queryOrId.get('isQuery')) ? queryOrId : Query.Singleton.create({ store: this, autoRefresh: Em.isNone(autoRefresh) ? true : autoRefresh, id: queryOrId });
      var proxy = SingleResultProxy.create({});
      proxy.set('dataSource', this.acquire(owner, query, loadedCallback, loadFailedCallback, proxy, force));
      return proxy;
    },

    acquireAll: function(owner, loadedCallback, loadFailedCallback) {
      return this.acquire(owner, { store: this, limit: 1000, offset: 0 }, loadedCallback, loadFailedCallback);
    },

    count: function(owner, queryParams, loadedCallback, loadFailedCallback, loadedCallbackScope, force) {
      var isCounter = { isCounter: true };
      var query = (queryParams.get && queryParams.get('isQuery'))
          ? queryParams.copy().reopen(isCounter)
          : Query.Search.create($.extend(queryParams, isCounter));

      return SingleResultProxy.create({
        dataSource: this.acquire(owner, query, loadedCallback, loadFailedCallback, loadedCallbackScope, force)
      });
    },

    acquire: function(owner, queryParams, loadedCallback, loadFailedCallback, loadedCallbackScope, force) {
      if (owner !== null && typeof(owner) !== 'string') {
        throw ['Invalid owner provided - must be string or null', owner, queryParams, force];
      }

      var query = (queryParams.get && queryParams.get('isQuery')) ? queryParams.copy() : Query.Search.create(queryParams);
      query.set('store', this);
      var queryUri = query.get('uri');

      // Note activity
      sendEmberAction('dataAcquired', this.get('name'), query);

      var dataSourcesByOwner = this.get('dataSourcesByOwner');
      var dataSourcesByQuery = this.get('dataSourcesByQuery');

      var dataSource = null;

      if (owner) {
        // Get whatever dataSource owner had previously acquired
        var previousDataSourceForOwner = dataSourcesByOwner[owner];
        if (previousDataSourceForOwner) {
          // Is it the same? 
          var previousQueryUri = previousDataSourceForOwner.get('query.uri');
          if (previousQueryUri === queryUri) {
            // If so then keep it.
            dataSource = previousDataSourceForOwner;
          } else {
            // Otherwise release it.
            dataSourcesByOwner[owner] = undefined;
            previousDataSourceForOwner.release(owner);

            // TODO: Design and implement a dataSource release strategy
            // if (previousDataSourceForOwner.get('orphaned')) {
            // dataSourcesByQuery[previousQueryUri] = undefined;
            // }
          }
        }
      }

      if (!dataSource) {
        // Get a dataSource for the given query
        dataSource = dataSourcesByQuery[queryUri];
        if (!dataSource) {
          // Build a dataSource
          dataSource = this.createDataSourceForQuery(query);

          // Register dataSource
          dataSourcesByQuery[queryUri] = dataSource;
        }

        if (owner) {
          // Acquire dataSource for the given owner
          dataSource.get('owners').pushObject(owner);
          dataSourcesByOwner[owner] = dataSource;
        }
      }

      // Run query
      dataSource.freshen(owner === null || force, loadedCallback, loadFailedCallback, loadedCallbackScope || dataSource);

      return dataSource;
    },

    // Transform the deserialized attributes from the DataSource into Model instances. Such model instances
    // may be newly loaded, or may be updated instances of models that were previously loaded.
    loadAttrHashes: function(attrHashes) {
      return this.materializeAttrHashes(attrHashes, this.get('materializedObjectsById'));
    },

    materializeAttrHashes: function(attrHashes, materializedObjectsById) {
      var store = this;

      var materializedObjectProxies = attrHashes.map(function(attrs, i) {
        // Every object has a unique id, which we key from.
        var id = store.uniqueId(attrs);

        var obj = materializedObjectsById[id];
        var resources = store.get('Spec.resource');
        if (!obj) {
          // No model with this id has been loaded yet. Create a new model instance for these attributes.
          var materializedData = Em.Object.extend(attrs).create();

          // Replace all aggregate javascript object with Ember objects
          resources.forEach(function(resource) {
            var mappedMaterializedData = materializedData[resource.attr];
            if (Aggregate.OneToMany.detectInstance(resource.type)) {
              if (mappedMaterializedData) {
                materializedData[resource.attr] = mappedMaterializedData.map(function (data) {
                  return Em.Object.create(data);
                });
              } else {
                logger.error('DATA_STORE: missing attribute: ', resource.attr);
              }
            }
            // add the check for OneToOne
            else if (Aggregate.OneToOne.detectInstance(resource.type)) {
              materializedData[resource.attr] = Em.Object.create(mappedMaterializedData);
            }
          });

          obj = materializedObjectsById[id] = store.get('Model').create({
            dataStore: store,
            data: materializedData,
            presentation: store.get('Spec.Presenter').create({ content: materializedData }),
            loadedAt: new Date()
          });
        } else {
          // Replace all aggregate javascript object with Ember objects
          resources.forEach(function(resource) {
            var attrsForMapping = attrs[resource.attr];
            if (Aggregate.OneToMany.detectInstance(resource.type)) {
              if (attrsForMapping) {
                attrs[resource.attr] = attrsForMapping.map(function(data) {
                  return Em.Object.create(data);
                });
              } else {
                logger.error('DATA_STORE: missing attribute: ', resource.attr);
              }
            }
            // add the check for OneToOne
            else if (Aggregate.OneToOne.detectInstance(resource.type)) {
              attrs[resource.attr] = Em.Object.create(attrsForMapping);
            }
          });

          obj.get('data').setProperties(attrs);
          obj.set('loadedAt', new Date());
        }

        // TODO: Why the extra proxy? Whyyyy.
        return Em.ObjectProxy.create({ content: obj });
      });

      // For every aggregate spec, walk materializedObjectProxies and create /set Object and Presenter instances
      // for the related property, in the data and presentation for the related attribute.

      // logger.log('AM_DATA: STORE: materialize: FINISH', attrHashes, materializedObjectProxies);

      return materializedObjectProxies;
    },

    performSearch: function(query, data) {
      return query.performSearch(data);
    },

    performSort: function(query, data) {
      return query.performSort(data);
    },

    performPaging: function(query, data) {
      return query.performPaging(data);
    },

    loadRawResultSetData: function(query, json) {
      return this.loadAttrHashes(this.get('Spec').mapRawResultSetData(query, json));
    },

    loadRawSingletonData: function(query, json) {
      return this.loadAttrHashes(this.get('Spec').mapRawSingletonData(query, json));
    }
  });
});
