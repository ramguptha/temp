/* global console:false */
require(['config'], function (loginConfig) {

  require([
    'config',
    'jquery',
    'purl',
    'handlebars',
    'ember',
    'packages/am/am-session',
    'packages/platform/modal',
    'packages/platform/ajax',
    'ui',
    // We must require the resources for the root locale so that they are built into releases
    'packages/am/login/nls/strings',
    'packages/am/login/nls/config',
    'logger',
    'text!packages/am/login/lib/templates/application.handlebars'
  ], function(
    loginConfig,
    $,
    shim,
    Handlebars,
    Em,
    AmSession,
    Modal,
    Ajax,
    UI,
    strings,
    config,
    logger,
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
      loginMessagePath = 'login.loginMessage';
    } else if ($.url().param('loggedOut') === 'true') {
      loginMessagePath = 'login.loggedOutMessage';
    }

    var errorMessagePath = null;
    if ($.url().param('sessionTimedOut') === 'true') {
      errorMessagePath = 'login.sessionTimeoutMessage';
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
            '/com.absolute.am.webapi/api/login',
            'application/x-www-form-urlencoded; charset=UTF-8',
            {
              ServerName: this.get('serverName'),
              ServerPort: this.get('serverPort'),
              UserName: this.get('username'),
              Password: this.get('password'),
              Locale: amLocaleName
            },
            this.loginSucceeded.bind(this),
            this.loginFailed.bind(this)
          );
        },

        forgotPassword: function() {
          this.set('errorMessagePath', 'login.forgotPasswordDescription');
        },

        gotoUserLoginPage: function() {
          var amLocaleName = this.get('localeName');
          window.document.location.assign('../mylogin/' + '?locale=' + amLocaleName);
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
            message = Em.String.htmlSafe(template(this.get('errorMessageContext')));
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

      isLoginDisabled: function() {
        var controller = this;
        return 'serverName serverPort username password'.w().some(function(name) {
          var val = controller.get(name);

          if (val === undefined) {
            return false;
          }

          return !val || val.length === 0;
        });
      }.property('serverName', 'serverPort', 'username', 'password'),

      serverPort: 3971,

      localeName: 'en-US',
      availableLocales: [{ name: 'en-us', label: 'English (US)' }, { name: 'ja', label: '日本語' }],

      // Rendered locale strings
      translations: null,

      // Raw locale strings
      localeStrings: null,

      // Locale-specific settings
      localeConfig: null,

      SSPLoginEnabled: true,

      init: function() {
        this._super();
        var self = this;

        // disable SSP if it's forbidden
        $.get('/com.absolute.am.webapi/api/ssp/login')
          .always(function(data) {
            if(data.status === 403) {
              self.set('SSPLoginEnabled', false);
            }
          });

        var storedLocaleName = this.getCookie('localeName');
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
        if (this.logDebug) {
          logger.log(data)
        };

        this.set('ajaxInProgress', false);
        if (this.get('rememberMe')) {
          this.setCookie('rememberMe', true);

          this.setCookie('serverName', encodeURIComponent(this.get('serverName')));
          this.setCookie('serverPort', this.get('serverPort'));
          this.setCookie('username', encodeURIComponent(this.get('username')));
        } else {
          this.setCookie('rememberMe', false);

          this.clearCookie('serverName');
          this.clearCookie('serverPort');
          this.clearCookie('username');
        }

        AmSession.initialize(data.resultParameters);

        if (AmSession.hasMcmAccessRights()) {
          document.location = '../am/';
        } else {
          this.setProperties({
            successMessagePath: null,
            errorMessagePath: 'login.errors.unsufficientAccessRights'
          });
        }
      },

      loginFailed: function(details, textStatus, errorThrown) {
        if (this.logDebug) {
          logger.log(details, textStatus, errorThrown);
        }

        var httpStatusCode = details.jqXHR.status;
        var httpStatusName = textStatus;
        var httpErrorThrown = (undefined !== errorThrown) ? errorThrown : details.errorThrown;

        var errorMessagePath = null;
        var errorMessageContext = {};

        switch (httpStatusCode) {
          case 401:
            errorMessagePath = 'login.errors.invalidUserOrPassword';
            break;
          case 503:
            errorMessagePath = 'login.errors.serverUnavailable';
            break;
          default:
            if (!Em.isEmpty(httpErrorThrown)) {
              errorMessagePath = 'login.errors.errorText';
              errorMessageContext = { text: httpErrorThrown };
            } else {
              errorMessagePath = 'login.errors.statusNameAndCode';
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

        var root = 'packages/am/login/nls/';

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
            serverName: decodeURIComponent(ctrl.getCookie('serverName')),
            serverPort: ctrl.getCookie('serverPort'),
            username: decodeURIComponent(ctrl.getCookie('username')),
            rememberMe: true
          });
          UI.setFocus(this.$('#Password'));
        } else {
          ctrl.set('rememberMe', false);
          UI.setFocus(this.$('#ServerName'));
        }

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
            $(this).append('<span class="spinner spinner-small"></span>');
          } else {
            $(this).remove()
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
  });
});
