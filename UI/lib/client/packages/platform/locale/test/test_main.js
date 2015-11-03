define([
  'ember',
  'handlebars',
  'testing',
  '../lib/resolve',
  '../lib/render'
], function(
  Em,
  Handlebars,
  Testing,
  resolve,
  render
) {
  'use strict';

  return Testing.package('locale', [
    Testing.module('main', [
      Testing.test('resolve', function(assert) {
        var strings = {
          shared: {
            foo: 'bar',
            bar: { ref: 'shared.foo' },
            baz: { ref: 'broken.path' }
          }
        };

        assert.strictEqual(resolve(strings, 'shared.foo'), strings.shared.foo, 'Basic lookup');
        assert.strictEqual(resolve(strings, 'shared.bar'), strings.shared.foo, 'Ref lookup');
        assert.strictEqual(resolve(strings, 'shared.foo', true), 'shared.foo', 'Lookup while localizing');

        assert.strictEqual(resolve(strings, 'foo'), undefined, 'Missing path, 1 slug');
        assert.strictEqual(resolve(strings, 'foo.bar'), undefined, 'Missing path, > 1 slug');
        assert.strictEqual(resolve(strings, 'shared.baz'), undefined, 'Missing ref, > 1 slug');
      }),

      Testing.test('render', function(assert) {
        var handlebarsTemplate = Handlebars.compile('foo {{bar}} <em>baz</em>');
        var stringTemplate = '<em>foo</em>';

        assert.strictEqual(
          render(stringTemplate).toString(),
          stringTemplate,
          'String template is simply returned as SafeString, no context specified'
        );

        assert.strictEqual(
          render(handlebarsTemplate, { bar: 'zzz' }).toString(),
          'foo zzz <em>baz</em>',
          'Handlebars template is rendered with context'
        );
      })
    ])
  ]);
});
