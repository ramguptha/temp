define([
  'testing',
  'packages/platform/selection'
], function(
  Testing,
  Selection
) {
  'use strict';

  return Testing.package('platform/selection', [
    Testing.module('main', [
      Testing.test('base', function(assert) {
        assert.ok(true, 'Parsing, deps');
      })
    ])
  ]);
});
