// Development Utilities
// =====================
//
// A collection of utility methods for configuring development app servers.

'use strict';

var express = require('express'), expressMarkdown = require('express-markdown');
var lessMiddleware = require('less-middleware');
var ConfigGenerator = require('./config_generator');
var cg = new ConfigGenerator();
var devTestsRoot = '/dev/tests';

// Log the request and result at this point in the call chain.
var middleWare = {
  logger: function(request, result, next) {
    console.log('REQUEST:');
    console.log(JSON.stringify(request.headers, true, 2));
    if (result['content-type'] =~ /text/) {
      console.log('RESPONSE:');
      var emittedObjects = [];
      console.log(JSON.stringify(result, function(key, value) {
        if (emittedObjects.indexOf(value) < 0 || typeof(value) !== 'object') {
          emittedObjects.push(value);
          return value;
        } else {
          return '[[[ RECURSE ]]]';
        }
      }, 2));
    }
    next();
  }
};

// We require a 0.8.x Node, due to the way SSL certificates are handled by the proxies.
// Connection handling for self-signed certs changed between 0.8.x and 0.10.x.
function checkNodeVersion() {
  if (8 < Number(process.version.split('.')[1])) {
    throw "Sorry, connection handling for self-signed certs changed between 0.8.x and 0.10.x. We can't handle the new way.";
  }
}

// Basic dev server setup.
function configureDevEnvironment(app) {
  configureDevRoot(app);
  configureSharedDocServer(app);
  configureSharedTestServer(app);
}

// Configure middleware to redirect root to doc root.
function configureDevRoot(app) {
  app.use('/dev', function(request, result, next) {
    if (request.path.match(/^\/?$/)) {
      result.redirect('/dev/docs/');
    } else {
      next();
    }
  });
}

// Configure middleware for serving formatted documents written in Markdown.
function configureDocServer(app, wwwRoot, fileSystemRoot) {
  app.use(wwwRoot, expressMarkdown({
    directory: fileSystemRoot,
    view: '../../../docs/assets/layout.handlebars'
  }));
  app.use(wwwRoot, express.static(fileSystemRoot));
  app.use(wwwRoot, express.directory(fileSystemRoot));
}

// Configure middleware for the global docs.
function configureSharedDocServer(app) {
  configureDocServer(app, '/dev/docs', __dirname + '../../../docs');
}

function configureSharedTestServer(app) {
  app.use(devTestsRoot, function(request, result, next) {
    if (request.path.match(/^\/?$/)) {
      result.redirect(devTestsRoot + '/index.html');
    } else {
      next();
    }
  });

  app.use('/dev/src', express.static(__dirname + '../../../lib/client'));
  app.use(devTestsRoot, express.static(__dirname + '../../../tests/client'));
}

// Configure middleware for less files
function configureLessFileServer(appRoot, dirname, app) {
  app.use(appRoot, lessMiddleware(dirname, {
    compiler: { sourceMap: true }
  }));
}

// Configure dev test config javascript file
function configureStaticConfigFile(app, fn) {
  app.use(devTestsRoot + '/config.js', genConfigMiddleware(fn));
}

// Generate middleware for config files
function genConfigMiddleware(fn) {
  return function(req, res) {
    var appConfig = fn();
    // Response config.js as javascript type
    res.set({'Content-Type': 'application/javascript'});
    res.send(genStaticConfig(appConfig));
  };
}

function genStaticConfig(config) {
  return 'define(function() { requirejs.config(' + JSON.stringify(cg.generateConfig(config)) + '); });';
}

// Exports
// -------

module.exports = {
  middleWare: middleWare,

  checkNodeVersion: checkNodeVersion,

  configureDevEnvironment: configureDevEnvironment,

  configureDevRoot: configureDevRoot,
  configureDocServer: configureDocServer,
  configureSharedDocServer: configureSharedDocServer,
  configureSharedTestServer: configureSharedTestServer,
  configureLessFileServer: configureLessFileServer,
  configureStaticConfigFile: configureStaticConfigFile,

  generateAppConfig: cg.generateAppConfig,
  generateConfig: cg.generateConfig,
  parseConfigFile: cg.parseConfigFile,
  genConfigMiddleware: genConfigMiddleware,
  genStaticConfig: genStaticConfig
}
