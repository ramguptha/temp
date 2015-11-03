define([
  'ember',
  './lib/namespace',
  'packages/platform/ajax',
  'packages/platform/send-ember-action'
], function(
  Em,
  AmSession,
  Ajax,
  sendEmberAction
) {
  'use strict';

  return AmSession.reopen({
    loginData: null,

    sessionIsActive: false,
    sessionIsExpiring: false,
    sessionOkButtonClicked: false,

    sessionTimeoutMsecs: 0,
    lastAliveTime: new Date().getTime(),
    expiryWarnMsecs: 65 * 1000,
    sessionCheckMsecs: 60 * 1000,

    checkTimeout: null,
    checkTimeoutHandle: null,
    loginUrl: '/com.absolute.am.webapi/api/login',

    initialize: function(loginData) {
      this.set('loginData', loginData);

      AmSession.set('sessionTimeoutMsecs', loginData.SessionTimeout * 1000);

      'keypress click'.w().forEach(function(name) {
        Em.$(document).bind(name, this.kickSessionActivity);
      }, this);

      this.set('checkTimeout', function() {
        // If user activity has occurred within the check interval, invoke login api to keep the session alive
        if (AmSession.get('sessionIsActive')) {
          // Session check on time only, till the next click
          AmSession.set('sessionIsActive', false);

          // Reset timer to display dialog after 10 minutes next time (sessionTimeoutMsecs = loginData.SessionTimeout * 1000))
          AmSession.resetTimer();
          AmSession.startTimer();

          AmSession.set('lastAliveTime', new Date().getTime());
          AmSession.doSessionCheck();
        } else {
          // Otherwise warn the user that their session will expire within the warning interval
          var timeNow = new Date().getTime();
          if (!AmSession.get('sessionIsExpiring') &&
            (timeNow - AmSession.get('lastAliveTime')) >= (AmSession.get('sessionTimeoutMsecs') - AmSession.get('expiryWarnMsecs'))) {
            AmSession.set('sessionIsExpiring', true);
            sendEmberAction('sessionExpiring');
          }
        }
      });

      this.startTimer();
    },

    startTimer: function() {
      this.set('checkTimeoutHandle', window.setInterval(this.get('checkTimeout'), this.get('sessionCheckMsecs')));
    },

    resetTimer: function() {
      window.clearInterval(this.get('checkTimeoutHandle'));
      this.set('checkTimeoutHandle', null);
    },

    kickSessionActivity: function() {
      // Mark sessionIsActive and do SessionCheck only one time during regular timer cycle for 1 minute (sessionCheckMsecs)
      AmSession.set('sessionIsActive', true);

      // Do session check immediately only if OK button is clicked
      if(AmSession.get('sessionOkButtonClicked')) {
        AmSession.set('sessionIsActive', false);
        AmSession.set('sessionOkButtonClicked', false);

        // Reset timer to display dialog after 10 minutes next time (sessionTimeoutMsecs = loginData.SessionTimeout * 1000))
        AmSession.resetTimer();
        AmSession.startTimer();

        AmSession.set('lastAliveTime', new Date().getTime());
        AmSession.doSessionCheck();
      }

    },

    doSessionCheck: function() {
      Ajax.get(
        'Log in Get (Session check)',
        AmSession.get('loginUrl'),
        {},
        function(data) {
          AmSession.set('sessionIsExpiring', false);
        },
        function(details, textStatus, errorThrown) {
          if (details.status == 401) {
            document.location = '../login/?sessionTimedOut=true';
          }
        }
      );
    },

    hasMcmAccessRights: function() {
      // no longer checking for specific permissions on login
      // let the backend worry about permissions
      /*var adminInfo = this.get('loginData.AdminInfo');
      var hasAccess = adminInfo.AllowModifyMobileMedia == 1 &&
        adminInfo.AllowModifyiOSPolicies == 1 &&
        adminInfo.AllowManageiOSDevices == 1;*/

      return true;
    },

    hasInstallConfigProfilePermission: function() {
      var commandPermissionsHi = this.get('loginData.AdminInfo.CommandPermissionsHigh32');
      return (commandPermissionsHi & (1<<35));
    },

    hasUninstallConfigProfilePermission: function() {
      var commandPermissionsHi = this.get('loginData.AdminInfo.CommandPermissionsHigh32');
      return (commandPermissionsHi & (1<<36));
    },

    hasInstallProvisioningProfilePermission: function() {
      var commandPermissionsHi = this.get('loginData.AdminInfo.CommandPermissionsHigh32');
      return (commandPermissionsHi & (1<<37));
    },

    hasUninstallProvisioningProfilePermission: function() {
      var commandPermissionsHi = this.get('loginData.AdminInfo.CommandPermissionsHigh32');
      return (commandPermissionsHi & (1<<38));
    },

    hasLockDevicePermission: function() {
      var commandPermissionsHi = this.get('loginData.AdminInfo.CommandPermissionsHigh32');
      return (commandPermissionsHi & (1<<39));
    },

    hasClearPasscodePermission: function() {
      var commandPermissionsHi = this.get('loginData.AdminInfo.CommandPermissionsHigh32');
      return (commandPermissionsHi & (1<<40));
    },

    hasRemoteErasePermission: function() {
      var commandPermissionsHi = this.get('loginData.AdminInfo.CommandPermissionsHigh32');
      return (commandPermissionsHi & (1<<41));
    },

    hasUpdateDeviceInfoPermission: function() {
      var commandPermissionsHi = this.get('loginData.AdminInfo.CommandPermissionsHigh32');
      return (commandPermissionsHi & (1<<42));
    },

    hasSendMessagePermission: function() {
      var commandPermissionsHi = this.get('loginData.AdminInfo.CommandPermissionsHigh32');
      return (commandPermissionsHi & (1<<43));
    },

    hasInstallApplicationPermission: function() {
      var commandPermissionsHi = this.get('loginData.AdminInfo.CommandPermissionsHigh32');
      return (commandPermissionsHi & (1<<44));
    },

    hasUninstallApplicationPermission: function() {
      var commandPermissionsHi = this.get('loginData.AdminInfo.CommandPermissionsHigh32');
      return (commandPermissionsHi & (1<<45));
    },

    hasSetRoamingOptionsPermission: function() {
      var commandPermissionsHi = this.get('loginData.AdminInfo.CommandPermissionsHigh32');
      return (commandPermissionsHi & (1<<46));
    },

    hasChangeAppConfigPermission: function() {
      var commandPermissionsHi = this.get('loginData.AdminInfo.CommandPermissionsHigh32');
      return (commandPermissionsHi & (1<<54));
    },

    hasChangeActivationLockPermission: function() {
      var commandPermissionsHi = this.get('loginData.AdminInfo.CommandPermissionsHigh32');
      return (commandPermissionsHi & (1<<58));
    },

    hasChangeOrganizationInfoPermission: function() {
      var commandPermissionsHi = this.get('loginData.AdminInfo.CommandPermissionsHigh32');
      return (commandPermissionsHi & (1<<59));
    },

    hasOneOrMoreCommandPermissions: function() {
      var commandPermissionsHi = this.get('loginData.AdminInfo.CommandPermissionsHigh32');
      return (commandPermissionsHi & 0x7FB8);
    },

    getServerVersion: function() {
      return this.get('loginData.ServerVersion');
    },

    getWebAPIVersion: function() {
      return this.get('loginData.WebAPIVersion');
    },

    getServerName: function() {
      return this.get('loginData.ServerName');
    },

    getUserName: function() {
      return this.get('loginData.UserName');
    },

    getSelfServicePrivileges: function() {
      return this.get('loginData.SelfServicePrivileges');
    },

    getEnableLiveDataUpdates: function() {
      return this.get('loginData.EnableLiveDataUpdates');
    },

    getDelayLiveDataUpdates: function() {
      return Number(this.get('loginData.DelayLiveDataUpdates'));
    },

    isSelfServicePortal: function() {
      return this.get('loginData.SelfServicePrivileges') ? true : false;
    },

    getCookie: function(cookieName) {
      var allCookies = document.cookie;
      var cookie = null;
      var cookieSearchStr = cookieName + '=';
      var start = allCookies.indexOf(cookieSearchStr);
      if (start !== -1) {
        start += cookieSearchStr.length;
        var end = allCookies.indexOf(';', start);
        if (end === -1) {
          end = allCookies.length;
        }
        cookie = allCookies.substring(start, end);
      }
      return cookie;
    },

    setCookie: function(cookieName, cookieValue, maxAge) {
      var DEFAULT_COOKIE_MAX_AGE = 2147483648;
      if (Em.isNone(maxAge)) {
        maxAge = DEFAULT_COOKIE_MAX_AGE;
      }
      document.cookie = cookieName + '=' + cookieValue + ';max-age=' + maxAge;
    }
  });
});
