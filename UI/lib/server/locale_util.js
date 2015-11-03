'use strict';

// LocaleUtil
// ==========
//
// Shared code for locale strings import / export.

// setPath(obj, path, value)
// -------------------------
//
// Treat path as a '.' delimited set of object keys to walk from obj in order to set value. e.g.
//
// Given:
//
//     var obj = {
//       foo: {
//         bar: null
//       }
//     }
//
// Invoking setPath(obj, 'foo.bar', true) would transform obj to this structure:
//
//     var obj = {
//       foo: {
//         bar: true
//       }
//     }
//
// Note that setPath() will not create intermediate objects along the path. They are expected to exist at invocation time.
var setPath = function(obj, path, value) {
  var slugs = path.split('.');

  for (var i = 0; i < slugs.length - 1; i++) {
    obj = obj[slugs[i]];
  }

  obj[slugs[slugs.length - 1]] = value;
}

// validateHandlebars(template)
// ----------------------------
//
// Returns true if the Handlebars is able to compile the template AND it contains a valid HTML fragment.
//
// False otherwise. This validator is designed to be "good enough", not to be a perfect HTML checker.
var Handlebars = require('handlebars');
var fs = require('fs');

// Just aiming for "good enough"
var TAG_OPEN = /^<(\w+)[^<>]*>/;
var TAG_CLOSE = /^<\/(\w+)>/;

var validateHandlebars = function(template) {

  // Compile and render the template with no context. This will:
  //
  // 1. Validate the structure of the moustaches.
  // 2. Remove them from the fragment so we can simply validate HTML (not that it should matter).
  try {
    var fragment = Handlebars.compile(template)({});
  } catch(e) {
    // Handlebars failed
    return false;
  } 

  // The Handlebars compiler doesn't complain about closing of un-opened moustaches, but we do
  if (-1 !== fragment.indexOf('}}')) {
    return false;
  }

  // Returned by nextToken() on error
  var INVALID = {};

  // The stack of open HTML tags
  var context = [];

  var nextToken = function() {
    // Find earliest index for '<'
    var idx = fragment.indexOf('<');
    if (-1 === idx) {
      return null;
    }

    // Discard text up to token start
    fragment = fragment.slice(idx);

    // Parse token
    var closingMatch = fragment.match(TAG_CLOSE);
    if (closingMatch) {
      fragment = fragment.slice(closingMatch[0].length);
      return { name: closingMatch[1], isOpening: false };
    }

    var openingMatch = fragment.match(TAG_OPEN);
    if (openingMatch) {
      fragment = fragment.slice(openingMatch[0].length);
      return { name: openingMatch[1], isOpening: true };
    }

    return INVALID;
  };

  var token = null;
  while (token = nextToken()) {
    if (INVALID === token) {
      return false;
    } else if (token.isOpening) {
      context.push(token);
    } else {
      // !token.isOpening
      var topToken = context.pop();
      if (!topToken || topToken.name !== token.name) {
        return false;
      }
    }
  }

  return 0 === context.length;
};

// findBuildPaths(sitesPath)
// -------------------------
//
// Returns full path
var findBuildPaths = function(sitesPath) {
  var buildPaths = [];

  fs.readdirSync(sitesPath).forEach(function(name) {
    var possibleBuildPath = sitesPath + '/' + name + '/build';
    if (fs.existsSync(possibleBuildPath)) {
      buildPaths.push(possibleBuildPath);
    }
  });

  return buildPaths;
};

// visitDirectories(path, op)
// --------------------------
//
// Visitor pattern for a filesystem tree (depth first). Path must specify a directory.
// Only directories are visited.
var visitDirectories = function(path, op) {
  path = fs.realpathSync(path);

  if (path.length < process.cwd().length) {
    throw path;
  }

  fs.readdirSync(path)
    .filter(function(name) { return fs.statSync(fs.realpathSync(path + '/' + name)).isDirectory(); })
    .forEach(function(name) {
      // Visit children
      visitDirectories(fs.realpathSync(path + '/' + name), op);
    });

  // Visit self
  op(fs.realpathSync(path));
};

// visitResource(namespace, namespaceOp, stringOp, refOp, path)
// ------------------------------------------------------------
//
// Visitor pattern for a string resource file. Callbacks are invoked as follows:
//
// - namespaceOp(namespace, path)
// - stringOp(name, value, path)
// - refOp(name, value, path)
//
var visitResource = function(namespace, namespaceOp, stringOp, refOp, path) {
  path = path || '';

  if (namespaceOp) {
    namespaceOp(namespace, path);
  }

  for (var name in namespace) {
    if (namespace.hasOwnProperty(name)) {

      var value = namespace[name];
      var valuePath = path.length > 0 ? path + '.' + name : name;

      if ('string' === typeof(value)) {

        if (stringOp) {
          stringOp(name, value, valuePath);
        }
      } else if ('object' === typeof(value) && value.value) {
        // If a field has a comment attached to it the format will be e.g.:
        // 'foo': { value: 'foo', comment: 'bar' }
        // In this case, stop recursion and pass the object as it is
        if (stringOp) {
          stringOp(name, value, valuePath);
        }
      } else if ('object' === typeof(value) && value.ref) {

        if (refOp) {
          refOp(name, value, valuePath);
        }
      } else if ('object' === typeof(value)) {

        // Recurse into namespaces
        visitResource(value, namespaceOp, stringOp, refOp, valuePath);
      }
    }
  }
};

// findAndProcessResources(searchPaths, excludedPaths, process)
// -----------------------------------------------------------
//
// For each searchPath, search for nls/strings.js. Ignore children of the excludedPaths. For each resource, invoke
// process(resource, resourceFilePath).
var findAndProcessResources = function(searchPaths, excludedPaths, process) {
  var excludedFullPaths = excludedPaths.map(function(path) { return fs.realpathSync(path); });

  searchPaths.forEach(function(searchPath) {
    // Recurse through all directories, minding the exclusions list
    visitDirectories(searchPath, function(path) {
      // Skip excluded paths
      if (excludedFullPaths.some(function(excludedFullPath) { return 0 === path.indexOf(excludedFullPath); })) {
        return;
      }

      // Shadow definition of define() while reading resources
      var define = function(hash) { return hash; };

      // If nls/strings.js exists
      var resourcePath = path + '/nls/strings.js';
      if (fs.existsSync(resourcePath)) {
        // Read it and merge into the locale strings
        var src = fs.readFileSync(resourcePath).toString();

        var resource = null;
        try {
          resource = eval(src);
        } catch(e) {
          console.error('Error parsing ' + resourcePath + ': ' + e);
        }

        if (resource) {
          process(resource.root, resourcePath);
        }
      }
    });
  });
};

// toFormattedJS(obj)
// --------------------
//
// Stringify a JS Object to a more human-readable format.
var toFormattedJS = function(obj) {
  var lines = [];

  for (var name in obj) {
    if (obj.hasOwnProperty(name)) {
      var value, comment = '', addComma = false;

      // If the passed object has comment, add it to the field inline
      // e.g. 'foo': { value: 'foo', comment: 'bar' } will be transformed to: 'foo': 'foo', // bar
      if (obj[name].value) {
        value = JSON.stringify(obj[name].value, null, 4);
        comment = ' /* ' + obj[name].comment + ' */';
      } else {
        value = JSON.stringify(obj[name], null, 4);
      }

      lines.push('  ' + JSON.stringify(name, null, 4) + ': ' + value + ',' + comment);
    }
  }

  return '{\n' + lines.join('\n') + '\n}';
};

// Exports
// -------
module.exports = {
  setPath: setPath,

  TAG_OPEN: TAG_OPEN,
  TAG_CLOSE: TAG_CLOSE,
  validateHandlebars: validateHandlebars,

  findBuildPaths: findBuildPaths,
  visitDirectories: visitDirectories,
  visitResource: visitResource,

  findAndProcessResources: findAndProcessResources,
  toFormattedJS: toFormattedJS
};
