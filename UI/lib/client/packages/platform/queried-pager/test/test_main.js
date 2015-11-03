define([
  'testing',
  'ember',
  'packages/platform/query',
  'packages/platform/queried-pager'
], function(
  Testing,
  Em,
  Query,
  QueriedPager
) {
  'use strict';

  var SearchQuery = Query.Search;

  return Testing.package('queried-pager', [
    Testing.module('main', [
      Testing.asyncTest('searchQuery', function(assert, start) {
        var harness = Em.Object.create({
          searchQuery: SearchQuery.create({
            searchAttr: 'foo',
            searchFilter: 'bar'
          })
        });

        var pager = QueriedPager.extend({
          resetCount: 0,
          reset: function(root) {
            this.incrementProperty('resetCount');
            return this._super(root);
          }
        }).create({
          harness: harness,
          searchQueryBinding: 'harness.searchQuery'
        });

        assert.ok(SearchQuery.detectInstance(pager.get('searchQuery')), 'Binding is set');
        assert.strictEqual(0, pager.get('resetCount'), 'Reset is 0 after init');

        harness.set('searchQuery.searchFilter', 'baz');

        Em.run.later(this, function() {
          assert.strictEqual(pager.get('resetCount'), 1, 'Reset is invoked on bound query change');

          start();
        }, harness.get('searchQuery.observableUriUpdateDelayInMilliseconds') * 2);
      }),

      Testing.test('getGroupedAttrForParent', function(assert) {
        var pager = QueriedPager.create({
          searchQuery: SearchQuery.create({
            group: ['foo', 'bar']
          })
        });

        var group = pager.appendGroup(pager.get('root'), new pager.NodeTypes.Group('foo id'));
        group = pager.appendGroup(group, new pager.NodeTypes.Group('bar id'));

        group = pager.appendNodes(group, [new pager.NodeTypes.Record('record')]);

        assert.strictEqual(
          pager.getGroupedAttrForParent(pager.get('searchQuery.group'), group),
          'bar',
          'Deepest group attr'
        );

        assert.strictEqual(
          pager.getGroupedAttrForParent(pager.get('searchQuery.group'), group.parentNode),
          'foo',
          'Shallowest group attr'
        );
      })
    ])
  ]);
});
