define([
  'ember-core'
], function(
  Em
) {
  'use strict';

  // sendEmberAction(name, *args)
  // ============================
  //
  // Send an action to the active controller from any javascript scope. BEFORE YOU INVOKE THIS FROM CODE, ask
  // yourself if you _really_ have to. Valid reasons to invoke this method include:
  //
  // - The element triggering it is not within the proper view hierarchy (e.g. tooltips, menus).
  // - To facilitate loose binding between a low level component and a high level behaviour (such as a global error 
  //   handler for failed data requests, or a session timeout warning).
  //
  // For convenience's sake (to allow invocation from href="javascript:..." and such), this method is also available
  // as window.sendEmberAction().
  //
  // Fails gracefully; if App.__container__ is unavailable, does nothing.
  return window.sendEmberAction = function(name) {
    var container = window.App && window.App.__container__;

    if (!container) {
      return;
    }

    var applicationController = container.lookup('controller:application');
    if (!applicationController) {
      return;
    }

    var currentRoute = container.lookup('route:' + applicationController.get('currentRouteName'));
    if (!currentRoute) {
      return;
    }

    var activeController = currentRoute.get('controller');
    if (!activeController) {
      return;
    }

    return activeController.send.apply(activeController, arguments);
  };
});
