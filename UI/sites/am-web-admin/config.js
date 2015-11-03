'use strict';

(function() {
  return {
    paths: {
      slider: 'packages/platform/lib/bjqs-1.3',
      jqueryipaddress: 'packages/platform/lib/jquery.ipaddress',
      jquerycaret: 'packages/platform/lib/jquery.caret',
      contextmenu: 'packages/platform/lib/jquery.contextmenu',

      'ember-responsive': 'packages/platform/lib/ember-responsive',
      data: 'packages/platform/lib/ember-data',

      wysihtml5: 'packages/platform/lib/wysihtml5/wysihtml5-0.3.0_rc2',
      wysihtml5ParserRules: 'packages/platform/lib/wysihtml5/parser_rules/advanced',
      fileSaver: 'packages/platform/lib/fileSaver/fileSaver',
      decimal: 'packages/platform/lib/decimal'
    },

    packages: [
      'packages/platform/enum-type',
      'packages/platform/interval-type',
      'packages/platform/ip-type',
      'packages/time-type',
      'packages/version-type',
      'packages/platform/information-item',

      'packages/platform/ip-util',
      'packages/platform/version-util',
      'packages/platform/interval-util',

      'packages/am/am-app-foundation',
      'packages/am/am-assignable-item-foundation',
      'packages/am/am-assignable-item-util',
      'packages/platform/cookie',
      'packages/platform/regex',
      'packages/platform/aggregate',
      'packages/platform/help',
      'packages/platform/modern-ember',
      'packages/am/am-assignable-item',
      'packages/am/am-mobile-device',
      'packages/am/am-mobile-policy',
      'packages/am/am-multi-select',
      'packages/am/am-mobile-command',
      'packages/am/am-push-notifications',
      'packages/am/am-session',
      'packages/am/am-computer',
      'packages/am/am-data',
      'packages/am/am-desktop',
      'packages/am/am-computer-formatter',
      'packages/am/am-user-formatter',
      'packages/am/am-user-self-help-portal',
      'packages/am/availability-time-component',
      'packages/am/am-custom-field'
    ],

    map: {
      '*': {
        help: 'packages/platform/help',
        'am-desktop': 'packages/am/am-desktop',
        'am-multi-select': 'packages/am/am-multi-select',
        'am-data': 'packages/am/am-data',
        'am-session': 'packages/am/am-session',
        'am-computer-formatter': 'packages/am/am-computer-formatter'
      }
    },

    shim: {
      data: {
        deps: ['ember'],
        exports: 'DS'
      },
      jquerycaret: {
        deps: ['jquery']
      },
      jqueryipaddress: {
        deps: ['jquery', 'jquerycaret']
      },
      env: {
        deps: [],
        exports: 'AM_ENV'
      },
      contextmenu: {
        deps: ['jquery'],
        exports: '$'
      },
      'ember-responsive': {
        deps: ['ember-core'],
        exports: 'Ember'
      }
    }
  };
})();
