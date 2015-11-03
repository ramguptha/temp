/* global console:false */
require(['config'], function (sspConfig) {

  requirejs([
    'jquery',
    'packages/platform/locale-config',
    'logger'
  ], function (
    $,
    LocaleConfig,
    logger
  ) {
    // Check that the user is currently logged on and if not switch to the login page.
    var logDebug = false;

    if (logDebug) { logger.log('Checking Login status'); }

    $.getJSON(
      '/com.absolute.am.webapi/api/ssp/login',
      function(data) {

        if (logDebug) {
          logger.log('Logged in');
        }

        // AM stores locale name as en_US instead of en-us (ditto for other locales). Transform
        // to our format.
        var locale = data.resultParameters.Locale.toLowerCase().replace('_', '-');

        LocaleConfig.setLocale(locale, function() {

          requirejs([
            'mydevices/core',
            'mydevices/router',

            'locale',
            'packages/platform/ui/global_menu_ctrl',
            'packages/platform/modal',
            'packages/platform/desktop',
            'packages/platform/button-group-component',
            'am-data',
            'packages/am/am-session',
            'packages/am/am-user-self-help-portal',
            'packages/am/am-user-formatter',
            'packages/platform/nav-page-view',

            'packages/platform/clearable-text-field-component',
            'packages/platform/nav-header-component',
            'packages/platform/data-list-component',
            'packages/platform/paged-list-component',

            'ember'
          ], function (
            App,
            Router,

            Locale,
            GlobalMenuMgr,
            Modal,
            Desktop,
            ButtonGroupComponent,
            AmData,
            AmSession,
            AmUserSelfHelpPortal,
            AmUserFormatter,
            NavPageView,

            ClearableTextFieldComponent,
            NavHeaderComponent,
            DataListComponent,
            PagedListComponent,

            Em
          ) {

            /*
             * Check that the user is currently logged on
             * and if not switch to the login page.
             */
            var logDebug = false;

            // based on https://github.com/jmurphyau/ember-truth-helpers
            Em.HTMLBars._registerHelper('compare', function(arguments) {
              var operators = {
                '==':   function(l,r) { return l == r; },
                '===':  function(l,r) { return l === r; },
                '!=':   function(l,r) { return l != r; },
                '<':    function(l,r) { return l < r; },
                '>':    function(l,r) { return l > r; },
                '<=':   function(l,r) { return l <= r; },
                '>=':   function(l,r) { return l >= r; },
                'typeof': function(l,r) { return typeof l == r; }
              };

              if (arguments.length < 3)
                throw new Error("Compare needs 3 parameters");

              return operators[arguments[2]](arguments[0], arguments[1]);
            });

            // Restrict all <input> elements to 255 characters
            Em.TextField.reopen({
              maxlength: '255'
            });

            // Allow disabling individual select options
            Ember.SelectOption.reopen({
              attributeBindings: ['value', 'selected', 'disabled'],

              disabled: function () {
                var content = this.get('content');
                return content.disabled || false;
              }.property('content'),

              selected: function () {
                var content = this.get('content');
                return content.selected || false;
              }.property('content')
            });

            window.debug = Em.Object.create({
              AmData: AmData
            });

            if (logDebug) { logger.log('PACKAGES: Initializing'); }
            AmData.initialize(App);

            App.mergePackage(Locale);
            App.mergePackage(Modal);
            App.mergePackage(Desktop);
            App.mergePackage(ButtonGroupComponent);
            App.mergePackage(AmData);
            App.mergePackage(AmUserFormatter);
            App.mergePackage(NavPageView);
            App.mergePackage(ClearableTextFieldComponent);
            App.mergePackage(NavHeaderComponent);
            App.mergePackage(DataListComponent);
            App.mergePackage(PagedListComponent);
            App.mergePackage(AmUserSelfHelpPortal);

            // Ensure that GlobalMenuMgr is initialized
            GlobalMenuMgr.getInstance();

            AmSession.set('loginUrl', '/com.absolute.am.webapi/api/ssp/login'),
              AmSession.initialize(data.resultParameters);

            // Go!
            App.advanceReadiness();
          });
        });
      }
    ).fail(
      function (details, textStatus, errorThrown) {
        if (logDebug) { logger.log('Not logged in, error: ' + textStatus + ': ' + details.status + ' - ' + errorThrown); }
        document.location = '../mylogin/?loginRequired=true';
      }
    );
  })});
