define([
  'ember',
  'desktop',
  'am-desktop',
  'am-data'
], function(
  Em,
  Desktop,
  AmDesktop,
  AmData
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
      return AmData.get('actions.AmComputerCommandQueueDeleteAction').create({
        commandIds: this.get('commandIds')
      });
    },

    onSuccessCallback: function() {
      // on successful item deletion, return to the Queue list page
      this.transitionTo('am_command_queue.computers');
    }
  });
});
