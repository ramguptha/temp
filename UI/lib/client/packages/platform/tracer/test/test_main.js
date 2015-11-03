define([
  'testing',
  'packages/platform/tracer'
], function(
  Testing,
  Tracer
) {
  'use strict';

  return Testing.package('platform/tracer', [
    Testing.module('main', [
      Testing.test('base', function(assert) {
        assert.ok(true, 'parsing, deps');
      })
    ])
  ]);
});
