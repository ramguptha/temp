'use strict';
var _ = require('underscore');
var fs = require('fs');

var ConfigGenerator = function() {
  var mergeConfig = function(conf1, conf2) {
    _.extend(conf1.paths, conf2.paths);
    _.extend(conf1.map['*'], conf2.map['*']);
    _.extend(conf1.shim, conf2.shim);
    conf1.packages = _.union(conf1.packages, conf2.packages);
  };

  var generateBaseConfig = function(baseUrl, waitSeconds) {
    return {
      baseUrl: baseUrl,
      waitSeconds: waitSeconds,
      paths: {},
      packages: [],
      map: {
        '*': {}
      },
      shim: {}
    };
  };

  var parseConfigFile = function(url) {
    return eval(fs.readFileSync(url).toString());
  };

  this.generateAppConfig = function(baseUrl, waitSeconds, config) {
    var baseConfig = generateBaseConfig(baseUrl, waitSeconds);
    mergeConfig(baseConfig, config);
    return baseConfig;
  };

  this.generateConfig = function(appConfig) {
    // More readable naming
    var sharedConfig = parseConfigFile(__dirname + '/../client/packages/config.js');

    mergeConfig(appConfig, sharedConfig);
    return appConfig;
  };

  this.parseConfigFile = parseConfigFile;
};

module.exports = ConfigGenerator;
