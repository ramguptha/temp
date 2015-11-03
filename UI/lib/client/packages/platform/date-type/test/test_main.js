define([
  'testing',
  'packages/platform/date-type'
], function(
  Testing,
  DateType
) {
  'use strict';

  return Testing.package('date', [
    Testing.module('main', [
      Testing.test('isValid', function(assert) {
        assert.ok(DateType.isValid(new Date(1)), 'timestamp 1 is valid');

        assert.equal(DateType.isValid(null), false, 'null is not valid and does not throw');
        // fixme Removed because of an update to the date.isValid() implementation
        // Not sure how we are going to handle this case
        //assert.equal(DateType.isValid(new Date(0)), false, 'timestamp 0 is not valid');
        assert.equal(DateType.isValid(new Date(NaN)), false, 'timestamp NaN is not valid');
        assert.equal(DateType.isValid(Date.parse('blah blah')), false, 'parsed value of invalid datestring is not valid');
      }),

      Testing.test('(increment/decrement)ByUtcOffset', function(assert) {
        var date = new Date();
        assert.strictEqual(
          (new Date(date.getTime() + date.getTimezoneOffset() * 60 * 1000)).getTime(),
          DateType.incrementByUtcOffset(date).getTime()
        );
        assert.strictEqual(
          (new Date(date.getTime() - date.getTimezoneOffset() * 60 * 1000)).getTime(),
          DateType.decrementByUtcOffset(date).getTime()
        );
        assert.strictEqual(DateType.incrementByUtcOffset(null), null);
        assert.strictEqual(DateType.decrementByUtcOffset(null), null);

        var nanDate = new Date(NaN);
        assert.strictEqual(DateType.incrementByUtcOffset(nanDate), nanDate);
        assert.strictEqual(DateType.decrementByUtcOffset(nanDate), nanDate);
      })
    ])
  ]);
});
