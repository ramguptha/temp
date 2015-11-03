define([
  'testing',
  'packages/platform/is-data-presenter'
], function(
  Testing,
  IsDataPresenter
) {
  'use strict';

  return Testing.package('platform/is-data-presenter', [
    Testing.module('main', [
      Testing.test('base', function(assert) {
        assert.ok(true, 'Parsing and deps');
      })
    ])
  ]);
});
