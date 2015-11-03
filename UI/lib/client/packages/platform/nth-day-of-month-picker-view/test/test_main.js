define([
  'testing',
  'packages/platform/nth-day-of-month-picker-view'
], function(
  Testing,
  NthDayOfMonthPickerView
) {
  'use strict';

  return Testing.package('platform/nth-day-of-month-picker-view', [
    Testing.module('main', [
      Testing.test('base', function(assert) {
        assert.ok(true, 'Parsing, deps');
      })
    ])
  ]);
});
