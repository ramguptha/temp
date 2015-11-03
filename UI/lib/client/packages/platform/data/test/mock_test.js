define([
  'testing',
  'query',
  '../lib/spec',
  '../lib/model',
  '../lib/mock_data_store',
  '../lib/mock_data_source'
], function(
  Testing,
  Query,
  Spec,
  Model,
  MockDataStore,
  MockDataSource
) {
  'use strict';

  var spec = Spec.extend({
    resource: [
      { attr: 'id' },
      { attr: 'name' }
    ],

    names: 'id name'.w()
  }).create();

  var store = MockDataStore.extend({
    Spec: spec,
    Model: Model.extend(),

    MockData: [
      // id: 1
      { name: 'one' },

      // id: 2
      { name: 'two' },

      // id: 3
      { name: 'three' }
    ]
  }).create();

  return Testing.module('mocks', [
    Testing.asyncTest('count', function(assert, start) {
      store.count(
        null,
        Query.Search.create({ searchFilter: 't', adhocSearchableNames: 'name'.w() }),
        function(dataSource) {
          assert.strictEqual(dataSource.get('length'), 1, '1 match');
          assert.strictEqual(dataSource.objectAt(0).get('total'), 2, 'count is 2');

          start();
        }
      );
    }),

    Testing.asyncTest('search', function(assert, start) {
      store.acquire(
        null,
        Query.Search.create({ searchFilter: 't', adhocSearchableNames: 'name'.w() }),
        function(dataSource) {
          assert.strictEqual(dataSource.get('length'), 2, '2 matches');
          assert.ok(dataSource.contains(store.get('materializedObjectsById')[2]), 'two is matched');
          assert.ok(dataSource.contains(store.get('materializedObjectsById')[3]), 'three is matched');

          start();
        }
      );
    }),

    Testing.asyncTest('singleton', function(assert, start) {
      store.acquireOne(
        null,
        2,
        function(dataSource) {
          assert.strictEqual(dataSource.get('length'), 1, '1 match');
          assert.ok(dataSource.contains(store.get('materializedObjectsById')[2]), 'two is matched');

          start();
        }
      );
    })
  ]);
});
