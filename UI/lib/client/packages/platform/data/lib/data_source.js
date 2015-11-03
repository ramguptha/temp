define([
  'ember',
  'packages/platform/ajax',
  './data_counter',

  'logger'
], function(
  Em,
  Ajax,
  DataCounter,

  logger
) {
  'use strict';

  return Em.ArrayProxy.extend({
    Ajax: Ajax,
    DataCounter: DataCounter,

    name: 'Data Source',

    // This property replaces { content: null, init: function() { this.set('content', Em.A()); } }. The 
    // change has been made because of a bug in Ember 1.3.1:
    //
    //     var FirstElementProxy = Em.ObjectProxy.extend({
    //       array: null,
    //       content: Em.computed.alias('array.firstObject')
    //     });
    //
    //     var broken = FirstElementProxy.create({
    //       array: Em.ArrayProxy.extend({
    //         init: function() { this.set('content', Em.A()); }
    //       }).create()
    //     });
    //
    // Problem seems related to cache invalidation, as we must "get" first to repro
    //
    //     broken.get('name');
    //     broken.get('array').replace(0, 0, [Em.Object.create({ name: 'foo' })]);
    //
    //     console.log('Should be foo: ' + broken.get('name'));
    //
    content: function() { return Em.A(); }.property(),

    // A DataSource has one or more owners. When the number of owners falls to 0, the DataSource may be released.
    owners: function() { return Em.A(); }.property(),

    // The following callbacks will be invoked when the data_source is loaded.
    loadedCallbackQueue: function() { return Em.A(); }.property(),

    // The following callbacks will be invoked when the data_source fails to load.
    loadFailedCallbackQueue: function() { return Em.A(); }.property(),

    // When this query was last invalidated
    invalidatedAt: function() {
      return this.get('query.store.invalidatedAt');
    }.property('query.store.invalidatedAt'),

    // When this query was last loaded with data
    loadedAt: null,

    // When loading began for this query
    loadInvokedAt: null,

    // When this query last failed to load data
    loadFailedAt: null,

    // Details of the last problem encountered while loading data. NOT cleared on successful load.
    lastLoadError: null,

    // When this query was last released by an owner
    releasedAt: null,

    // Specifies what objects fall into scope for this source. Multiple owners will share the same DataSource if their query
    // has the same hash.
    query: null,

    spec: function() {
      return this.get('query.store.Spec');
    }.property('query.store.Spec'),

    // If isCounter is true, the dataSource is expected to populate itself with an Object array with a "total" property
    isCounter: function() {
      return this.get('query.isCounter');
    }.property('query.isCounter'),

    // If isLastPage is true, then there are no pages to acquire.
    isLastPage: undefined,

    // The total number of records matching the query i.e. the number of records returned without any paging or offset specified
    total: function() {
      throw 'deprecated';
    }.property(),

    byId: function() {
      var map = {};
      (this.get('content') || Em.A()).forEach(function(obj) {
        map[obj.get('id')] = obj;
      });
      return map;
    }.property('content.[]'),

    esc: function(str) {
      return encodeURIComponent(str);
    },

    loadComplete: function() {
      logger.log('AM_DATA: DATA_SOURCE: loadComplete', this, this.get('query.uri'));

      this.set('loadedAt', new Date());

      var callbacks = this.get('loadedCallbackQueue');
      callbacks.forEach(function(spec) {
        spec.callback.call(spec.scope, this);
      }, this);
      callbacks.clear();
      this.get('loadFailedCallbackQueue').clear();
    },

    loadFailed: function(errorDetail) {
      logger.log('AM_DATA: DATA_SOURCE: loadFailed', this, this.get('query.uri'));

      this.setProperties({
        loadFailedAt: new Date(),
        lastLoadError: errorDetail
      });

      var callbacks = this.get('loadFailedCallbackQueue');
      callbacks.forEach(function(spec) {
        spec.callback.call(spec.scope, this);
      }, this);
      callbacks.clear();
      this.get('loadedCallbackQueue').clear();
    },

    loadInProgress: function() {
      var attrs = this.getProperties('loadInvokedAt loadedAt loadFailedAt'.w());
      return attrs.loadInvokedAt > attrs.loadedAt && attrs.loadInvokedAt > attrs.loadFailedAt;
    }.property('loadedAt', 'loadFailedAt', 'loadInvokedAt'),

    loaded: function() {
      return this.get('loadedAt') != null;
    }.property('loadedAt'),

    failed: function() {
      return this.get('loadFailedAt') > this.get('loadInvokedAt');
    }.property('loadFailedAt', 'loadInvokedAt'),

    error: function() {
      return this.get('failed') ? this.get('lastLoadError') : null;
    }.property('failed', 'lastLoadError'),

    fresh: function() {
      var attrs = this.getProperties('loadedAt', 'invalidatedAt');
      var fresh = attrs.loadedAt != null && (attrs.invalidatedAt === null || attrs.invalidatedAt < attrs.loadedAt);
      logger.log('AM_DATA: DATA_SOURCE: fresh', fresh);
      return fresh;
    }.property('loadedAt', 'invalidatedAt'),

    load: function() {

      // Invoke GET on URI
      var self = this;

      self.Ajax.get(
        this.get('name'),
        this.get('uri'),

        {},
        function onSuccess(){
          self.onLoadSuccess.apply(self, arguments);
        },
        function onFailure() {
          self.loadFailed.apply(self, arguments);
        }
      );
    },

    onLoadSuccess: function(rawData) {
      var spec = this.get('spec');
      var query = this.get('query');
      var store = this.get('query.store');
      var content,  attrHashes;

      if (this.get('isCounter')) {
        content = this.get('content');
        content.replace(0, content.get('length'), [
          this.DataCounter.create({ total: spec.mapRawCounterData(query, rawData) })
        ]);
      } else {
        if (query.isSingleton) {
          attrHashes = spec.mapRawSingletonData(query, rawData);
        } else {
          var unlimitedAttrHashes = spec.mapRawResultSetData(query, rawData);
          var limit = query.get('limit');
          attrHashes = this.applyLimitsAndSetIsLastPage(unlimitedAttrHashes, limit);
        }

        // Materialize resultSet
        var materializedObjectProxies = store.loadAttrHashes(attrHashes);

        // Update items in dataStore
        content = this.get('content');
        content.replace(0, content.get('length'), materializedObjectProxies);
      }

      this.loadComplete();
    },

    // This is a separate function in order to minimize implementation of datastores related to endpoints
    // that do not support paging.
    applyLimitsAndSetIsLastPage: function(attrHashes, limit) {
      this.set('isLastPage', attrHashes.length <= limit);
      return attrHashes.slice(0, limit);
    },

    freshen: function(force, loadedCallback, loadFailedCallback, callbackScope) {
      if (force !== true && this.get('fresh')) {
        logger.log('AM_DATA: DATA_SOURCE: freshen: data is fresh', this.get('query.uri'), this);
        if (loadedCallback) {
          // In order for various variable assignments to happen (and hence make execution scope work) before
          // invocation, ensure that callback is called asynchronously even when data is present.
          Em.run.later(this, function() {
            loadedCallback.call(callbackScope, this);
          }, 0);
        }
      } else {
        if (!this.get('loadInProgress')) {
          logger.log('AM_DATA: DATA_SOURCE: freshen: data is not fresh - loading', this.get('query.uri'), this);
          this.set('loadInvokedAt', new Date());
          this.load();
        } else {
          logger.log('AM_DATA: DATA_SOURCE: freshen: data is not fresh, but load already in progress', this.get('query.uri'), this);
        }

        if (loadedCallback) {
          this.get('loadedCallbackQueue').pushObject({ scope: callbackScope, callback: loadedCallback });
        }

        if (loadFailedCallback) {
          this.get('loadFailedCallbackQueue').pushObject({ scope: callbackScope, callback: loadFailedCallback });
        }
      }
    },

    release: function(owner) {
      var owners = this.get('owners');
      owners.removeObject(owner);

      if (this.get('orphaned')) {
        logger.log('AM_DATA: DATA_STORE: eligable for disposal', this);
      }
    },

    orphaned: function() {
      return 0 === this.get('owners').length;
    }.property('owners.[]')
  });
});
