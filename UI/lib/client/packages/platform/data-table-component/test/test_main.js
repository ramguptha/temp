define([
  'testing',
  'packages/platform/data-table-component'
], function(
  Testing,
  DataTableComponent
) {
  'use strict';

  return Testing.package('platform/data-table-component', [
    Testing.module('main', [
      Testing.test('base', function(assert) {
        assert.ok(true, 'Parsing and deps');
      })
    ])
  ]);
});
