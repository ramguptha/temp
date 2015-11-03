define([
  'testing',
  'packages/platform/guid'
], function(
  Testing,
  Guid
) {
  'use strict';

  return Testing.package('guid', [
    Testing.module('main', [
      Testing.test('generate', function(assert) {
        for (var i = 0; i < 10; i++) {
          var guid = Guid.generate();
          assert.strictEqual(Guid.isValid(guid), true, 'generate should create valid guids');
        }
      }),

      Testing.test('isValid', function(assert) {
        assert.strictEqual(Guid.isValid(undefined), false, 'type');
        assert.strictEqual(Guid.isValid(null), false, 'type');
        assert.strictEqual(Guid.isValid(1), false, 'type');
        assert.strictEqual(Guid.isValid({}), false, 'type');

        assert.strictEqual(Guid.isValid('foo'), false, 'format');

        var guid = '3F2504E0-4F89-11D3-9A0C-0305E82C3301';
        assert.strictEqual(Guid.isValid(guid), true, 'valid');
        assert.strictEqual(Guid.isValid(guid.toLowerCase()), true, 'valid');

        assert.strictEqual(Guid.isValid(guid.replace('-', ' ')), false, 'not quite valid');
        assert.strictEqual(Guid.isValid(guid + 'a'), false, 'not quite valid');
      })
    ])
  ]);
});

