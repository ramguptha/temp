define([
  'testing',
  'packages/platform/formatter'
], function(
  Testing,
  Formatter
) {
  'use strict';

  return Testing.package('formatter', [
    Testing.module('main', [

      Testing.test('formatBoolean', function(assert) {
        assert.equal(Formatter.formatBoolean(), 'shared.false', 'No argument returns "shared.false"');
        assert.equal(Formatter.formatBoolean(''), 'shared.false', 'Empty string returns "shared.false"');
        assert.equal(Formatter.formatBoolean(true), 'shared.true', 'true returns "shared.true"');
        assert.equal(Formatter.formatBoolean(false), 'shared.false', 'false returns "shared.false"');
        assert.equal(Formatter.formatBoolean(256), 'shared.true', 'Number returns "shared.true"');
        assert.equal(Formatter.formatBoolean('ABC'), 'shared.true', 'Non-empty string returns "shared.true"');
      }),

      Testing.test('formatBooleanOrNA', function(assert) {
        assert.equal(Formatter.formatBooleanOrNA(), 'shared.baseline', 'No argument returns "shared.baseline"');
        assert.equal(Formatter.formatBooleanOrNA(''), 'shared.false', 'Empty string returns "shared.false"');
        assert.equal(Formatter.formatBooleanOrNA(true), 'shared.true', 'true returns "shared.true"');
        assert.equal(Formatter.formatBooleanOrNA(false), 'shared.false', 'false returns "shared.false"');
        assert.equal(Formatter.formatBooleanOrNA(256), 'shared.true', 'Number returns "shared.true"');
        assert.equal(Formatter.formatBooleanOrNA('ABC'), 'shared.true', 'Non-empty string returns "shared.true"');
      }),

      Testing.test('formatNumberToBooleanOrNA', function(assert) {
        assert.equal(Formatter.formatNumberToBooleanOrNA(), 'shared.baseline', 'No argument returns "shared.baseline"');
        assert.equal(Formatter.formatNumberToBooleanOrNA(''), 'shared.false', 'Empty string returns "shared.false"');
        assert.equal(Formatter.formatNumberToBooleanOrNA(undefined), 'shared.baseline', 'undefined returns "shared.baseline"');
        assert.equal(Formatter.formatNumberToBooleanOrNA(256), 'shared.true', 'Number returns "shared.true"');
        assert.equal(Formatter.formatNumberToBooleanOrNA('256'), 'shared.true', 'Number as a string returns "shared.true"');
        assert.equal(Formatter.formatNumberToBooleanOrNA('ABC'), 'shared.false', 'Non-empty string returns "shared.false"');
      }),

      Testing.test('toStringOrNA', function(assert) {
        assert.equal(Formatter.toStringOrNA(''), 'shared.baseline', 'An empty string is passed to the function');
        assert.equal(Formatter.toStringOrNA('    '), 'shared.baseline', 'A string with only white spaces is passed to the function');
        assert.equal(Formatter.toStringOrNA('test'), 'test', 'A string is passed to the function');
      }),

      Testing.test('formatDate', function(assert) {
        assert.equal(Formatter.formatDate(), 'shared.baseline', 'No argument returns "shared.baseline"');
        assert.equal(Formatter.formatDate(new Date(0)), 'shared.baseline', '0ms returns "shared.baseline"');
        assert.equal(Formatter.formatDate(new Date(1)), 'Jan 1, 1970', '1ms returns "Jan 1, 1970');
        assert.equal(Formatter.formatDate(new Date(100000000000)), 'Mar 3, 1973', '100000000000ms returns "Mar 3, 1973"');
      }),

//      Testing.test('formatShortDate', function(assert) {
//        assert.equal(Formatter.formatShortDate(), '--', 'No argument returns "--"');
//        assert.equal(Formatter.formatShortDate(new Date(0)), '--', '0ms returns "--"');
//        assert.equal(Formatter.formatShortDate(new Date(1)), 'Wednesday, December 31, 1969', '1ms returns "Wednesday, December 31, 1969"');
//        assert.equal(Formatter.formatShortDate(new Date(100000000000)), 'Saturday, March 3, 1973', '100000000000ms returns "Saturday, March 3, 1973"');
//      }),
//
//      Testing.test('formatTime', function(assert) {
//        assert.equal(Formatter.formatTime(), '--', 'No argument returns "--"');
//        assert.equal(Formatter.formatTime(new Date(0)), '--', '0ms returns "--"');
//        assert.equal(Formatter.formatTime(new Date(1)), 'Wednesday, December 31, 1969', '1ms returns "Wednesday, December 31, 1969"');
//        assert.equal(Formatter.formatTime(new Date(100000000000)), 'Saturday, March 3, 1973', '100000000000ms returns "Saturday, March 3, 1973"');
//      }),

      Testing.test('formatOSVersion', function(assert) {
        assert.equal(Formatter.formatOSVersion(), 'shared.baseline', 'No argument returns "shared.baseline"');
        assert.equal(Formatter.formatOSVersion(25), '0.0', '25 returns "0.0"');
        assert.equal(Formatter.formatOSVersion(1000000), '0.0.15', '1000000 returns "0.0.15"');
        assert.equal(Formatter.formatOSVersion(10000000), '0.9.8', '10000000 returns "0.9.8"');
      }),

      Testing.test('formatPercent', function(assert) {
        assert.equal(Formatter.formatPercent(), 'shared.baseline', 'No argument returns "shared.baseline"');
        assert.equal(Formatter.formatPercent(50), '50%', '50 returns "50%"');
      }),

//      Testing.test('formatChangeType', function(assert) {
//        assert.equal(Formatter.formatChangeType(0), '<span data-tooltip-attr="title" data-sticky-tooltip="true" title="New item added" class="icon-item-new icon-font-size-m color-primary-icon"></span>', '0 argument returns ""');
//      }),

      Testing.test('toUTC8601String', function(assert) {
        assert.equal(Formatter.toUTC8601String(new Date(0)), '1970-01-01T00:00:00Z', '1ms returns 1970-01-01T00:00:00Z');
        assert.equal(Formatter.toUTC8601String(new Date(1)), '1970-01-01T00:00:00Z', '1ms returns "1970-01-01T00:00:00Z"');
        assert.equal(Formatter.toUTC8601String(new Date(100000000000)), '1973-03-03T09:46:40Z', '100000000000ms returns "1973-03-03T09:46:40Z"');
      }),

      Testing.test('camelCaseToTitleCase', function(assert) {
        assert.equal(Formatter.camelCaseToTitleCase('test'), 'Test', 'test -> Test');
        assert.equal(Formatter.camelCaseToTitleCase('testOne'), 'Test One', 'testOne -> Test One');
        assert.equal(Formatter.camelCaseToTitleCase('testone'), 'Testone', 'testone -> Testone');
        assert.equal(Formatter.camelCaseToTitleCase('test-one'), 'Test One', 'test-one -> Test One');
      }),

      Testing.test('acronymsCapitalized', function(assert) {
        assert.equal(Formatter.acronymsCapitalized('Cpu'), 'CPU', 'Cpu -> CPU');
        assert.equal(Formatter.acronymsCapitalized('Cpus'), 'CPUs', 'Cpus -> CPUs');
        assert.equal(Formatter.acronymsCapitalized('cpu_inside_of_word'), 'cpu_inside_of_word', "cpu_inside_of_word -> didn't capitalized");
        assert.equal(Formatter.acronymsCapitalized('os'), 'OS', 'os -> OS');
        assert.equal(Formatter.acronymsCapitalized('bios'), 'bios', "bios -> 'os' inside of the word didn't capitalized");
        assert.equal(Formatter.acronymsCapitalized('utc'), '(UTC)', 'utc -> (UTC)');
      }),

      Testing.test('parseHoursMinutes', function(assert) {
        assert.deepEqual(Formatter.parseHoursMinutes('04:07'), { hours: 4, minutes: 7 });
        assert.deepEqual(Formatter.parseHoursMinutes('12:13'), { hours: 12, minutes: 13 });
        assert.deepEqual(Formatter.parseHoursMinutes('-789:999'), { hours: -789, minutes: 999 });
        assert.strictEqual(Formatter.parseHoursMinutes(':1'), null);
        assert.strictEqual(Formatter.parseHoursMinutes('1:'), null);
        assert.strictEqual(Formatter.parseHoursMinutes(':'), null);
        assert.strictEqual(Formatter.parseHoursMinutes(null), null);
      })
    ])
  ]);
});


