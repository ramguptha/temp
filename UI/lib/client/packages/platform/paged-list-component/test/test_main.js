define([
  'testing',
  'packages/platform/paged-list-component'
], function(
  Testing,
  PagedListComponent
) {
  'use strict';

  return Testing.package('platform/paged-list-component', [
    Testing.module('main', [
      Testing.test('base', function(assert) {
        assert.ok(true, 'Parsing, deps');
      })
    ])
  ]);
});
