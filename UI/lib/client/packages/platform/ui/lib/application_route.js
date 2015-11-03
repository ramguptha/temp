define([
  'ember',
  './route',
  'packages/platform/modal',
  'packages/platform/send-ember-action'
], function(
  Em,
  Route,
  Modal,
  sendEmberAction
) {
  'use strict';

  // ApplicationRoute
  // ================
  //
  // The ApplicationRoute is responsible for handling "global" actions via bubbling.
  return Em.Route.extend(Modal.HasShowModal, {
    actions: {
      gotoViaGlobalNav: function(spec) {
        if (spec.callBack) {
          spec.callBack(this);
        } else if (spec.path) {
          var context = spec.context;
          this.transitionToEnumContext(spec.path, 'function' === typeof(context) ? context() : context);
        } else {
          logger.error('ROOT: Unknown path name', evt, evt.context);
        }
      },

      gotoBreadcrumb: function(crumb) {
        this.transitionToEnumContext(crumb.get('path'), crumb.get('context'));
      },

      // Ajax Notifications
      ajaxInvoked: Em.K,

      ajaxSucceeded: Em.K,

      ajaxFailed: Em.K,

      // Data Notifications
      dataAcquired: Em.K,

      // Location Notifications
      urlChanged: Em.K,

      // Session Handling and Notification
      refreshSession: Em.K,
      sessionIsDying: Em.K,
      sessionHasDied: Em.K,

      noteUserActivity: function() {
        this.controllerFor('activityMonitor').touchUser();
      },

      noteLayoutMetricsChange: function() {
        window.App.set('layoutMetricsChangedAt', new Date());
      }
    },

    init: function() {
      this._super();

      // Observers
      this.get('router.url');
    },

    // transitionTo, but with an enumerable as context
    transitionToEnumContext: function(path, enumContext) {
      var args = Em.makeArray(path);

      if (!Em.isNone(enumContext)) {
        args.pushObjects(Em.makeArray(enumContext));
      }

      this.transitionTo.apply(this, args.toArray());
    },

    urlDidChange: function() {
      // Send through entire controller / route heirarchy (with safety) instead of directly to self
      this.send('urlChanged', this.get('router.url'));
    }.observes('router.url')
  });
});
