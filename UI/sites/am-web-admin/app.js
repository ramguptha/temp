// The Absolute Manage Development Server
// ======================================
//
// A standard app server, serving the client directory merged with the lib/client directory.
// Requests to /com.absolute.am.webapi/* are proxied to a development server.

'use strict';

// Dependencies
// ------------

var express = require('express'),
  consolidate = require('consolidate'),
  httpProxy = require('http-proxy'),
  optimist = require('optimist'),
  devUtil = require('../../lib/server/dev_util');

// Middleware
// ----------

var debug = false;

var apiDispatcher = function(host, port, useHttps, pathMatcher) {
  console.log('API: ' + host);

  var proxy = new httpProxy.createProxyServer({
    target: (useHttps ? 'https://' : 'http://') + host + (port ? ':' + port : ''),
    headers: { host: host }
  });

  if (debug) {
    proxy.on('start', function(request, result, response) {
      console.log('START');
      console.log();
      console.log(request);
      console.log();
    });
    proxy.on('forward', function(request, result, response) {
      console.log('FORWARD');
    });
    proxy.on('end', function(error, request, result) {
      console.log('END');
    });
  }

  var listenerKey = host + ':' + port;
  var configuredResponseListeners = {};

  return function(request, result, next) {
    if (!pathMatcher(request.path)) {
      next();
    } else {
      // Else proxy it to the AM environment
      proxy.web(request, result);

      if (debug && !configuredResponseListeners[listenerKey]) {
        proxy.proxies[listenerKey].on('proxyResponse', function(request, result, response) {
          console.log('RESPONSE');
        });
        proxy.proxies[listenerKey].on('proxyError', function(error, request, result) {
          console.log('ERROR');
          console.log(error);
        });
        configuredResponseListeners[listenerKey] = true;
      }
    }
  };
};

// Go!
// ----

// default environment is dev
if (undefined === process.env.NODE_ENV) {
  process.env.NODE_ENV = 'development';
}

//FYI: optimist is a cmdln parameter parsing library.
//to start the app with the alternate packages use:
//node app --pl='../cc-old/client/packages'
optimist = optimist
  .usage('Start a local app server.')
  .options('p', {
    describe: 'Use alternative port',
    alias: 'port'
  })
  .options('a', {
    describe: 'Proxy to alternative api server',
    alias: 'api-server',
    string: true
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

// Proxy setup
var apiServer = argv.a || 'webadmin-qaammdm8.absolute.com';

var getAmConfig = function() {
  return devUtil.generateAppConfig('..', 120, devUtil.parseConfigFile('./config.js'));
};

app.configure(function(){
  app.use(express.favicon());
  app.use(apiDispatcher(apiServer, 8080, false, function(path) {
    return 0 === path.indexOf('/com.absolute.am.webapi/api/');
  }));
  app.use(app.router);
  app.use(express.methodOverride());

  app.use('/login/config.js', devUtil.genConfigMiddleware(getAmConfig));
  app.use('/am/config.js', devUtil.genConfigMiddleware(getAmConfig));
  app.use('/mylogin/config.js', devUtil.genConfigMiddleware(getAmConfig));
  app.use('/mydevices/config.js', devUtil.genConfigMiddleware(getAmConfig));
});

app.get('/', function(req, res) {
  res.redirect('/am');
});

app.configure('development', function(){
  // Logger
  app.use(express.logger('dev'));

  app.use(express.static(__dirname + '/client'));
  app.use(express.static(__dirname + '../../../lib/client'));

  devUtil.configureDevEnvironment(app);
  devUtil.configureDocServer(app, '/dev/am-web-admin/docs', __dirname + '/docs');

  app.use(express.errorHandler({
    dumpExceptions: true, 
    showStack: true
  }));
});

app.configure('production', function(){
  // build client if directory client-build not found
  require('./build');
  app.use(express['static'](__dirname + '/build/out/www'));
});

// HTTP
var port = process.env.PORT || 3000;

if(argv.p !== undefined) {
  port = argv.p; //if --p parameter was passed overwrite the default port
}
app.listen(port);

console.log('Http server listening on port ' + port);
