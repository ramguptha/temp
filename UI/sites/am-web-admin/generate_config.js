// Automatically generate the requireJS configs and store 

// Run: node sites\am-web-admin\generate_config.js

'use strict';

var devUtil = require('../../lib/server/dev_util'),
  path = require('path'),
  fs = require('fs'),
  config = {
    rootAreaDirectory: __dirname + path.sep + 'client' + path.sep,

    configPath: __dirname + path.sep + 'config.js',

    areas: ['login', 'am', 'mylogin', 'mydevices']
  },
  areaConfigFile = devUtil.generateAppConfig('..', 120, devUtil.parseConfigFile(config.configPath)),
  requirejsConfigPath;

config.areas.forEach(function(area){

  requirejsConfigPath = config.rootAreaDirectory + area + path.sep + 'config.js';

  fs.writeFile(requirejsConfigPath, devUtil.genStaticConfig(areaConfigFile), function(err) {
    if(err) {
      console.log(err);
    } else {
      console.log('The file was saved!');
    }
  });
});
