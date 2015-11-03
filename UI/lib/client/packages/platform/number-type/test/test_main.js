define([
  'testing',
  'packages/platform/number-type'
], function(
  Testing,
  NumberType
) {
  'use strict';

  return Testing.package('number-type', [
    Testing.module('main', [
      Testing.test('isValid', function(assert) {
        assert.equal(NumberType.isValid(1), true, '1 is Number');
        assert.equal(NumberType.isValid(-1), true, '-1 is Number');
        assert.equal(NumberType.isValid("1"), false, 'String is not a valid number');
        assert.equal(NumberType.isValid({}), false, 'Object is not a valid number');
        assert.equal(NumberType.isValid(true), false, 'Boolean is not a valid number');
        assert.equal(NumberType.isValid(function(){}), false, 'Function is not a valid number');
      })
    ])
  ]);
});


