define([
  'testing',
  'packages/platform/queried-counter'
], function(
  Testing,
  QueriedCounter
) {
  'use strict';

  return Testing.package('platform/queried-counter', [
    Testing.module('main', [
      Testing.test('base', function(assert) {
        assert.ok(true, 'Parsing and deps');
      })
    ])
  ]);
});
