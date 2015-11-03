define([
  'testing',
  'packages/platform/child-controller'
], function(
  Testing,
  ChildController
) {
  'use strict';

  return Testing.package('platform/child-controller', [
    Testing.module('main', [
      Testing.test('base', function(assert) {
        assert.ok(true, 'Parsing, deps');
      })
    ])
  ]);
});
