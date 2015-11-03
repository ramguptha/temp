// Export Locale Strings
// =====================
//
// Responsible for creating an english translations "template" file from the corresponding package resources.

var optimist   = require('optimist'),
    fs         = require('fs'),
    localeUtil = require('../lib/server/locale_util');

// Main Entry
// ----------

// Command-line options
optimist = optimist
  .usage('Create a merged resource file from the provided package resources for processing by the translation group.')
  .options('i', {
    describe: 'Comma separated list of paths to search for \'nls/strings.js\'',
    alias: 'string-search-dirs',
    default: '.'
  })
  .options('e', {
    describe: 'Comma separated list of root paths to ignore when searching for string files',
    alias: 'string-exclude-dirs'
  })
  .options('o', {
    describe: 'Write JS properties file (strings.js) to this directory',
    alias: 'out',
    default: '.'
  })
  .options('v', {
    describe: 'If provided, do not write translations file',
    alias: 'validate',
    boolean: true
  })
  .options('h', {
    describe: 'Print this message',
    alias: 'help'
  });

var argv = optimist.argv;

if (argv.help) {
  optimist.showHelp();
  return;
}

var translations = {};
var resourcePathsByTranslationKey = {};

var searchPaths = (argv.i || '.').split(',');
var excludedPaths = argv.e ? argv.e.split(',') : localeUtil.findBuildPaths('sites');

localeUtil.findAndProcessResources(searchPaths, excludedPaths, function(resource, resourceFilePath) {
  localeUtil.visitResource(
    resource,
    null,
    function(name, value, path) {
      // Complain on key conflicts
      if (translations[path]) {
        console.error(
          'Key conflict: ' + path + ' defined in ' + resourcePathsByTranslationKey[path] + 
          ' and ' + resourceFilePath
        );
      }

      // Complain on malformed template
      //  Do not check the handlebars validation if key is an object which has 'value' and 'comment' keys
      // e.g. { ago: { value: 'ago', comment: 'Do not leave the field blank' }
      if ((typeof value !== 'object' && !value.value) && !localeUtil.validateHandlebars(value)) {
        console.error('Malformed resource: ' + path + ' defined in ' + resourceFilePath);
      }

      // Merge
      translations[path] = value;
      resourcePathsByTranslationKey[path] = resourceFilePath;
    }
  );
});

// Write the merged locale strings file
if (!argv.validate) {
  var js = localeUtil.toFormattedJS(translations);
  fs.writeFile(argv.out + '/strings.js', js);
}
