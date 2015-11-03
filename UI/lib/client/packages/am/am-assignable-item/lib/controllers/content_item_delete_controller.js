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
    heading: 'amAssignableItem.modals.deleteContent.heading'.tr(),
    headingIconClass: 'icon-square-attention1',

    actionDescription: 'amAssignableItem.modals.deleteContent.description'.tr(),
    actionButtonLabel: 'amAssignableItem.modals.deleteContent.buttons.deleteContent'.tr(),

    inProgressMsg: 'shared.modals.inProgressMessage'.tr(),
    successMsg: 'shared.modals.successMessage'.tr(),
    errorMsg: 'shared.modals.errorMessage'.tr(),

    confirmationView: Desktop.ModalActionConfirmView,

    contentIds: null,

    urlForHelp: Help.uri(1036),

    initProperties: function()  {
      var model = this.get('model');

      this.setProperties({
        urlForHelp: Help.uri(1036),
        contentIds: model.contentIds
      });
    },

    buildAction: function() {
      this.set('urlForHelp', null);
      return AmData.get('actions.AmContentDeleteAction').create({
        contentIds: this.get('contentIds')
      });
    },

    onSuccessCallback: function () {
      // on successful item deletion, return to the content list page
      this.transitionToRoute('am_assignable_list.content');
    }
  });
});
