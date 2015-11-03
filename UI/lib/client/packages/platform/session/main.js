define([
  'ember',
  'desktop',

  './lib/controllers/session_expiring_controller',
  'text!./lib/templates/session_expiring.handlebars',

  './lib/controllers/session_renewal_warning_controller',
  'text!./lib/templates/session_renewal_warning.handlebars',

  'i18n!./nls/strings'
], function(
  Em,
  Desktop,

  SessionExpiringController,
  sessionExpiringTemplate,

  SessionRenewalWarningController,
  sessionRenewalWarningTemplate,

  strings
) {
  'use strict';

  // Session Support
  // ===============

  // Session Expiring View
  // ---------------------

  var SessionExpiringView = Em.View.extend({
    defaultTemplate: Em.Handlebars.compile(sessionExpiringTemplate),
    layout: Desktop.ModalActionLayoutTemplate,
    classNames: 'is-session-expiring-container'.w()
  });

  // Session Renewal Warning View
  // ----------------------------

  var SessionRenewalWarningView = Em.View.extend({
    defaultTemplate: Em.Handlebars.compile(sessionRenewalWarningTemplate),
    layout: Desktop.ModalActionLayoutTemplate,
    classNames: 'is-session-renewal-warning-container'.w()
  });

  return {
    appClasses: {
      SessionExpiringController: SessionExpiringController,
      SessionExpiringView: SessionExpiringView,

      SessionRenewalWarningController: SessionRenewalWarningController,
      SessionRenewalWarningView: SessionRenewalWarningView
    },

    appStrings: strings,

    SessionExpiringController: SessionExpiringController,
    SessionExpiringView: SessionExpiringView,

    SessionRenewalWarningController: SessionRenewalWarningController,
    SessionRenewalWarningView: SessionRenewalWarningView
  };
});
