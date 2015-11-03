define([
  'testing',
  'packages/platform/storage'
], function (Testing, Store) {
  'use strict';

  var key = 'UNIT_TESTING_KEY';
  var value = 'UNIT_TESTING_VALUE';

  return Testing.package('storage', [
    Testing.module('main', [
      Testing.asyncTest('Read, Write, Clear', function (assert, start) {
        assert.equal(Store.write(key, value), value, 'Store.write should return what was written.');
        assert.equal(Store.read(key), value, 'Store.read should return what was written.');
        assert.equal(Store.clear(key), key, 'Store.clear should return the cleared key.');
      })
    ])
  ]);
});