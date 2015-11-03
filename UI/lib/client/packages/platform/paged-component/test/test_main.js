define([
  'testing',
  'packages/platform/paged-component'
], function(
  Testing,
  PagedComponent
) {
  'use strict';

  return Testing.package('platform/paged-component', [
    Testing.module('main', [
      Testing.test('domIdMapper', function(assert) {
        var domIdMapper = PagedComponent.DomIdMapper.create();

        assert.ok(domIdMapper.validateId('f0501fec-17c9-43ef-91bf-49f9f255daff'), 'Guids are valid');
        assert.ok(domIdMapper.validateId('2BCB6F198SAA00080007'), 'ESNs are valid');
        assert.ok(domIdMapper.validateId('aA0_.+=|\\/:'), 'Representative of all valid character classes');

        var notableInvalidCharacters = '<>(){}[]&!@#$%^&*"\'';
        for (var i = 0; i < notableInvalidCharacters.length; i++) {
          var ch = notableInvalidCharacters[i];
          assert.strictEqual(domIdMapper.validateId(ch), false, 'Notable invalid characters: ' + ch);
        }

        [null, undefined, true, false, 0, 1, /./].forEach(function(value) {
          assert.strictEqual(domIdMapper.validateId(value), false, 'Notable invalid values: ' + value);
        });

        assert.strictEqual(domIdMapper.validateId('<b>foo</b>'), false, 'No HTML');

        assert.throws(function() { domIdMapper.validateId('>', true); }, 'Throws in strict mode');
        assert.throws(function() { domIdMapper.validateId('>', true, 'unit test'); }, 'Throws in strict mode');
      })
    ])
  ]);
});
