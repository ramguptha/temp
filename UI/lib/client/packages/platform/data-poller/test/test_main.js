define([
  'testing',
  'packages/platform/data-poller'
], function(
  Testing,
  DataPoller
) {
  'use strict';

  return Testing.package('platform/data-poller', [
    Testing.module('main', [
      Testing.test('base', function(assert) {
        assert.ok(true, 'Parsing, deps');
      })
    ])
  ]);
});
