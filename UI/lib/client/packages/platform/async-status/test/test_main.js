define([
  'testing',
  'packages/platform/async-status'
], function(
  Testing,
  AsyncStatus
) {
  'use strict';

  return Testing.package('platform/async-status', [
    Testing.module('main', [
      Testing.test('base', function(assert) {
        assert.ok(true, 'Parsing, deps');
      })
    ])
  ]);
});
