define([
  'testing',
  'packages/platform/count-status-component'
], function(
  Testing,
  CountStatusComponent
) {
  'use strict';

  return Testing.package('platform/count-status-component', [
    Testing.module('main', [
      Testing.test('base', function(assert) {
        assert.ok(true, 'Parsing and deps');
      })
    ])
  ]);
});
