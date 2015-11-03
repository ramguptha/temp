define([
  'testing',
  'packages/platform/cookie'
], function(
  Testing,
  Cookie
) {
  'use strict';

  var key = 'UNIT_TESTING_KEY';
  var value = 'UNIT_TESTING_VALUE';

  return Testing.package('cookie', [
    Testing.module('main', [
      Testing.asyncTest('Read, Write, Clear', function(assert, start) {
        assert.equal(Cookie.write(key, value), value, 'Cookie.write should return what was written.');
        assert.equal(Cookie.read(key), value, 'Cookie.read should return what was written.');
        assert.equal(Cookie.clear(key), key, 'Cookie.clear should return the cleared key.');
        window.setTimeout(function() {
          start();
          assert.equal(Cookie.read(key), undefined, 'Cookie.read should no longer return what was written.');
        }, 2);
      })
    ])
  ]);
});

