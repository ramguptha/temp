define([
  'testing',
  'packages/platform/data-list-component'
], function(
  Testing,
  DataListComponent
) {
  'use strict';

  return Testing.package('platform/data-list-component', [
    Testing.module('main', [
      Testing.test('base', function(assert) {
        assert.ok(true, 'Parsing, deps');
      })
    ])
  ]);
});
