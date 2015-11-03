var testrunner = require('qunit');

testrunner.run({
  code: { path: './lib/server/locale_util.js', namespace: 'localeUtil' },
  tests: './tests/server/test_locale_util.js'
}, function(err, report) {});
