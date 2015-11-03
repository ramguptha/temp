/* global console:false */
require(['config'], function (loginConfig) {

  requirejs([
    'config',
    'jquery',
    'purl',
    'spinnercore',
    'handlebars',
    'ember',
    'packages/am/am-session',
    'packages/platform/modal',
    'packages/platform/ajax',
    'ui',
    // We must require the resources for the root locale so that they are built into releases
    'packages/am/am-user-login/nls/strings',
    'packages/am/am-user-login/nls/config',
    'text!packages/am/am-user-login/lib/templates/application.handlebars'
  ], function(
    loginConfig,
    $,
    shim,
    Spinner,
    Handlebars,
    Em,
    AmSession,
    Modal,
    Ajax,
    UI,
    strings,
    config,
    loginTemplate
  ) {
    'use strict';

    var logDebug = false;

    // Restrict all <input> elements to 255 characters
    Em.TextField.reopen({
      maxlength: '255'
    });

    // Do browser compatibility check before launching app
    // Check that input type file is supported. Note that it is not sufficient to just check that the file type
    // attribute 'sticks' or that the 'disabled' attribute is false as some web articles claim.
    var inputElem = document.createElement('input');
    inputElem.setAttribute('type', 'file');
    var bool = inputElem.type !== 'text';
    var supportsFileType = inputElem.files == '[object FileList]';

    // Safari reports that it supports FileList and it's not disabled, even though it's only actually supported
    // on 6.x or later, so we have to do a specific check for earlier versions of Safari.
    if (supportsFileType) {
      var browserVersion = window.navigator.appVersion;
      if (browserVersion.indexOf('Safari') !== -1) {
        var searchStr = 'Version/';
        var idx = browserVersion.indexOf(searchStr);
        if (idx !== -1) {
          supportsFileType = browserVersion.charAt(idx + searchStr.length) > '5';
        }
      }
    }

    if (!supportsFileType) {
      document.location = '../browser-error/';
    }

    // By default, loginMessage is not displayed
    var loginMessagePath = null;
    if ($.url().param('loginRequired') === 'true') {
      loginMessagePath = 'userLogin.loginMessage';
    } else if ($.url().param('loggedOut') === 'true') {
      loginMessagePath = 'userLogin.loggedOutMessage';
    }

    var errorMessagePath = null;
    if ($.url().param('sessionTimedOut') === 'true') {
      errorMessagePath = 'userLogin.sessionTimeoutMessage';
    }

    // Application Controller
    // ----------------------

    var ApplicationController = Em.Controller.extend({
      actions: {
        login: function() {
          this.setProperties({ errorMessagePath: null, successMessagePath: null, ajaxInProgress: true });

          // Absolute Manage deals with locale names in the following format: en_US. We deal with them
          // like this: en-us. Transform from our format to Absolute Manage's.
          var amLocaleName = this.formatLocaleNameUpperCase(this.get('localeName'));

          Ajax.post(
            'Log in',
            '/com.absolute.am.webapi/api/ssp/login',
            'application/x-www-form-urlencoded; charset=UTF-8',
            {
              Domain: this.get('domainName'),
              DefaultServerName: this.get('serverName'),
              DefaultServerPort: this.get('serverPort'),
              UserName: this.get('username'),
              Password: this.get('password'),
              Locale: amLocaleName
            },
            this.loginSucceeded.bind(this),
            this.loginFailed.bind(this)
          );
        },

        forgotPassword: function() {
          this.set('errorMessagePath', 'userLogin.forgotPasswordDescription');
        }

      },

      formatLocaleNameUpperCase: function(localeName) {
        var amLocaleName = localeName;
        var localeNameParts = localeName.split('-');
        if (localeNameParts.length > 1) {
          localeNameParts[1] = localeNameParts[1].toUpperCase();
          amLocaleName = localeNameParts.join('_');
        }
        return amLocaleName;
      },

      formatShortLocaleName: function(localeName) {
        var amLocaleName = localeName;
        var localeNameParts = localeName.split('-');
        if (localeNameParts.length > 1) {
          amLocaleName = localeNameParts[0];
        }
        return amLocaleName;
      },

      ajaxInProgress: false,

      errorMessagePath: errorMessagePath,
      errorMessageContext: {},
      errorMessage: function() {
        var message = null;

        var errorMessagePath = this.get('errorMessagePath');
        var translationTemplates = this.get('translationTemplates');
        if (!Em.isNone(errorMessagePath) && translationTemplates) {
          var template = Em.get(translationTemplates, errorMessagePath);
          if (!Em.isNone(template)) {
            message = template(this.get('errorMessageContext'));
          }
        }

        return message;
      }.property('errorMessagePath', 'errorMessageContext', 'translationTemplates'),

      successMessagePath: loginMessagePath,
      successMessage: function() {
        var message = null;

        var successMessagePath = this.get('successMessagePath');
        var translations = this.get('translations');
        if (!Em.isNone(successMessagePath) && translations) {
          message = Em.get(translations, successMessagePath);
        }

        return message;
      }.property('successMessagePath', 'translations'),

      serverName: null,
      serverPort: 3971,
      serverNameAndPortRequired: false,
      isServerNameAndPortEmpty: false,

      isLoginDisabled: function() {
        var controller = this;

        var fieldList = this.get('serverNameAndPortRequired') ? 'domainName username password serverName serverPort' :  'domainName username password'
        return fieldList.w().some(function(name) {
          var val = controller.get(name);

          if (val === undefined) {
            return false;
          }

          return !val || val.length === 0;
        });
      }.property('domainName', 'username', 'password', 'serverName', 'serverPort', 'serverNameAndPortRequired'),

      isDisplayServerNameAndPort: function() {
        return this.get('isServerNameAndPortEmpty') || this.get('serverNameAndPortRequired');
      }.property('isServerNameAndPortEmpty', 'serverNameAndPortRequired'),

      setIsServerNameAndPortEmpty: function() {
        this.set('isServerNameAndPortAreNotEmpty',
          Em.isEmpty(this.get('serverName')) || Em.isEmpty(this.get('serverPort')));
      }.observes('serverName', 'serverPort'),

      localeName: 'en-US',
      availableLocales: [{ name: 'en-us', label: 'English (US)' }, { name: 'ja', label: '日本語' }],

      // Rendered locale strings
      translations: null,

      // Raw locale strings
      localeStrings: null,

      // Locale-specific settings
      localeConfig: null,

      init: function() {
        this._super();

        this.getDefaultServerInfo();

        var localeParam = location.search.split('locale=')[1];
        if(localeParam) {
          // Get rid of localization query string parameter after using
          window.history.replaceState( {} , 'mylogin/', '/mylogin/' );
        }

        var storedLocaleName = localeParam ? localeParam : this.getCookie('localeName');
        // English is by default
        var localeNameFound = 'en-us';
        // Try to find stored in cookies locale
        if (!Em.isEmpty(storedLocaleName)) {
          this.get('availableLocales').some(function(item) {
            if( item.name === storedLocaleName ) {
              localeNameFound = storedLocaleName;
              return true;
            }
          });
          // Store if something is found or default English version
          this.set('localeName', localeNameFound);
          return;
        }

        // If no cookies for locale found, try default language
        var defaultLocaleName = (window.navigator.language || window.navigator.userLanguage).toLowerCase();
        if (!Em.isEmpty(defaultLocaleName)) {
          // Use short locale name for all non English locale (ja)
          if( defaultLocaleName !== 'en-us' && defaultLocaleName !== 'en-ca' && defaultLocaleName !== 'en-gb') {
            defaultLocaleName = this.formatShortLocaleName(defaultLocaleName);
          }
          this.get('availableLocales').some(function(item) {
            if( item.name === defaultLocaleName ) {
              localeNameFound = defaultLocaleName;
              return true;
            }
          });
        }

        // Store if something is found or default English version
        this.set('localeName', localeNameFound);

      },

      getCookie: function(cookieName) {
        var cookieMatcher = cookieName + '=';

        var allCookies = document.cookie;
        var cookie = null;
        var start = allCookies.indexOf(cookieMatcher);
        if (start !== -1) {
          start += cookieMatcher.length;
          var end = allCookies.indexOf(';', start);
          if (end === -1) {
            end = allCookies.length;
          }
          cookie = allCookies.substring(start, end);
        }
        return cookie;
      },

      setCookie: function(name, value) {
        var COOKIE_MAX_AGE = 2147483648;

        var tenYears = new Date();
        tenYears.setUTCFullYear(tenYears.getUTCFullYear() + 10);

        document.cookie = name + '=' + String(value) + ';max-age=' + COOKIE_MAX_AGE + ';expires=' + tenYears.toUTCString() + ';';
      },

      clearCookie: function(name) {
        document.cookie = name + '=';
      },

      loginSucceeded: function(data) {
        this.set('ajaxInProgress', false);
        if (this.get('rememberMe')) {
          this.setCookie('rememberMe', true);

          this.setCookie('domainName', encodeURIComponent(this.get('domainName')));
          this.setCookie('username', encodeURIComponent(this.get('username')));
        } else {
          this.setCookie('rememberMe', false);

          this.clearCookie('domainName');
          this.clearCookie('username');
        }

        AmSession.set('loginUrl', '/com.absolute.am.webapi/api/ssp/login'),
          AmSession.initialize(data.resultParameters);

        document.location = '../mydevices/';
      },

      loginFailed: function(details, textStatus, errorThrown) {
        var httpStatusCode = details.jqXHR.status;
        var httpStatusName = textStatus;
        var httpErrorThrown = (undefined !== errorThrown) ? errorThrown : details.errorThrown;

        var errorMessagePath = null;
        var errorMessageContext = {};

        switch (httpStatusCode) {
          case 401:
            errorMessagePath = 'userLogin.errors.invalidUserOrPassword';
            break;
          case 503:
            errorMessagePath = 'userLogin.errors.serverUnavailable';
            break;
          default:
            if (!Em.isEmpty(httpErrorThrown)) {
              errorMessagePath = 'userLogin.errors.errorText';
              errorMessageContext = { text: httpErrorThrown };
            } else {
              errorMessagePath = 'userLogin.errors.statusNameAndCode';
              errorMessageContext = { name: httpStatusName, code: httpStatusCode };
            }
            break;
        }

        this.setProperties({
          ajaxInProgress: false,
          successMessagePath: null,
          errorMessagePath: errorMessagePath,
          errorMessageContext: errorMessageContext
        });
      },

      localeNameDidChange: function() {
        var localeName = this.get('localeName');

        this.loadLocaleResources(localeName);
        this.setCookie('localeName', localeName);
      }.observes('localeName').on('init'),

      loadLocaleResources: function(localeName) {
        var self = this;

        var root = 'packages/am/am-user-login/nls/';

        var initializeLocale = function(localeStrings, localeConfig) {
          var translationTemplates = self.transformTree(
            localeStrings,
            function(value) { return 'string' === typeof(value); },
            function(string) { return Handlebars.compile(string); }
          );

          self.setProperties({
            translationTemplates: translationTemplates,
            translations: self.transformTree(
              translationTemplates,
              function(value) { return 'function' === typeof(value); },
              function(template) { return template(); }
            ),

            localeStrings: localeStrings,
            localeConfig: localeConfig
          });
        };

        if (Em.isEmpty(localeName) || 'en-us' === localeName.toLowerCase()) {
          require([root + 'strings', root + 'config'], function(rootLocaleStrings, rootLocaleConfig) {
            initializeLocale(rootLocaleStrings.root, rootLocaleConfig.root);
          });
        } else {
          require([root + localeName + '/strings', root + localeName + '/config'], initializeLocale);
        }
      },

      transformTree: function(strings, test, transform) {
        var translations = {};

        for (var name in strings) {
          if (strings.hasOwnProperty(name)) {
            var value = strings[name];

            if ('object' === typeof(value)) {
              translations[name] = this.transformTree(value, test, transform);
            } else if (test(value)) {
              translations[name] = transform(value);
            } else throw ['Unknown translation type', name, value];
          }
        }

        return translations;
      },

      getDefaultServerInfo: function () {
        $.get('/com.absolute.am.webapi/api/ssp/defaultServerInfo',
          this.getDefaultServerInfoSucceeded.bind(this))
          .fail(this.getDefaultServerInfoFailed.bind(this));
      },

      getDefaultServerInfoSucceeded: function(data) {
        this.set('ajaxInProgress', false);

        this.setProperties({
          serverName: data.serverName,
          serverNameAndPortRequired: Em.isNone(data.serverName) || Em.isNone(data.serverPort)
        });

        if(data.serverPort) {
          this.set('serverPort', data.serverPort)
        }
      },

      getDefaultServerInfoFailed: function(details, textStatus, errorThrown) {
        var httpStatusCode = details.jqXHR.status;
        var httpStatusName = textStatus;
        var httpErrorThrown = (undefined !== errorThrown) ? errorThrown : details.errorThrown;

        var errorMessagePath = null;
        var errorMessageContext = {};

        // TODO verify errors
        switch (httpStatusCode) {
          case 401:
            errorMessagePath = 'userLogin.errors.invalidUserOrPassword';
            break;
          case 503:
            errorMessagePath = 'userLogin.errors.serverUnavailable';
            break;
          default:
            if (!Em.isEmpty(httpErrorThrown)) {
              errorMessagePath = 'userLogin.errors.errorText';
              errorMessageContext = { text: httpErrorThrown };
            } else {
              errorMessagePath = 'userLogin.errors.statusNameAndCode';
              errorMessageContext = { name: httpStatusName, code: httpStatusCode };
            }
            break;
        }

        this.setProperties({
          ajaxInProgress: false,
          successMessagePath: null,
          errorMessagePath: errorMessagePath,
          errorMessageContext: errorMessageContext
        });
      }

    });

    // Application View
    // ----------------

    var ApplicationView = Em.View.extend({
      template: Em.Handlebars.compile(loginTemplate),

      didInsertElement: function() {
        var ctrl = this.get('controller');
        var self = this;
        var rememberMe = ctrl.getCookie('rememberMe');
        if (rememberMe == 'true') {
          ctrl.setProperties({
            domainName: decodeURIComponent(ctrl.getCookie('domainName')),
            username: decodeURIComponent(ctrl.getCookie('username')),
            rememberMe: true
          });
          UI.setFocus(this.$('#Password'));
        } else {
          ctrl.set('rememberMe', false);
          UI.setFocus(this.$('#DomainName'));
        }

        var spinner = new Spinner({
          lines: 9,
          length: 0,
          width: 3,
          radius: 6,
          corners: 1,
          rotate: 0,
          trail: 56,
          speed: 1.6
        });
        this.set('spinner', spinner);

        this.$('#LoginContainer').on('click focus', 'input', function() {
          var errorMessagePath = self.get('controller').get('errorMessagePath');
          if (!Em.isNone(errorMessagePath)) {
            self.get('controller').set('errorMessagePath', null);
          }
        })
      },

      willDestroyElement: function() {
        this.$('#LoginContainer').off();
      },

      showAjaxStatus: function() {
        if ('inDOM' !== this.get('state')) {
          return;
        }

        var inProgress = this.get('controller.ajaxInProgress');
        var spinner = this.get('spinner');
        this.$('.spinner-login').each(function(i, elt) {
          if (inProgress) {
            spinner.spin(elt);
          } else {
            spinner.stop(elt);
          }
        });
      }.observes('controller.ajaxInProgress')
    });

    // The Application
    // ---------------

    window.App = Em.Application.create({
      VERSION: '0.0.1',
      autoinit: false,
      
      ApplicationRoute: UI.ApplicationRoute,
      ApplicationController: ApplicationController,
      ApplicationView: ApplicationView,

      ModalsService: Modal.appClasses.ModalsService
    });
  })
});
