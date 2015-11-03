define([
  'testing',
  'packages/platform/nav-page-view'
], function(
  Testing,
  NavPageView
) {
  'use strict';

  return Testing.package('platform/nav-page-view', [
    Testing.module('main', [
      Testing.test('base', function(assert) {
        assert.ok(true, 'Parsing, deps');
      })
    ])
  ]);
});
