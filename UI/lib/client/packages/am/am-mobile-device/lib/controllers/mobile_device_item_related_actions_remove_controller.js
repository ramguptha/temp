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
    heading: 'amMobileDevice.modals.removePerformedAction.heading'.tr(),
    headingIconClass: 'icon-square-attention1',

    actionDescription: 'amMobileDevice.modals.removePerformedAction.description'.tr(),
    actionButtonLabel: 'amMobileDevice.modals.removePerformedAction.buttons.deleteLabel'.tr(),

    inProgressMsg: 'shared.modals.inProgressMessage'.tr(),
    successMsg: 'shared.modals.successMessage'.tr(),
    errorMsg: 'shared.modals.errorMessage'.tr(),

    confirmationView: Desktop.ModalActionConfirmView,

    deviceId: null,
    actionIds: null,

    urlForHelp: null,

    initProperties: function()  {
      var model = this.get('model');

      this.setProperties({
        // TODO
        //urlForHelp: Help.uri(1002),
        deviceId: model.deviceId,
        actionIds: model.actionIds
      });
    },

    buildAction: function() {
      this.set('urlForHelp', null);
      return AmData.get('actions.AmMobileDeviceRelatedPerformedActionRemoveAction').create({
        deviceId: this.get('deviceId'),
        actionIds: this.get('actionIds')
      });
    }
  });
});
