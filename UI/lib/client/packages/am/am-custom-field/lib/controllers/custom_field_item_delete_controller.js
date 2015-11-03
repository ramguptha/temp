define([
  'ember',
  'help',
  'desktop',
  'am-desktop',
  'packages/am/am-data'
], function(
  Em,
  Help,
  Desktop,
  AmDesktop,
  AmData
) {
  'use strict';

  return AmDesktop.ModalActionController.extend({
    heading: 'amCustomField.modals.deleteCustomField.heading'.tr(),
    headingIconClass: 'icon-square-attention1',

    actionDescription: 'amCustomField.modals.deleteCustomField.description'.tr(),
    actionButtonLabel: 'amCustomField.modals.deleteCustomField.buttons.deleteLabel'.tr(),

    inProgressMsg: 'amCustomField.modals.deleteCustomField.inProgressMessage'.tr(),
    successMsg: 'amCustomField.modals.deleteCustomField.successMessage'.tr(),
    errorMsg: 'amCustomField.modals.deleteCustomField.errorMessage'.tr(),

    confirmationView: Desktop.ModalActionConfirmView,

    fieldIds: null,

    urlForHelp: null,

    initProperties: function()  {
      var model = this.get('model');

      this.setProperties({
        urlForHelp: Help.uri(1076),
        fieldIds: model.fieldIds
      });
    },

    buildAction: function() {
      this.set('urlForHelp', null);
      return AmData.get('actions.AmCustomFieldDeleteAction').create({
        fieldIds: this.get('fieldIds')
      });
    }
  });
});
