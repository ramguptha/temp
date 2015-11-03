define([
  'testing',
  'packages/platform/collection-header-component'
], function(
  Testing,
  CollectionHeaderComponent
) {
  'use strict';

  return Testing.package('platform/collection-header-component', [
    Testing.module('main', [
      Testing.test('base', function(assert) {
        assert.ok(true, 'Parsing and deps');
      })
    ])
  ]);
});
