define([
  'testing',
  'query',
  'packages/platform/data/stub',
  'packages/platform/data-pager'
], function(
  Testing,
  Query,
  StubData,
  DataPager
) {
  'use strict';

  return Testing.package('data-pager', [
    Testing.module('main', [
      Testing.test('dataStore', function(assert) {
        var pager = DataPager.extend({
          resetCount: 0,
          reset: function() {
            this.incrementProperty('resetCount');
            this._super();
          }
        }).create({
          dataStore: Em.Object.create()
        });

        assert.strictEqual(pager.get('resetCount'), 0, 'No resets on init');

        pager.set('dataStore', Em.Object.create());

        assert.strictEqual(pager.get('resetCount'), 1, 'Reset when dataStore changes');
      }),

      Testing.asyncTest('load', function(assert, start) {
        var pager = DataPager.create({
          searchQuery: Query.Search.create(),
          dataStore: StubData.StubDataStore.create()
        });

        pager.load(pager.get('readTail'), null, function() {
          assert.deepEqual(
            pager.get('root.children').mapBy('nodeData.id'),
            StubData.defaultStubData.mapBy('id'),
            'stub records appended'
          );

          start();
        });
      }),

      Testing.asyncTest('load with grouping', function(assert, start) {
        var spec = StubData.makeStubSpec('value1 value2 value3'.w());

        var data = [];

        for (var i = 0; i < 12; i++) {
          data.push(
            {
              id: String(i),
              value1: 'value1.' + Math.floor(i / 4),
              value2: 'value2.' + Math.floor(i / 2),
              value3: 'value3.' + i
            }
          );
        }

        var dataStore = StubData.StubDataStoreBase.create({
          Spec: spec,
          Model: StubData.StubModel.extend({ Spec: spec }),
          stubData: data
        });

        var pager = DataPager.create({
          searchQuery: Query.Search.create({ group: 'value1 value2'.w() }),
          dataStore: dataStore
        });

        pager.load(pager.get('root').children[0], null, function() {
          assert.strictEqual(pager.get('root.children.length'), 3, '3 top level groups');

          pager.get('root.children').forEach(function(group) {
            assert.strictEqual(group.children.length, 2, 'top level groups have 2 children each');

            group.children.forEach(function(childGroup) {
              assert.ok(childGroup instanceof pager.NodeTypes.Group, '2nd level nodes are groups');
              assert.strictEqual(childGroup.children.length, 2, '2nd level groups have 2 children each');

              childGroup.children.forEach(function(record) {
                assert.ok(record instanceof pager.NodeTypes.Record, 'leaf nodes are records');
              });
            });
          });

          start();
        });
      })
    ])
  ]);
});
