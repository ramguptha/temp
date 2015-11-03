define([
  'testing',
  'packages/platform/enum-util'
], function(
  Testing,
  EnumUtil
) {
  'use strict';

  return Testing.package('EnumUtil', [
    Testing.module('main', [
      Testing.test('exclude', function(assert) {
        var inputArray = [];

        inputArray = [2,3];
        assert.equal(EnumUtil.exclude(inputArray, null, null, null), inputArray, 'Input: [2,3], Exclude: null, expected: [2,3]');
        assert.deepEqual(EnumUtil.exclude(inputArray, null, [], null), inputArray, 'Input: [2,3], Exclude: [], expected: [2,3]');
        assert.deepEqual(EnumUtil.exclude(inputArray, null, [2], null), [3], 'Input: [2,3], Exclude: [2], expected: [3]');

        inputArray = [{ id: 2 }, { id: 3 }];
        assert.deepEqual(EnumUtil.exclude(inputArray, null, null, null), inputArray, 'Input: [{ id: 2 }, { id: 3 }], Exclude: null, expected: [{ id: 2 }, { id: 3 }]');
        assert.deepEqual(EnumUtil.exclude(inputArray, null, [{ id: 2 }], null), [], 'Input: [{ id: 2 }, { id: 3 }], Exclude: [{ id: 2 }], expected: [] -> when mapper is NULL');
        assert.deepEqual(EnumUtil.exclude(inputArray, 'id', [{ id: 2 }], 'id'), [{ id: 3 }], 'Input: [{ id: 2 }, { id: 3 }], Exclude: [{ id: 2 }], expected: [{ id: 3 }] -> when mapper is "id"');
      }),

      Testing.test('intersect', function(assert) {
        var inputArray = [];

        inputArray = [2,3];
        assert.equal(EnumUtil.intersect(inputArray, null, null, null), inputArray, 'Input: [2,3], Intersect: null, expected: [2,3]');
        assert.deepEqual(EnumUtil.intersect(inputArray, null, [], null), [], 'Input: [2,3], Intersect: [], expected: []');
        assert.deepEqual(EnumUtil.intersect(inputArray, null, [2], null), [2], 'Input: [2,3], Intersect: [2], expected: [2]');

        inputArray = [{ id: 2 }, { id: 3 }];
        assert.deepEqual(EnumUtil.intersect(inputArray, null, null, null), inputArray, 'Input: [{ id: 2 }, { id: 3 }], Intersect: null, expected: [{ id: 2 }, { id: 3 }]');
        assert.deepEqual(EnumUtil.intersect(inputArray, 'id', [{ id: 2 }], 'id'), [{ id: 2 }], 'Input: [{ id: 2 }, { id: 3 }], Intersect: [{ id: 2 }], expected: [{ id: 2 }] -> when mapper is "id"');
      })
    ])
  ]);
});


