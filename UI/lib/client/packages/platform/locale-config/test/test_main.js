define([
  'testing',
  'packages/platform/locale-config'
], function(
  Testing,
  LocaleConfig
) {
  'use strict';

  return Testing.package('locale-config', [
    Testing.module('main', [
      Testing.test('nearestSupportedLocale', function(assert) {
        assert.strictEqual(LocaleConfig.nearestSupportedLocale('ja', ['ja-jp']), 'ja-jp');
        assert.strictEqual(
          LocaleConfig.nearestSupportedLocale(
            'en-us',
            ['en_US'],
            function(localeCode) { return localeCode.replace('_', '-').toLowerCase(); }),
          'en_US'
        );
      })
    ])
  ]);
});
