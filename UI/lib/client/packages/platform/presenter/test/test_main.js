define([
  'testing',
  'packages/platform/presenter'
], function(
  Testing,
  Presenter
) {
  'use strict';

  return Testing.package('platform/presenter', [
    Testing.module('main', [
      Testing.test('base', function(assert) {
        assert.ok(true, 'Parsing and deps');
      })
    ])
  ]);
});
