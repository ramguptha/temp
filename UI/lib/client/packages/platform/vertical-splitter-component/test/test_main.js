define([
  'testing',
  'packages/platform/vertical-splitter-component'
], function(
  Testing,
  VerticalSplitterComponent
) {
  'use strict';

  return Testing.package('platform/vertical-splitter-component', [
    Testing.module('main', [
      Testing.test('base', function(assert) {
        assert.ok(true, 'Parsing and deps');
      })
    ])
  ]);
});
