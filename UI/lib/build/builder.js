// The Next Gen UI Build Library
// =============================

'use strict';

// Dependencies
// ------------

var requirejs = require('requirejs'),
  fs = require('fs'),
  crypto = require('crypto'),
  async = require('async'),
  ncp = require('ncp').ncp,
  exec = require('child_process').exec,
  devUtil = require('../server/dev_util');

// Default build configuration settings
// ------------------------------------

var defaultBuildConfig = {
  // Missing, and required:
  //
  // modules: [ { name: 'path to app' }, ... ]
  //
  // dir: 'path to final output dir'

  mainConfigFile: 'client/config.js',
  findNestedDependencies: true,

  appDir: 'build/working',
  baseUrl: './',

  // optimize: 'none', // For debugging built versions
  optimize: 'none',

  wrap: true,
  inlineText: true,
  preserveLicenseComments: false,

  // filter items to be copied to the 'dir' using this pattern. Refer to options.filter in https://github.com/AvianFlu/ncp to see how this can be defined.
  filter: function(){
    return true;
  },

  nodeRequire: require
};

// Utility functions
// -----------------

function checksum(filename) {
  return crypto.createHash('md5').update(fs.readFileSync(filename, 'utf8')).digest('hex');
}

function prependCopyrightNotice(path, start, end, copyrightTemplate) {
  var content = start + "\n" + copyrightTemplate + "\n" + end + "\n" + fs.readFileSync(path);
  fs.writeFileSync(path, content);
}

function cacheBreak(input, rootPath, regexp, pathTransformer, replacer) {
  return input.replace(regexp, function(fullMatch, pathMatch) {
    var relativePath = pathTransformer(pathMatch);
    var version = checksum(rootPath + relativePath);

    return replacer(pathMatch, version);
  });
}

function configureModules(moduleNames) {
  var packagesPath = '../../lib/client/packages';
  var exclusions = ['env'];

  var jsWorldLocaleSources = fs.readdirSync(packagesPath + '/platform/lib/JsWorld-2.8.1/locales/js');
  exclusions = exclusions.concat(jsWorldLocaleSources.map(function(src) {
    return 'jsworld-locales/' + src.replace(/\.js$/, '');
  }));

  return moduleNames.map(function(name) {
    return { name: name + '/main', exclude: exclusions };
  });
}

// Visitor pattern for a filesystem tree (depth first). Path must specify a directory.
function visitTree(path, op) {
  path = fs.realpathSync(path);

  if (path.length < process.cwd().length) {
    throw path;
  }

  fs.readdirSync(path)
    .filter(function(name) { return fs.statSync(fs.realpathSync(path + '/' + name)).isDirectory(); })
    .forEach(function(name) { visitTree(fs.realpathSync(path + '/' + name), op); });

  fs.readdirSync(path).forEach(function(name) { op(fs.realpathSync(path + '/' + name)); });
}

// Recursively remove all javascript files from the path, leaving those in the exceptions list alone.
function purgeJsFiles(path, exceptions) {
  return visitTree(path, function(visitedPath) {
    if (!exceptions.some(function(exceptionPath) { return exceptionPath === visitedPath; }) && visitedPath.match(/\.js$/)) {
      fs.unlinkSync(visitedPath);
    }
  });
}

function logHeader(header, buildOutput) {
  console.log('');
  console.log('********************************************************************************');
  console.log(header);
  console.log('');

  if (arguments.length > 1) {
    console.log(buildOutput);
  }
}

// The Binder
// ----------
//
// Returns an object with a collection of build steps suitable for async.series(), and a 
// run() convenience method that invokes them in that way.
function bind(indexHtmlFileName, buildConfig, buildParam) {
  return {
    generateConfigFile: function(callback) {
      var config = devUtil.generateAppConfig(buildParam.baseUrl, buildParam.waitSeconds, buildParam.configObject);
      fs.writeFile(
        buildConfig.mainConfigFile,
        'define(function() { requirejs.config('+ JSON.stringify(devUtil.generateConfig(config)) +'); });',
        'utf8',
        function(err, written, buffer) { if (!err) callback(); }
      );
    },

    purgeWorkingDir: function(callback) {
      logHeader('PURGE WORKING DIR');

      exec('rm -Rf build/working', function(error, stdout, stderr) { callback(); });
    },

    initializeWorkingDir: function(callback) {
      logHeader('MAKE WORKING DIR');

      exec('mkdir -p build/working', function(error, stdout, stderr) { callback(); });
    },

    copyLibs: function(callback) {
      logHeader('COPY LIBS TO MERGED DIR');
      ncp('../../lib/client', buildConfig.appDir, {filter: buildConfig.filter}, callback);
    },

    copyApps: function(callback) {
      logHeader('COPY APPS TO MERGED DIR');
      ncp('client', buildConfig.appDir, callback);
    },

    optimize: function(callback) {
      logHeader('OPTIMIZE');

      requirejs.optimize(buildConfig, function(buildOutput) {
        logHeader('OPTIMIZE COMPLETE', buildOutput);
        callback();
      });
    },

    finalizeBuild: function(callback) {
      logHeader('FINALIZE BUILD');

      var copyrightTemplate = fs.readFileSync('../../copyright_template.txt', 'utf8').replace('{{BUILD_YEAR}}', (new Date()).getFullYear());

      for (var i = 0; i < buildConfig.modules.length; i++) {
        var module = buildConfig.modules[i];

        var moduleRoot = buildConfig.dir + module.name.replace('main', '');
        var indexPath = moduleRoot + indexHtmlFileName;

        console.log('Modifying ' + indexPath + '...');

        // Prepend copyright notices.
        prependCopyrightNotice(moduleRoot + 'main.js', '/*', '*/', copyrightTemplate);

        // Add md5 checksum all urls in index.aspx, to break client side caches.
        var indexhtml = fs.readFileSync(indexPath, 'utf8');

        indexhtml = cacheBreak(indexhtml, moduleRoot, /data-main="(\w+)"/g,
          function(match) { return match + '.js'; },
          function(match, version) { return 'data-main="' + match + '.js?v=' + version + '"'; }
        );
        indexhtml = cacheBreak(indexhtml, moduleRoot, /href="(\S+).css"/g,
          function(match) { return match + '.css'; },
          function(match, version) { return 'href="' + match + '.css?v=' + version + '"'; }
        );

        fs.writeFileSync(indexPath, indexhtml, 'utf8');
      }

      // Purge unoptimized JS files from package and app hierarchy (save <module>/main.js).
      var appMains = buildConfig.modules.map(function(module) {
        return fs.realpathSync(buildConfig.dir + module.name + '.js');
      });

      var libFiles = [];
      visitTree(buildConfig.dir + '/packages/platform/lib', function(path) {
        libFiles.push(path);
      });

      var resourceFiles = [];
      var packagesPath = buildConfig.dir + 'packages';

      fs.readdirSync(packagesPath).forEach(function(topPackageName) {
        var topPackagePath = packagesPath + '/' + topPackageName;

        if (fs.statSync(topPackagePath).isDirectory()) {

          fs.readdirSync(topPackagePath).forEach(function (packageName) {
            var nlsPath = topPackagePath + '/' + packageName + '/nls';

            if (fs.existsSync(nlsPath)) {
              var rootResourceNames = [];
              var localizationNames = [];

              // Exclude localized resources for each root resource
              fs.readdirSync(nlsPath).forEach(function (nlsName) {
                if (fs.statSync(nlsPath + '/' + nlsName).isDirectory()) {
                  localizationNames.push(nlsName);
                } else {
                  rootResourceNames.push(nlsName);
                }
              });

              localizationNames.forEach(function (localizationName) {
                rootResourceNames.forEach(function (rootResourceName) {
                  resourceFiles.push(fs.realpathSync(
                    nlsPath + '/' + localizationName + '/' + rootResourceName
                  ));
                });
              });
            }
          });
        }
      });

      var purgeExceptions = appMains.concat(libFiles).concat(resourceFiles);

      purgeJsFiles(buildConfig.dir + 'packages', purgeExceptions);
      buildConfig.modules.forEach(function(module) {
        purgeJsFiles(buildConfig.dir + module.name.replace('main', ''), appMains);
      });

      // Purge .DS_Store crud.
      visitTree(buildConfig.dir, function(path) {
        var dsPath = path + '/' + '.DS_Store';
        if (fs.existsSync(dsPath)) {
          fs.unlinkSync(dsPath);
        }
      });

      // Purge empty dirs.
      visitTree(buildConfig.dir, function(path) {
        if (fs.statSync(path).isDirectory() && 0 === fs.readdirSync(path).length) {
          fs.rmdirSync(path);
        }
      });

      fs.unlinkSync(buildConfig.mainConfigFile);
      callback();
    },

    run: function(callback) {
      return async.series([
        this.generateConfigFile,
        this.initializeWorkingDir,
        this.copyLibs,
        this.copyApps,
        this.optimize,
        this.finalizeBuild
      ], callback);
    }
  };
}

// Exports
// -------

module.exports = {
  defaultBuildConfig: defaultBuildConfig,

  checksum: checksum,
  configureModules: configureModules,
  prependCopyrightNotice: prependCopyrightNotice,
  cacheBreak: cacheBreak,
  visitTree: visitTree,
  purgeJsFiles: purgeJsFiles,
  logHeader: logHeader,

  bind: bind
};
