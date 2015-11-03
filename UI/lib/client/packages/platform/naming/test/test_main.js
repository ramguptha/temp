define([
  'testing',
  'packages/platform/naming'
], function(
  Testing,
  Naming
) {
  'use strict';

  return Testing.package('naming', [
    Testing.module('main', [
      Testing.test('annotate', function(assert) {
        assert.equal(Naming.annotate('test', ' - copy', 12), 'test - copy', 'The length of the original text is lower than the max size.');
        assert.equal(Naming.annotate('test test', ' - copy', 12), 'test  - copy', 'The length of the original text is more than the max size.');
        assert.equal(Naming.annotate('test    ', ' - copy', 12), 'test - copy', 'The original string has trailing spaces');
        assert.equal(Naming.annotate('  test', ' - copy', 12), 'test - copy', 'The original string has trailing spaces at the beginning');
      })
    ])
  ]);
});


