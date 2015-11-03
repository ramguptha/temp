define([
  'testing',
  'packages/platform/data-counter'
], function(
  Testing,
  DataCounter
) {
  'use strict';

  return Testing.package('platform/data-counter', [
    Testing.module('main', [
      Testing.test('base', function(assert) {
        assert.ok(true, 'Parsing and deps');
      })
    ])
  ]);
});
