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
    heading: 'amMobileDevice.modals.deleteCustomFieldData.heading'.tr(),
    headingIconClass: 'icon-square-attention1',

    actionDescription: 'amMobileDevice.modals.deleteCustomFieldData.description'.tr(),
    actionButtonLabel: 'amMobileDevice.modals.deleteCustomFieldData.buttons.deleteLabel'.tr(),

    inProgressMsg: 'amMobileDevice.modals.deleteCustomFieldData.inProgressMessage'.tr(),
    successMsg: 'amMobileDevice.modals.deleteCustomFieldData.successMessage'.tr(),
    errorMsg: 'amMobileDevice.modals.deleteCustomFieldData.errorMessage'.tr(),

    confirmationView: Desktop.ModalActionConfirmView,

    deviceId: null,
    itemIds: null,

    urlForHelp: null,

    initProperties: function()  {
      var model = this.get('model');

      this.setProperties({
        // TODO
        //urlForHelp: Help.uri(1000),
        deviceId: model.deviceId,
        itemIds: model.itemIds
      });
    },

    buildAction: function() {
      this.set('urlForHelp', null);
      return AmData.get('actions.AmMobileDeviceRelatedCustomFieldDataDeleteAction').create({
        deviceId: this.get('deviceId'),
        itemIds: this.get('itemIds')
      });
    }
  });
});
