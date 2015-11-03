/* global console:false */
require(['config'], function (amConfig) {

  requirejs([
    'jquery',
    'packages/platform/locale-config'
  ], function (
    $,
    LocaleConfig
  ) {
    // Check that the user is currently logged on and if not switch to the login page.
    $.getJSON(
      '/com.absolute.am.webapi/api/login',
      function(data) {

        // AM stores locale name as en_US instead of en-us (ditto for other locales). Transform
        // to our format.
        var locale = data.resultParameters.Locale.toLowerCase().replace('_', '-');

        LocaleConfig.setLocale(locale, function() {

          requirejs([
            'am/core',
            'am/router',

            'locale',
            'packages/platform/ui/global_menu_ctrl',
            'packages/platform/modal',
            'packages/platform/desktop',
            'packages/platform/button-group-component',
            'packages/am/am-desktop',
            'packages/am/am-data',
            'packages/am/am-assignable-item',
            'packages/am/am-assignable-item-foundation',
            'packages/am/am-mobile-device',
            'packages/am/am-mobile-policy',
            'packages/am/am-mobile-command',
            'packages/am/am-session',
            'packages/am/am-computer',
            'packages/am/am-computer-formatter',
            'packages/am/am-push-notifications',
            'packages/am/availability-time-component',
            'packages/am/am-custom-field',

            'packages/platform/clearable-text-field-component',
            'packages/platform/count-status-component',
            'packages/platform/paged-list-component',
            'packages/platform/paged-table-component',
            'packages/platform/data-list-component',
            'packages/platform/data-table-component',
            'packages/platform/adhoc-search-component',
            'packages/platform/nav-header-component',
            'packages/platform/collection-header-component',
            'packages/platform/vertical-splitter-component',
            'packages/platform/button-group-horizontal-component',
            'packages/platform/nav-page-view',

            'ember'
          ], function (
            App,
            Router,

            Locale,
            GlobalMenuMgr,
            Modal,
            Desktop,
            ButtonGroupComponent,
            AmDesktop,
            AmData,
            AmAssignableItem,
            AmAssignableItemFoundation,
            AmMobileDevice,
            AmMobilePolicy,
            AmMobileCommand,
            AmSession,
            AmComputer,
            AmComputerFormatter,
            AmPushNotifications,
            AvailabilityTimeComponent,
            AmCustomField,

            ClearableTextFieldComponent,
            CountStatusComponent,
            PagedListComponent,
            PagedTableComponent,
            DataListComponent,
            DataTableComponent,
            AdhocSearchComponent,
            NavHeaderComponent,
            CollectionHeaderComponent,
            VerticalSplitterComponent,
            ButtonGroupHorizontalComponent,
            NavPageView,

            Em
          ) {

            /*
             * Check that the user is currently logged on
             * and if not switch to the login page.
             */
            // based on https://github.com/jmurphyau/ember-truth-helpers
            Em.HTMLBars._registerHelper('compare', Ember.HTMLBars.makeBoundHelper(function(arguments) {
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
            }));

            // Restrict all <input> elements to 255 characters
            Em.TextField.reopen({
              maxlength: '255'
            });

            // Allow disabling individual select options
            Em.SelectOption.reopen({
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
              AmData: AmData,
              AmMobileDevice: AmMobileDevice,
              AmMobilePolicy: AmMobilePolicy,
              AmComputer: AmComputer
            });

            AmData.initialize(App);

            App.mergePackage(Locale);
            App.mergePackage(Modal);
            App.mergePackage(Desktop);
            App.mergePackage(ButtonGroupComponent);
            App.mergePackage(AmDesktop);
            App.mergePackage(AmData);
            App.mergePackage(AmMobileDevice);
            App.mergePackage(AmMobilePolicy);
            App.mergePackage(AmAssignableItem);
            App.mergePackage(AmAssignableItemFoundation);
            App.mergePackage(AmMobileCommand);
            App.mergePackage(AmComputer);
            App.mergePackage(AmComputerFormatter);
            App.mergePackage(AvailabilityTimeComponent);
            App.mergePackage(AmCustomField);

            App.mergePackage(ClearableTextFieldComponent);
            App.mergePackage(CountStatusComponent);
            App.mergePackage(PagedListComponent);
            App.mergePackage(PagedTableComponent);
            App.mergePackage(DataListComponent);
            App.mergePackage(DataTableComponent);
            App.mergePackage(AdhocSearchComponent);
            App.mergePackage(NavHeaderComponent);
            App.mergePackage(CollectionHeaderComponent);
            App.mergePackage(VerticalSplitterComponent);
            App.mergePackage(ButtonGroupHorizontalComponent);
            App.mergePackage(NavPageView);

            // Ensure that GlobalMenuMgr is initialized
            GlobalMenuMgr.getInstance();

            AmSession.initialize(data.resultParameters);
            AmPushNotifications.initialize();

            // Go!
            App.advanceReadiness();
          });
        });
      }
    ).fail(
      function (details, textStatus, errorThrown) {
        document.location = '../login/?loginRequired=true';
      }
    );
  });

});
