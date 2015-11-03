var shell = require('shelljs');
var PORT = process.env.PORT || 3000;

module.exports = function(grunt) {

  var environment = grunt.option('e') || 'dv2arch';

  // Project configuration.
  grunt.initConfig({
    pkg: grunt.file.readJSON('package.json'),
    open: {
      server: {
        path: 'http://localhost:' + PORT
      },
      'design-docs': {
        path: 'http://localhost:' + PORT + '/dev/cc-classic/docs/tasks'
      },
      'unit-tests': {
        path: 'http://localhost:' + PORT + '/dev/tests'
      }
    },
    express: {
      options: {
        args: ['-e', environment],
        debug: true
      },
      dev: {
        options: {
          script: 'sites/cc-classic/app.js'
        }
      }
    }
  });

  grunt.loadNpmTasks('grunt-open');
  grunt.loadNpmTasks('grunt-express-server');

  grunt.registerTask(
    'setup-docs',
    'Install pre-requisites for building docs. Not part of the package.json in order to make life easier for casual browsers of the source.',
    function() {
      shell.exec('npm install DaveDuchene/groc.git');
    }
  );

  grunt.registerTask(
    'clean-docs',
    'Remove the generated API documentation',
    function() {
      shell.exec('rm -Rf docs/api');
    }
  );

  grunt.registerTask(
    'build-docs',
    'Build the api docs.',
    function() {
      shell.exec("node_modules/groc/bin/groc --glob 'client/**/*.js' --except 'client/packages/platform/lib/**/*.js' --strip 'client' --out 'docs/api'");
    }
  );

  // Default task(s).
  grunt.registerTask('default', ['test']);
  // Start cc-classic server task
  grunt.registerTask('wait', function () {
    var done = this.async();

    setTimeout(function () {
      done();
    }, 1500);
  });
  grunt.registerTask('express-keepalive', 'Keep grunt running', function() {
    this.async();
  });
  grunt.registerTask('cc-classic', ['express', 'wait', 'open:server', 'express-keepalive']);
  // Start cc-classic design-docs task
  // TODO It's assumed now that the server is already running. Need to implement a check for it to avoid running the server if it's still running
  grunt.registerTask('cc-design-docs', ['open:design-docs']);
  // Start cc-classic unit-tests task
  grunt.registerTask('cc-unit-tests', ['open:unit-tests']);
};
