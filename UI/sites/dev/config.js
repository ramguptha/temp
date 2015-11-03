'use strict';

(function() {
  return {
    paths: {
      env: '/dev/tests/env',
      jqueryipaddress: 'packages/platform/lib/jquery.ipaddress',
      jquerycaret: 'packages/platform/lib/jquery.caret',
      slider: 'packages/platform/lib/bjqs-1.3',
      nestedsortable: 'packages/platform/lib/jquery.mjs.nestedSortable',

      'ember-responsive': 'packages/platform/lib/ember-responsive'
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

      'packages/platform/help',
      'packages/platform/aggregate',
      'packages/platform/cookie',
      'packages/platform/regex',
      'packages/platform/naming',

      'packages/platform/set',
      'packages/platform/pager',
      'packages/platform/queried-pager',
      'packages/platform/data-sourced-pager',

      'packages/cc/cc-session',
      'packages/cc/cc-processing-status',
      'packages/cc/cc-data',
      'packages/cc/cc-desktop',

      'packages/cc/cc-device',
      'packages/cc/cc-device-foundation',

      'packages/cc/cc-reports',

      'packages/cc/cc-policies-foundation',
      'packages/cc/cc-policies',

      'packages/cc/cc-software-titling-foundation',
      'packages/cc/cc-software-titling',

      'packages/cc/cc-app-foundation'
    ],
    map: {
      '*': {
        help: 'packages/platform/help',
        'advanced-filter': 'packages/platform/advanced-filter',
        regex: 'packages/platform/regex'
      }
    },
    shim: {
      slider: {
        deps: ['jquery'],
        exports: '$'
      },
      jqueryipaddress: {
        deps: ['jquery', 'jquerycaret']
      },
      env: {
        deps: [],
        exports: 'ABS_ENV'
      },
      'ember-responsive': {
        deps: ['ember-core'],
        exports: 'Ember'
      }
    }
  };
})();
