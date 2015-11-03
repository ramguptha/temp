define([
  'testing',
  'packages/platform/button-group-horizontal-component'
], function(
  Testing,
  ButtonGroupHorizontalComponent
) {
  'use strict';

  return Testing.package('platform/button-group-horizontal-component', [
    Testing.module('main', [
      Testing.test('base', function(assert) {
        assert.ok(true, 'Parsing and deps');
      })
    ])
  ]);
});
