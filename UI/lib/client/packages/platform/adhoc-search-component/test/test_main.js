define([
  'testing',
  'packages/platform/adhoc-search-component'
], function(
  Testing,
  AdhocSearchComponent
) {
  'use strict';

  return Testing.package('platform/adhoc-search-component', [
    Testing.module('main', [
      Testing.test('base', function(assert) {
        assert.ok(true, 'Parsing and deps');
      })
    ])
  ]);
});
