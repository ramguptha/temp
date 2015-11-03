define([
  'env',
  'packages/platform/locale-config'
], function(
  Env,
  LocaleConfig
) {
  'use strict';

  return {
    uri: function(helpPageId) {
      return this.baseUri.call() + 'Default.htm#cshid=' + helpPageId;
    },

    baseUri: function() {
      var helpRoot = Env.helpRoot;
      switch (LocaleConfig.locale()) {
        case 'ja':
          helpRoot = Env.helpRootJa;
          break;
      }
      return helpRoot;
    },

    baseUriSsp: function() {
      var helpRoot = Env.helpSspRoot;
      switch (LocaleConfig.locale()) {
        case 'ja':
          helpRoot = Env.helpSspRootJa;
          break;
      }
      return helpRoot;
    }

  };
});




