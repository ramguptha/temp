define([
  'testing',
  'packages/platform/session'
], function(
  Testing,
  Session
) {
  'use strict';

  return Testing.package('platform/session', [
    Testing.module('main', [
      Testing.test('base', function(assert) {
        assert.ok(true, 'Parsing and deps');
      })
    ])
  ]);
});
