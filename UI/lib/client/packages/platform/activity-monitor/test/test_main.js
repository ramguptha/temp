define([
  'testing',
  'packages/platform/activity-monitor'
], function(
  Testing,
  ActivityMonitor
) {
  'use strict';

  return Testing.package('platform/activity-monitor', [
    Testing.module('main', [
      Testing.test('base', function(assert) {
        assert.ok('true', 'Parsing and deps');
      })
    ])
  ]);
});
