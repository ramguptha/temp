define([
  'testing',
  'packages/platform/send-ember-action'
], function(
  Testing,
  sendEmberAction
) {
  'use strict';

  return Testing.package('platform/send-ember-action', [
    Testing.module('main', [
      Testing.test('base', function(assert) {
        assert.ok(true, 'Parsing and deps');
      })
    ])
  ]);
});
