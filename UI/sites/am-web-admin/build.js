// Build the UI for Absolute Manage Web Admin
// ==========================================

'use strict';

var builder = require('../../lib/build/builder'),
  devUtil   = require('../../lib/server/dev_util'),
  _         = require('underscore'),
  recursive = require('recursive-readdir'),
  CleanCSS  = require('clean-css'),
  fs        = require('fs');

var buildDir = 'build/out/www';

builder.bind(
  'index.html', 
  _.extend({}, builder.defaultBuildConfig, {
    optimize: 'uglify',

    modules: builder.configureModules(['am', 'login', 'mylogin', 'mydevices']),

    mainConfigFile: 'client/config.js',

    dir: buildDir,

    excludePattern: null,

    // filter out the CC related stuff and .gitignore files
    filter: function(fileName) {
      return fileName.indexOf('/packages/analysis') === -1 && fileName.indexOf('/packages/cc') === -1 && !/.gitignore$/.test(fileName);
    }
  }), {
    baseUrl: '..',
    waitSeconds: 120,
    configObject: devUtil.parseConfigFile('./config.js')
  }
).run(
  function(err) {
    if (err) {
      builder.logHeader('BUILD FAILED', err);
    } else {
      builder.logHeader('BUILD COMPLETE');
      optimizeMore();
    }
  }
);

// delete the unnecessary .handlebar files and minify the .css
function optimizeMore() {
  recursive(buildDir, function (err, files) {
    files.forEach(function(file) {
      if(/.css$/.test(file)) {
        fs.writeFileSync(file, new CleanCSS().minify(fs.readFileSync(file).toString()).styles);
      } else if(/.handlebars/.test(file)) {
        fs.unlinkSync(file);
      }
    });
  });
}
