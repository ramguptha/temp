define([
  'testing',
  'packages/platform/paged-table-component'
], function(
  Testing,
  PagedTableComponent
) {
  'use strict';

  return Testing.package('platform/paged-table-component', [
    Testing.module('main', [
      Testing.test('base', function(assert) {
        assert.ok(true, 'Parsing, deps');
      })
    ])
  ]);
});
