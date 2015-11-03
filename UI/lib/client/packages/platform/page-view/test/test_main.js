define([
  'testing',
  'packages/platform/page-view'
], function(
  Testing,
  PageView
) {
  'use strict';

  return Testing.package('platform/page-view', [
    Testing.module('main', [
      Testing.test('base', function(assert) {
        assert.ok(true, 'Parsing, deps');
      })
    ])
  ]);
});
