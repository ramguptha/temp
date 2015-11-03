define([
  'ember',
  'desktop',
  'am-desktop',
  'am-data',
  'formatter',
  'locale'
], function(
  Em,
  Desktop,
  AmDesktop,
  AmData,
  Formatter,
  Locale
) {
  'use strict';

  return AmDesktop.ModalActionController.extend({
    heading: 'amMobileCommand.modals.deleteCommand.heading'.tr(),
    headingIconClass: 'icon-square-attention1',

    actionDescription: 'amMobileCommand.modals.deleteCommand.description'.tr(),
    actionButtonLabel: 'amMobileCommand.modals.deleteCommand.buttons.deleteCommand'.tr(),

    inProgressMsg: 'shared.modals.inProgressMessage'.tr(),
    successMsg: 'shared.modals.successMessage'.tr(),
    errorMsg: 'shared.modals.errorMessage'.tr(),

    confirmationView: Desktop.ModalActionConfirmView,

    commandIds: null,

    initProperties: function()  {
      var model = this.get('model');

      this.set('commandIds', model.commandIds);
    },

    buildAction: function() {
      return AmData.get('actions.AmCommandHistoryDeleteAction').create({
        commandIds: this.get('commandIds')
      });
    },

    //WARNING: this backend command has a unique permission bit which may not always be set for an admin user ( we don't even make sure it's set after a successful login )
    //we override this function to take control of how we display the error message. In the future we should probably create our own am-desktop/modal_action_controller instead.
    actionDidFail: function(ajaxError) {
      var jqXHR = ajaxError.jqXHR;
      var errorDetails = Formatter.formatErrorResponse(jqXHR.responseText);

      if(Em.isEmpty(errorDetails)) {
        if(jqXHR.statusText === 'timeout') {
          errorDetails = Locale.renderGlobals('shared.modals.errors.timeout').toString();
        } else {
          errorDetails = Locale.renderGlobals('shared.modals.errors.generic').toString();
        }
      }

      this.setProperties({
        statusMsg: null,
        actionFailed: true,
        errorDetails: errorDetails,
        showOkBtn: false,
        isTryAgainDisabled: false
      });

      // Invoke 'onErrorCallback', if defined
      if (typeof(this.onErrorCallback) === 'function') {
        this.onErrorCallback(ajaxError);
      }
    },

    onSuccessCallback: function () {
      // on successful item deletion, return to the History list page
      this.transitionTo('am_command_history.mobile_devices');
    }
  });
});
