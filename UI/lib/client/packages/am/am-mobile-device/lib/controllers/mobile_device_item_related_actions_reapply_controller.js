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
    heading: 'amMobileDevice.modals.reapplyPerformedAction.heading'.tr(),
    headingIconClass: 'icon-square-attention1',

    actionDescription: 'amMobileDevice.modals.reapplyPerformedAction.description'.tr(),
    actionButtonLabel: 'amMobileDevice.modals.reapplyPerformedAction.buttons.deleteLabel'.tr(),

    inProgressMsg: 'amMobileDevice.modals.reapplyPerformedAction.inProgressMessage'.tr(),
    successMsg: 'amMobileDevice.modals.reapplyPerformedAction.successMessage'.tr(),
    errorMsg: 'amMobileDevice.modals.reapplyPerformedAction.errorMessage'.tr(),

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
      return AmData.get('actions.AmMobileDeviceRelatedPerformedActionReapplyAction').create({
        deviceId: this.get('deviceId'),
        actionIds: this.get('actionIds')
      });
    }
  });
});
