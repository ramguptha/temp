// The Absolute Manage Development Server
// ======================================
//
// A standard app server, that serves the dev/* endpoints only.

'use strict';

// Dependencies
// ------------

var express = require('express'),
  consolidate = require('consolidate'),
  httpProxy = require('http-proxy'),
  optimist = require('optimist'),
  devUtil = require('../../lib/server/dev_util');

// Go!
// ----

// default environment is dev
if (undefined === process.env.NODE_ENV) {
  process.env.NODE_ENV = 'development';
}

optimist = optimist
  .usage('Start a local app server.')
  .options('p', {
    describe: 'Use alternative port',
    alias: 'port'
  })
  .options('h', {
    describe: 'Print this message',
    alias: 'help'
  });

if (optimist.argv.h) {
  optimist.showHelp();
  return;
} else {
  var argv = optimist.argv;
}

var app = express();
app.engine('handlebars', consolidate.handlebars);

app.get('/', function(req, res) {
  res.redirect('/dev/docs');
});

app.get('/dev', function(req, res) {
  res.redirect('/dev/docs');
});

// Logger
app.use(express.logger('dev'));

devUtil.configureDevEnvironment(app);

app.use(express.errorHandler({
  dumpExceptions: true, 
  showStack: true
}));

// HTTP
var port = process.env.PORT || 3000;

if(argv.p !== undefined) {
  port = argv.p; //if --p parameter was passed overwrite the default port
}
app.listen(port);

console.log('Http server listening on port ' + port);

