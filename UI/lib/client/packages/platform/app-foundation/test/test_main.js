define([
  'testing',
  'packages/platform/app-foundation'
], function(
  Testing,
  AppFoundation
) {
  'use strict';

  return Testing.package('platform/app-foundation', [
    Testing.module('main', [
      Testing.test('base', function(assert) {
        assert.ok(true, 'Parsing, deps');
      })
    ])
  ]);
});
