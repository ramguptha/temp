define([
  'ember',
  'help',
  'desktop',
  'am-desktop',

  'am-data'
], function(
  Em,
  Help,
  Desktop,
  AmDesktop,

  AmData
) {
  'use strict';

  return AmDesktop.ModalActionController.extend({
    tHeadingSingle: 'amAssignableItem.modals.deleteAction.heading'.tr(),
    tHeadingPlural: 'amAssignableItem.modals.deleteActions.heading'.tr(),
    headingIconClass: 'icon-square-attention1',

    tActionDescriptionSingle: 'amAssignableItem.modals.deleteAction.description'.tr(),
    tActionDescriptionPlural: 'amAssignableItem.modals.deleteActions.description'.tr(),
    tActionButtonLabelSingle: 'amAssignableItem.modals.deleteAction.buttons.deleteAction'.tr(),
    tActionButtonLabelPlural: 'amAssignableItem.modals.deleteActions.buttons.deleteAction'.tr(),

    inProgressMsg: 'shared.modals.inProgressMessage'.tr(),
    successMsg: 'shared.modals.successMessage'.tr(),
    errorMsg: 'shared.modals.errorMessage'.tr(),

    confirmationView: Desktop.ModalActionConfirmView,

    actionIds: null,

    urlForHelp: null,

    initProperties: function() {
      var actionIds = this.get('model').actionIds;
      this.setProperties({
        actionIds: actionIds,
        urlForHelp: Help.uri(1056),
        heading: actionIds.length > 1 ? this.get('tHeadingPlural') : this.get('tHeadingSingle'),
        actionDescription: actionIds.length > 1 ? this.get('tActionDescriptionPlural') : this.get('tActionDescriptionSingle'),
        actionButtonLabel: actionIds.length > 1 ? this.get('tActionButtonLabelPlural') : this.get('tActionButtonLabelSingle')
      });
    },

    buildAction: function() {
      this.set('urlForHelp', null);

      return AmData.get('actions.AmActionDeleteAction').create({
        actionIds: this.get('actionIds')
      });
    },

    onSuccessCallback: function () {
      // on successful item deletion, return to the content list page
      this.transitionToRoute('am_assignable_list.actions');
    }
  });
});
