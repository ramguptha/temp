define([
  'testing',
  'packages/platform/counter'
], function(
  Testing,
  Counter
) {
  'use strict';

  return Testing.package('platform/counter', [
    Testing.module('main', [
      Testing.test('base', function(assert) {
        assert.ok(true, 'Parsing, deps');
      })
    ])
  ]);
});
