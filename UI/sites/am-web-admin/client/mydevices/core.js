define([
  'ember',
  'packages/platform/app-foundation',
  'packages/platform/ajax',
  'packages/am/am-session',
  'am-data',

  './namespace',

  './controllers/application_controller',

  './views/about_info_view',
  './views/session_expiring_view',

  'help',
  'logger'
], function (
  Em,
  AppFoundation,
  Ajax,
  AmSession,
  AmData,

  App,

  ApplicationController,

  AboutInfoView,
  SessionExpiringView,

  Help,
  logger
) {
  // Create a local namespace for the app, and also hook into the window for debugging.
  window.App = App;

  var ApplicationRoute = App.get('ApplicationRoute').extend({
    actions: {
      logout: function() {
        Ajax.del(
          'Log out',
          AmData.get('urlRoot') + '/api/login',
          'application/x-www-form-urlencoded; charset=UTF-8',
          {},
          function(data) {
            window.document.location = '../mylogin/?loggedOut=true';
          },
          function(details, textStatus, errorThrown) {
            this.controllerFor('application').set(
              'errorMessage', textStatus + ': ' + details.status + ' - ' + errorThrown
            );
          }
        );
      },

      showAboutInfo: function() {
        this.showModal({ name: 'about_info' });
      },

      sessionExpiring: function() {
        this.showModal({ name: 'session_expiring' });
      }
    }
  });

  App.setProperties({
    VERSION: '0.0.1',

    ApplicationRoute: ApplicationRoute,
    ApplicationController: ApplicationController,
    ApplicationView: AppFoundation.ApplicationView.extend({
      helpLink: function() {
        return Help.baseUriSsp();
      }.property()
    }),

    // Landing
    // -------

    LandingController: null,
    LandingView: null,

    // About Absolute Manage Web Admin
    // -------------------------------

    AboutInfoController: Em.Controller.extend({
      actions: {
        close: function() {
          this.send('closeModal');
        }
      },

      modalActionWindowClass: 'modal-action-window',

      displayClose: true,

      serverVersion: function() {
        return AmSession.getServerVersion();
      }.property(),

      webAPIVersion: function() {
        return AmSession.getWebAPIVersion();
      }.property(),

      serverName: function() {
        return AmSession.getServerName();
      }.property(),

      userName:  function() {
        return AmSession.getUserName();
      }.property()
    }),
    AboutInfoView: AboutInfoView,

    // Session Expiration Warning
    // --------------------------

    SessionExpiringController: Em.Controller.extend({
      actions: {
        onOK: function() {
          window.clearInterval(this.get('checkTimeoutHandle'));
          this.set('checkTimeoutHandle', null);

          logger.log('onOK calling doSessionCheck ..');

          AmSession.set('sessionOkButtonClicked', true);
          AmSession.kickSessionActivity();
          this.send('closeModal');
        }
      },

      modalActionWindowClass: 'modal-action-window',

      secsToExpiry: null, 

      checkTimeout: null,
      checkTimeoutHandle: null,

      init: function() {
        var self = this;

        this.set('checkTimeout', function() {
          var secsToExpiry = self.getSecsToExpiry();

          if (secsToExpiry > 0) {
            self.set('secsToExpiry', secsToExpiry);
          } else {
            window.clearInterval(self.get('checkTimeoutHandle'));
            window.document.location = '../mylogin/?sessionTimedOut=true';
          }
        });
      },

      onShowModal: function() {
        this.set('secsToExpiry', this.getSecsToExpiry());
        this.set('checkTimeoutHandle', window.setInterval(this.get('checkTimeout'), 1000));
      },

      getSecsToExpiry: function() {
        var millisecsToExpiry = AmSession.get('sessionTimeoutMsecs') + AmSession.get('lastAliveTime') - new Date().getTime();
        return Math.floor(millisecsToExpiry / 1000);
      }
    }),
    SessionExpiringView: SessionExpiringView
  });

  // Export it from the module.
  return App;
});

