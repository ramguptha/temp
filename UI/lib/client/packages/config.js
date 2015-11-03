'use strict';

(function() {
  return {
    paths: {
      moment: 'packages/platform/lib/moment-2.8.2/moment',
      'moment-locales': 'packages/platform/lib/moment-2.8.2/locale',
      jsworld: 'packages/platform/lib/JsWorld-2.8.1/JsWorld',
      'jsworld-locales': 'packages/platform/lib/JsWorld-2.8.1/locales/js',

      jquery: 'packages/platform/lib/jquery-1.10.2',
      jqueryui: 'packages/platform/lib/jquery-ui-1.10.3.custom',
      select2: 'packages/platform/lib/select2/select2',
      jqgridcore: 'packages/platform/lib/jqGrid-4.6/js/jquery.jqGrid.src',
      jqgrid: 'packages/platform/lib/jqGrid-4.6/js/i18n/grid.locale-en',
      spinnercore: 'packages/platform/lib/spin',
      spinner: 'packages/platform/lib/spinner',
      purl: 'packages/platform/lib/purl',
      tipsy: 'packages/platform/lib/jquery.tipsy',
      squire: 'packages/platform/lib/Squire',
      atmosphere: 'packages/platform/lib/atmosphere',

      'ember-core': 'packages/platform/lib/ember-1.13.8/ember.debug',
      'ember-template-compiler': 'packages/platform/lib/ember-1.13.8/ember-template-compiler',
      handlebars: 'packages/platform/lib/handlebars-1.3.0',
      'ember-states': 'packages/platform/lib/ember-states',

      radioButtonGroup: 'packages/platform/lib/ember-radioButtonGroup',
      printstacktrace: 'packages/platform/lib/stacktrace',
      timepicker: 'packages/platform/lib/jquery-ui-timepicker-addon',
      tree: 'packages/platform/lib/tree.jquery',

      text: 'packages/platform/lib/text',
      i18n: 'packages/platform/lib/i18n'
    },
    packages: [
      'packages/platform/date-type',
      
      'packages/platform/number-type',

      'packages/platform/enum-util',

      'packages/platform/offset-monitor',
      'packages/platform/child-controller',
      'packages/platform/session',
      'packages/platform/ember-env',
      'packages/platform/ember-overrides',
      'packages/platform/locale-config',
      'packages/platform/locale',
      'packages/platform/logger',
      'packages/platform/guid',
      'packages/platform/uploader',
      'packages/platform/modal',
      'packages/platform/ui',
      'packages/platform/query',
      'packages/platform/advanced-filter',
      'packages/platform/formatter',
      'packages/platform/formats',
      'packages/platform/ajax',
      'packages/platform/activity-monitor',
      'packages/platform/send-ember-action',
      'packages/platform/desktop',
      'packages/platform/data',
      'packages/platform/tipsy-extensions',
      'packages/platform/testing',
      'packages/platform/storage',
      'packages/platform/set',
      'packages/platform/sink',
      'packages/platform/single-enumerable',
      'packages/platform/tracer',
      'packages/platform/selection',
      'packages/platform/apply-styles',
      'packages/platform/bound-alias-shim',

      'packages/platform/nth-day-of-month-picker-view',
      'packages/platform/presenter',
      'packages/platform/is-data-presenter',
      'packages/platform/async-status',
      'packages/platform/counter',
      'packages/platform/queried-counter',
      'packages/platform/data-counter',
      'packages/platform/count-status-component',
      'packages/platform/pager',
      'packages/platform/queried-pager',
      'packages/platform/data-pager',
      'packages/platform/paged-component',
      'packages/platform/paged-list-component',
      'packages/platform/paged-table-component',
      'packages/platform/data-list-component',
      'packages/platform/data-table-component',
      'packages/platform/data-table-controller',
      'packages/platform/adhoc-search-component',
      'packages/platform/collection-header-component',
      'packages/platform/page-view',
      'packages/platform/nav-page-view',
      'packages/platform/nav-header-component',
      'packages/platform/vertical-splitter-component',
      'packages/platform/button-group-component',
      'packages/platform/button-group-horizontal-component',
      'packages/platform/context-menu-component',
      'packages/platform/clearable-text-field-component',
      'packages/platform/data-poller',

      'packages/platform/app-foundation'
    ],
    map: {
      '*': {
        locale: 'packages/platform/locale',
        ember: 'packages/platform/ember-overrides',
        logger: 'packages/platform/logger',
        testing: 'packages/platform/testing',
        guid: 'packages/platform/guid',
        query: 'packages/platform/query',
        filter: 'packages/platform/advanced-filter',
        ui: 'packages/platform/ui',
        desktop: 'packages/platform/desktop',
        uploader: 'packages/platform/uploader',
        formatter: 'packages/platform/formatter',
        formats: 'packages/platform/formats'
      }
    },
    shim: {
      jsworld: {
        exports: 'jsworld'
      },
      jquery: {
        exports: '$'
      },
      jqueryui: {
        deps: ['jquery'],
        exports: '$'
      },
      select2: {
        deps: ['jquery'],
        exports: '$'
      },
      tree: {
        deps: ['jquery'],
        exports: '$'
      },
      jqgridcore: {
        deps: ['jquery']
      },
      jqgrid: {
        deps: ['jqgridcore'],
        exports: '$'
      },
      handlebars: {
        deps: ['jquery'],
        exports: 'Handlebars'
      },
      'ember-core': {
        deps: ['jquery', 'handlebars', 'packages/platform/ember-env'],
        exports: 'Ember'
      },
      'ember-states': {
        deps: ['ember-core'],
        exports: 'Ember'
      },
      radioButtonGroup: {
        deps: ['ember'],
        exports: 'Ember'
      },
      printstacktrace: {
        exports: 'printStackTrace'
      },
      spinnercore: {
        exports: 'Spinner'
      },
      spinner: {
        exports: '$',
        deps: ['spinnercore', 'jquery']
      },
      purl: {
        deps: ['jquery'],
        exports: '$'
      },
      timepicker: {
        deps: ['jqueryui'],
        exports: '$'
      },
      tipsy: {
        deps: ['jquery'],
        exports: '$'
      },
      atmosphere: {
        deps: ['jquery'],
        exports: 'atmosphere'
      }
    }
  };
})();
