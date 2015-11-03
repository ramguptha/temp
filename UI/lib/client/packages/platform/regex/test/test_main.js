define([
  'testing',
  'packages/platform/regex'
], function(
    Testing,
    Regex
    ) {
  'use strict';

  return Testing.package('regex', [
    Testing.module('main', [
      Testing.test('esc', function(assert) {
        assert.equal(Regex.esc('!@#$%^&*Hello, $&~ld456456'), "\\!@\\#\\$%\\^&\\*Hello\\, \\$&~ld456456", 'The characters are properly escaped');
        assert.equal(Regex.esc('\\(){}'), "\\\\\\(\\)\\{\\}", 'The characters are properly escaped');
        assert.equal(/Hello/.test('!@#\\$%\\^&\\*Hello, \\$&~ld456456'), true, 'The regex is working properly');
      })
    ])
  ]);
});