define([
  'testing',
  'packages/platform/button-group-component'
], function(
  Testing,
  ButtonGroupComponent
) {
  'use strict';

  return Testing.package('platform/button-group-component', [
    Testing.module('main', [
      Testing.test('base', function(assert) {
        assert.ok(true, 'Parsing and deps');
      })
    ])
  ]);
});
