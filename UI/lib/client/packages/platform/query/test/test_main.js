define([
  'testing',
  '../lib/search_query',
  'packages/platform/advanced-filter'
], function(
  Testing,
  SearchQuery,
  AdvancedFilter
) {
  'use strict';

  return Testing.package('query', [
    Testing.module('search_query', [
      Testing.test('copy', function(assert) {

        var contextFilter = AdvancedFilter.EqualsOp.create({
          lval: AdvancedFilter.Attr.create({ value: 'testAttr', type: String }),
          rval: AdvancedFilter.StringLiteral.create({ value: 'test' })
        });

        var searchQuery = SearchQuery.create({
          contextFilter: contextFilter
        });

        var copyOfQuery = searchQuery.copy();

        assert.strictEqual(copyOfQuery.get('uri'), searchQuery.get('uri'), 'Copy ContextFilter -> URIs should be equal');

        // Case that rval is a non-empty/not-null string
        assert.strictEqual(copyOfQuery.get('contextFilter.rval.value'), 'test', 'Copy ContextFilter -> FilterOp:(rval: "test") -> rval should be "test"');

        // Cases that rval is empty string ""
        contextFilter.set('rval', AdvancedFilter.EmptyStringConstant.create());
        copyOfQuery = searchQuery.copy();
        assert.strictEqual(copyOfQuery.get('uri'), searchQuery.get('uri'), 'Copy ContextFilter with EmptyStringConstant rval -> URIs should be equal');
        assert.strictEqual(copyOfQuery.get('contextFilter.rval.type'), AdvancedFilter.TYPE_STRING, 'Copy ContextFilter with EmptyStringConstant -> type should be TYPE_STRING');

        // Cases that rval is null
        contextFilter.set('rval', AdvancedFilter.NullConstant.create());
        copyOfQuery = searchQuery.copy();
        assert.strictEqual(copyOfQuery.get('uri'), searchQuery.get('uri'), 'Copy ContextFilter with NullConstant rval -> URIs should be equal');
        assert.equal(copyOfQuery.get('contextFilter.rval.name'), 'NULL', 'Copy ContextFilter with NullConstant rval -> name should be "NULL"');

        // Copy a query with sorting and grouping
        var queryWithSortAndGroup = SearchQuery.create({
          sort: [{ attr: 'foo', dir: 'asc' }, { attr: 'bar', dir: 'desc' }],
          group: 'foo bar'.w()
        });

        var copyOfQueryWithSortAndGroup = queryWithSortAndGroup.copy();

        assert.equal(queryWithSortAndGroup.get('uri'), copyOfQueryWithSortAndGroup.get('uri'), 'Copy sort and group -> URIs should be equal');
      }),

      Testing.test('uri', function(assert) {
        var emptyQuery = SearchQuery.create();

        var queryWithSort = SearchQuery.create({ sort: [{ attr: 'foo', dir: 'asc' }] });
        assert.notEqual(
          queryWithSort.get('uri'),
          emptyQuery.get('uri'),
          'Query with sort should have different URI from empty query'
        );

        var queryWithGroup = SearchQuery.create({ group: 'foo bar'.w() });
        assert.notEqual(
          queryWithGroup.get('uri'),
          emptyQuery.get('uri'),
          'Query with group should have different URI from empty query'
        );
      }),

      Testing.test('isCounter', function(assert) {
        var queryWithoutIsCounter = SearchQuery.create({ sort: [{ attr: 'foo', dir: 'asc' }] });
        var queryWithIsCounter = SearchQuery.create({ sort: [{ attr: 'foo', dir: 'asc' }], isCounter: true });

        assert.notEqual(
          queryWithoutIsCounter.get('uri'),
          queryWithIsCounter.get('uri'),
          'Changing isCounter should change the URI'
        );
      }),

      Testing.asyncTest('observableUri', function(assert, start) {
        var query = SearchQuery.create({
          searchAttr: 'foo',
          searchFilter: 'bar',
          observableUriUpdateDelayInMilliseconds: 10
        });

        assert.strictEqual(query.get('uri'), query.get('observableUri'), 'uri and observableUri are the same on init');

        query.addObserver('observableUri', this, function() {
          assert.strictEqual(
            query.get('uri'),
            query.get('observableUri'), 'uri and observableUri are the same after set'
          );

          start();
        });

        query.set('searchFilter', 'baz');
      })
    ])
  ]);
});
