define([
  'testing',
  'packages/platform/ip-util'
], function(
  Testing,
  IPUtil
) {
  'use strict';

  return Testing.package('ip-util', [
    Testing.module('main', [
      Testing.test('ip validity', function(assert) {

        // positive testing;
        assert.equal(IPUtil.isValid('0.0.0.0'), true, '0.0.0.0 is valid IP');
        assert.equal(IPUtil.isValid('8.8.8.8'), true, '8.8.8.8 is valid IP');
        assert.equal(IPUtil.isValid('255.255.255.255'), true, '255.255.255.255 is valid IP');

        // negative testing;
        assert.equal(IPUtil.isValid(new Date()), false, 'Object is not a valid IP');
        assert.equal(IPUtil.isValid(function() {}), false, 'Function is not a valid IP');
        assert.equal(IPUtil.isValid(NaN), false, 'NaN is not a valid IP');
        assert.equal(IPUtil.isValid(0), false, '0 is not a valid IP');
        assert.equal(IPUtil.isValid(null), false, 'null is not a valid IP');
        assert.equal(IPUtil.isValid("0.0.0,0"), false, '0.0.0,0 is not a valid IP (coma used)');
        assert.equal(IPUtil.isValid("-1.0.0.0"), false, '-1.0.0.0 is not a valid IP');
        assert.equal(IPUtil.isValid("256.325.576.1"), false, '256.325.576.1 is not a valid IP');
      }),

      Testing.test('ip to int', function(assert) {
        // positive testing;
        assert.equal(IPUtil.IPv4StringToInt('0.0.0.0'), 0, 'Min boundary for IP');
        assert.equal(IPUtil.IPv4StringToInt('0.0.2.0'), 512, 'Normal conversion for IP');
        assert.equal(IPUtil.IPv4StringToInt('255.255.255.255'), 4294967295, 'Max boundary for IP');

        // negative testing;
        assert.equal(IPUtil.IPv4StringToInt(new Object()), null, 'Null if try to parse object');
        assert.equal(IPUtil.IPv4StringToInt(1234), null, 'Null if try to parse number');
        assert.equal(IPUtil.IPv4StringToInt('-255.255.255.255'), null, 'Null if try to parse incorrect IP');
      }),

      Testing.test('int to ip', function(assert) {
        // positive testing;
        assert.equal(IPUtil.IPv4IntToString(0), '0.0.0.0', 'Min boundary for IP');
        assert.equal(IPUtil.IPv4IntToString(512), '0.0.2.0', 'Normal conversion for IP');
        assert.equal(IPUtil.IPv4IntToString(4294967295), '255.255.255.255', 'Max boundary for IP');

        // negative testing;
        assert.equal(IPUtil.IPv4IntToString(new Object()), null, 'Null if try to parse NaN');
        assert.equal(IPUtil.IPv4IntToString(42949672954294967295), null, 'Null if int is too big');
      })
    ])
  ]);
});
