// Import Locale Strings
// =====================
//
// Responsible for creating / updating client package resources given a translations file.

var optimist = require('optimist'),
  path       = require('path'),
  fs         = require('fs'),
  _          = require('underscore'),
  localeUtil = require('../lib/server/locale_util');

// Read translations into memory, returning them as a map of { 'locale name': { strings ... } }, e.g.:
//
//     {
//       'de-de': { foo: 'bar' }
//     }
//
// If no translations are found, returns null.
function readTranslations(translationsPath) {
  var translations = {};

  fs.readdirSync(fs.realpathSync(translationsPath)).filter(function(name) {
    return name.match(/^strings_[\w]+(-[\w]+)*\.js$/);
  }).forEach(function(name) {
    var locale = name.match(/strings_(.*)\.js/)[1];
    var localeTranslationsSrc = fs.readFileSync(fs.realpathSync(translationsPath + '/' + name));

    // The translation team's tool seems to add some kind of garbage to the end of the first line in its
    // JSON export. No idea what it is (not a BOM). Anyway, strip it out.
    localeTranslationsSrc = localeTranslationsSrc.toString().replace(/^[^\r\n]*{[^\r\n]*(\r?\n)/, '{$1');

    // We need to reconstruct the key-value pairs if there is any comment added by the translation team
    var localeTranslationsArray =  localeTranslationsSrc.split('\n').map(function(item, index) {
      if ((/^{|}$/g).test(item)) { return item; }

      // Check if there is a line that contains comment (Expected style for comment is /* ... */
      if (/\/\*/.test(item)) {
        var commentedLine = item.split('/*');
        // Comment is expected to be after ',' at the end of the line, but check if it's put before ending ','
        var comment = commentedLine[1].replace(/\*\//, '').trim().replace(/,$/, '').trim();

        // Some translated values may end with a ':' characters.
        // Make sure we split by only the first occurrence (that is after key)
        var line = commentedLine[0].split(/:(.+)/);
        var key = line[0];
        var value = line[1].trim().replace(/,$/, '');

        // Construct a new key for the any type of these commented lines:
        // "ago": "ago", /* test comment */ OR
        // "ago": "ago" /* test comment */ , OR
        // "ago": "ago" /* test comment , OR
        // "ago": "ago", /* test comment
        //
        // would be transformed to:
        // "ago": { "value": "ago", "comment": "test comment" },
        //
        item = key + ': { "value"' + ': ' + value + ', "comment"' + ': "' + comment + '" }, \n';
      }

      // As eval() does not fail with a helpful message,
      // we do a test eval() at each line and exit the process in case of failure
      try {
        var testJS = eval('({' + item + '})');
      } catch(e) {
        console.error('Failed to parse translations file ' + name + '. Line Number: ' + (index + 1) + '\n' + item);

        // Return with the code specific to javascript parser
        process.exit(3);
      }

      return item;
    });

    localeTranslationsSrc = localeTranslationsArray.join('');

    try {
      // Any thrown error with eval() should have been caught upon mapping earlier in this code.
      // Here any possible remaining error regarding the structure should be caught.
      var localeTranslations = eval('(' + localeTranslationsSrc + ')');
    } catch(e) {
      console.error('Failed to parse translations file ' + name + ', ' + e);

      // Return with the code specific to javascript parser
      process.exit(3);
    }

    for (var key in localeTranslations) {
      // Do not check the handlebars validation if key is an object which has 'value' and 'comment' keys
      // e.g. { ago: { value: 'ago', comment: 'Do not leave the field blank' }
      if ((typeof localeTranslations[key] !== 'object' && !localeTranslations[key].value) &&
        !localeUtil.validateHandlebars(localeTranslations[key])) {
        console.error('Malformed resource: ' + key + ' defined in ' + name);
      }
    }

    translations[locale] = localeTranslations;
  });

  return 0 < _.keys(translations).length ? translations : null;
}

function updateEnglishCommentsForLocale(translations, locale, resourceFile) {
  var englishTranslationsSrc = fs.readFileSync(resourceFile).toString(),
  englishTranslations = null, localeTranslations = translations[locale], writeToFile = false;

  try {
    // Shadow definition of define() while reading resources
    var define = function(hash) { return hash; };

    englishTranslations = eval(englishTranslationsSrc);
  } catch(e) {
    console.error('Error parsing ' + resourceFile + ': ' + e);
  }

  for (var key in localeTranslations) {
    if (localeTranslations.hasOwnProperty(key) && (localeTranslations[key] instanceof Object)) {

      // checking whether an English translation exists in the current resource, if it exists then we insert the value and the comment
      try {
        var englishValue = eval('englishTranslations.root.' + key);
        if(englishValue instanceof Object) {
          englishValue = englishValue.value;
        }
        var valueToSet = '{"value": "' +  englishValue + '", "comment": "' + localeTranslations[key].comment + '"}';
        eval('englishTranslations.root.' + key + '=' + valueToSet);
        writeToFile = true;
      } catch(e) {}
    }
  }

  // Write localized resource
  if(writeToFile) {
    fs.writeFileSync(resourceFile, 'define(' + localeUtil.toFormattedJS(englishTranslations) + ');');
  }
}

// Given a resource / resourceFilePath, write corresponding resources for each translation in memory.
function writeResources(translations, resource, resourceFilePath, writeCommentsToEnglishXlation) {
  // For each translation
  for (var locale in translations) {
    var localeTranslations = translations[locale];

    // Create localized resource using resource as template and translations for values
    var localeResource = {};
    localeUtil.visitResource(
      resource,
      function(namespace, path) {
        if (0 < path.length) {
          localeUtil.setPath(localeResource, path, {});
        }
      },
      function(name, value, path) {
        var translation = localeTranslations[path];
        if (translation) {
          // take out the translation comment from the localized file
          if( writeCommentsToEnglishXlation && translation instanceof Object) {
            translation = {value: translation.value};
          }
          localeUtil.setPath(localeResource, path, translation);
        } else {
          console.error('Missing translation in ' + locale + ': ' + path);
        }
      }
    );

    // Ensure resource dir for locale exists
    var localeResourceDirPath = resourceFilePath.replace(/strings\.js$/, '') + locale;
    if (!fs.existsSync(localeResourceDirPath)) {
      fs.mkdirSync(localeResourceDirPath);
    }

    if(writeCommentsToEnglishXlation) {
      // write the comments to the English translation file
      updateEnglishCommentsForLocale(translations, locale, resourceFilePath);
    }

    // Write localized resource
    fs.writeFileSync(localeResourceDirPath + '/strings.js', 'define(' + localeUtil.toFormattedJS(localeResource) + ');');
  }
}

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
  .options('t', {
    describe: 'Path to translations directory. Files like "strings.js", "strings_de-de.js" and so-on are expected',
    alias: 'translations-dir',
    default: '.'
  })
  .options('v', {
    describe: 'If provided, do not write translations file',
    alias: 'validate',
    boolean: true
  })
  .options('w', {
    describe: 'Import the translation comments into the English translation files',
    alias: 'import-comments-to-english',
    boolean: true
  })
  .options('h', {
    describe: 'Print this message',
    alias: 'help'
  });

var argv = optimist.argv;

// Read all translations into memory
var translations = readTranslations(argv.t);
if (!translations) {
  console.error('No translations found in ' + argv.t);

  process.exit(1);
}

var searchPaths = (argv.i || '.').split(',');
var excludedPaths = argv.e ? argv.e.split(',') : localeUtil.findBuildPaths('sites');

if (!argv.validate) {
  localeUtil.findAndProcessResources(searchPaths, excludedPaths, function(resource, resourceFilePath) {
    writeResources(translations, resource, resourceFilePath, argv.w);
  });
}
