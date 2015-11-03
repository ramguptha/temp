define([
  'ember',
  'ui',
  'packages/platform/ajax',
  'formatter',

  '../views/modal_action_status_view',

  'text!../templates/spinner.handlebars',
  'locale',
  'logger'
], function(
  Em,
  UI,
  Ajax,
  Formatter,

  ModalActionStatusView,

  spinnerTemplate,
  Locale,
  logger
) {
  'use strict';

  // Modal Action Controller
  // =======================
  //
  // The base controller class for a modal window with a first "confirmation" or "parameter gathering" screen,
  // and a second "status" screen that shows how the related action is progressing.
  return UI.Controller.extend({
    actions: {
      cancel: function() {
        this.send('closeModal');
      },

      // TODO: This action name makes no sense given how it is handled
      ok: function() {
        this.send('closeModal');
      },

      onConfirmAction: function() {
        this.sendActionRequest();
      },

      tryAgain: function() {
        this.resetActionStatus();
        this.sendActionRequest();
      },

      showHideError: function(error) {
        this.toggleProperty('showErrorDetail');
      }
    },

    showErrorDetail: false,
    errorUrl: null,
    errorStatusText: null,
    errorStatusCode: null,

    tShowErrorGeneric: 'shared.modals.errors.generic'.tr(),
    tShowErrorTimeout: 'shared.modals.errors.timeout'.tr(),

    modalActionWindowClass: "modal-action-window",
    modalActionErrorMsgClass: "modal-action-error",
    modalActionErrorDetailsClass: "modal-action-details",

    action: null,

    actionInProgress: false,
    actionFailed: false,

    statusMsg: null,
    statusMsgItem: null,
    statusMsgDetails: null,

    errorDetails: null,
    displayClose: false,
    isTryAgainDisabled: true,
    showOkBtn: false,

    // Views for Action State
    // ----------------------

    confirmationView: null,
    statusView: ModalActionStatusView,

    // Optional Callbacks
    // ------------------

    // If set, invoked on Action success; onActionSuccess(null, response).
    //
    // TODO: Remove first callback param (deprecated, used to be "router").
    onActionSuccess: null,

    // If set, invoked on Action failure; onActionError(null, jqXHR, textStatus, errorThrown).
    //
    // TODO: Remove first callback param (deprecated, used to be "router") and replace the rest with
    // an AjaxErrorDetail instance.
    onActionError: null,

    spinnerView: Em.View.extend({
      defaultTemplate: Em.Handlebars.compile(spinnerTemplate),
      classNames: ['float-left'],
      spinnerSizeClass: 'spinner-medium',
      showSpinner: false
    }),

    // View state: if we are "confirming the action", show the confirmationView. Otherwise show the statusView.
    confirmingAction: false,

    // Re-initialize properties when the modal is shown
    onShowModal: function(model) {
      this.set('showOkBtn', false);
      var hasConfirmationView = !Em.isNone(this.get('confirmationView'));

      if (hasConfirmationView) {
        this.set('confirmingAction', true);
      }

      this.initProperties(model);

      if (!hasConfirmationView) {
        this.send('onConfirmAction');
      }
    },

    // Override this method for per-modal invocation initialization
    initProperties: function(model) {},

    sendActionRequest: function() {
      var self = this;
      this.setProperties({
        actionInProgress: true,
        confirmingAction: false,
        statusMsg: this.get('inProgressMsg')
      });

      if (this.buildAction) {
        // New school
        var action = this.buildAction().reopen({
          onSuccess: function(response) {
            self.set('actionInProgress', false);
            self.actionDidSucceed(response);
          },

          onError: function(ajaxError) {
            self.set('actionInProgress', false);
            self.actionDidFail(ajaxError);
          }
        });

        this.set('action', action);

        action.invoke();
      } else {
        // Old school
        var endPoint = this.get('endPoint');
        var requestBody = this.get('requestBody');

        logger.log('DESKTOP: MODAL_ACTION_CONTROLLER, sendActionRequest: ', endPoint, ' body: ', requestBody);

        Ajax.post(
          'Action request',
          endPoint,
          'application/json; charset=UTF-8',
          requestBody,
          function success(response) {
            self.set('actionInProgress', false);
            self.actionDidSucceed(response);
          },
          function error(ajaxError) {
            self.set('actionInProgress', false);
            self.actionDidFail(ajaxError);
          }
        );
      }

      return true;
    },

    actionDidSucceed: function(response) {
      this.setProperties({
        statusMsg: this.get('successMsg'),
        statusMsgItem: this.get('successMsgItem'),
        statusMsgDetails: this.get('statusMsgDetails'),
        actionFailed: false,
        showOkBtn: true,
        isTryAgainDisabled: true,
        showErrorDetail: false
      });

      // Invoke 'onSuccessCallback', if defined
      if (typeof(this.onSuccessCallback) === 'function') {
        this.onSuccessCallback(response);
      }
    },

    actionDidFail: function(ajaxError) {
      var errorDetails = Formatter.formatErrorResponse(ajaxError.responseText);
      var errorStatusCode = ajaxError.jqXHR.status;
      var errorStatusText = ajaxError.jqXHR.statusText;
      var errorUrl = ajaxError.url;

      if(Em.isEmpty(errorDetails)) {
        if(ajaxError.statusText === 'timeout') {
          errorDetails = this.get('tShowErrorTimeout');
        } else {
          errorDetails = this.get('tShowErrorGeneric');
        }
      }

      this.setProperties({
        statusMsg: null,
        actionFailed: true,
        errorDetails: errorDetails,
        showOkBtn: false,
        isTryAgainDisabled: false,
        showErrorDetail: true,
        errorUrl: errorUrl,
        errorStatusText: errorStatusText,
        errorStatusCode: errorStatusCode
      });

      // Invoke 'onErrorCallback', if defined
      if (typeof(this.onErrorCallback) === 'function') {
        this.onErrorCallback(ajaxError);
      }
    },

    resetActionStatus: function() {
      this.setProperties({
        actionFailed: false,
        isTryAgainDisabled: true
      });
    }
  });
});