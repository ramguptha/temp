define([
  'testing',
  'packages/platform/clearable-text-field-component'
], function(
  Testing,
  ClearableTextFieldComponent
) {
  'use strict';

  return Testing.package('platform/clearable-text-field-component', [
    Testing.module('main', [
      Testing.test('base', function(assert) {
        assert.ok(true, 'Parsing and deps');
      })
    ])
  ]);
});
