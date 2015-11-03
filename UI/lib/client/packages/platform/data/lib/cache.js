define([
], function(
) {
  'use strict';

  // Keeps record of an observer of some data
  var Lock = Em.Object.extend({
    // The observer
    owner: null,

    // Identifies the data being observed
    criteria: null,

    // When observation began
    lockStart: null,

    init: function(owner, criteria) {
      this.set('owner', owner);
      this.set('criteria', criteria);
      this.set('lockStart', Date());
    }
  });

  // Describes some subset of records
  var Criteria = Em.Mixin.create({
    match: function() { throw 'required'; }.property()
  });

  // Notes observation of ALL records
  var CriteriaAny = Em.Object.extend(Criteria, {
    id: 'LOCK ALL RECORDS',

    match: function(record) {
      return true;
    }
  });

  // Notes observation of a single record
  var CriteriaOne = Em.Object.extend(Criteria, {
    id: null,

    match: function(record) {
      return this.get('id') === record.get('id');
    }
  });

  // Caches data for a given type
  var Cache = Em.Object.extend({
    init: function(cacheOwner) {
      // Owner of the cache
      this.set('cacheOwner', cacheOwner);

      // map(Owner => locks)
      this.set('locks', {});

      // idx => rawData
      this.set('rawData', Em.A());

      // idx => materialized data (sparse)
      this.set('materializedData', Em.A());

      // id => idx into rawData
      this.set('index', {});
    },

    lock: function(owner, criteria) {
      // release any data previously acquired by owner
      var locks = this.get('locks');
      var previousLock = locks[owner];
      logger.log('STORE: RELEASE', owner, previousLock);
      locks[owner] = null;

      logger.log('STORE: TODO: CULL DATA');

      // note that owner has acquired this data
      locks[owner] = Lock.create(owner, criteria);
    }
  });

  var AggregateCache = Em.Object.extend({
    init: function() {
      this.set('caches', Em.A());
    },

    register: function(cacheOwner) {
    }
  });

  return AggregateCache;
});
