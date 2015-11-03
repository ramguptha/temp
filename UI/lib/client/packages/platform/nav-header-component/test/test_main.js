define([
  'testing',
  'packages/platform/nav-header-component'
], function(
  Testing,
  NavHeaderComponent
) {
  'use strict';

  return Testing.package('platform/nav-header-component', [
    Testing.module('main', [
      Testing.test('base', function(assert) {
        assert.ok(true, 'Parsing and deps');
      })
    ])
  ]);
});
