define([
  'testing',
  'packages/platform/context-menu-component'
], function(
  Testing,
  ContextMenuComponent
) {
  'use strict';

  return Testing.package('platform/context-menu-component', [
    Testing.module('main', [
      Testing.test('base', function(assert) {
        assert.ok(true, 'Parsing and deps');
      })
    ])
  ]);
});
