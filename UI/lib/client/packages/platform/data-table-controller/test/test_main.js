define([
  'testing',
  'packages/platform/data-table-controller'
], function(
  Testing,
  DataTableController
) {
  'use strict';

  return Testing.package('platform/data-table-controller', [
    Testing.module('main', [
      Testing.test('base', function(assert) {
        assert.ok(true, 'Parsing and deps');
      })
    ])
  ]);
});
