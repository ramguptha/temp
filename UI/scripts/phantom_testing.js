// Phantomjs console unit test util
//
// TODOS
// * Detect if the server is already running
// * Auto start/stop server
'use strict';

var qunit = require('node-qunit-phantomjs');
var shell = require('shelljs');
var ccProxyPort = process.env.PORT || 3000;
var testUrl = 'http://localhost:' + ccProxyPort + '/dev/tests/index.html';

console.log('Phantomjs start testing and good luck ^_^ ...');
setTimeout(function () {
    qunit(testUrl, {
        verbose: false
    }, function(code) {
        shell.exit(code)
    });
}, 500);
