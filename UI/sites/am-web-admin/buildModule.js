// Load the requirejs optimizer
var requirejs = require('requirejs');

// Parse process.argv
var args = require('optimist')
  .usage('Usage: $0')

  .demand('m').alias('m', 'module').describe('m', 'Module name (client/packages/<module name>)')
  .demand('o').alias('o', 'optimize').describe('o', 'Optimize (default: uglify, none to disable)').default('o', 'uglify')

  .argv;

// Set up basic config
var config = {
  modules: [{ name: args.module + '/main' }],
  mainConfigFile: 'client/' + args.module + '/main.js',
  optimize: args.optimize,

  appDir: 'client',
  dir: 'client-build',
  baseUrl: './',

  wrap: true,
  inlineText: true,
  preserveLicenseComments: false,

	nodeRequire: require
};

console.log('BUILDING:');
console.log('');
console.log(config);

requirejs.optimize(config, function(output) {
  console.log('');
  console.log('RESULTS:');
  console.log('');
  console.log(output);
});
