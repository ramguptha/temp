define([
  'testing',
  'packages/platform/offset-monitor'
], function(
  Testing,
  OffsetMonitor
) {
  'use strict';

  return Testing.package('platform/offset-monitor', [
    Testing.module('main', [
      Testing.test('base', function(assert) {
        assert.ok(true, 'Parsing, deps');
      })
    ])
  ]);
});

