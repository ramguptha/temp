define([
  'ember',
  '../namespace',
  'desktop',
  'am-desktop',
  'am-data'
], function(
  Em,
  AmMobileDevice,
  Desktop,
  AmDesktop,
  AmData
) {
  'use strict';

  return AmDesktop.ModalActionController.extend({
    heading: 'amMobilePolicies.modals.reexecuteAction.heading'.tr(),
    headingIconClass: "icon-square-attention1",

    actionDescription: 'amMobilePolicies.modals.reexecuteAction.description'.tr(),
    actionButtonLabel: 'amMobilePolicies.modals.reexecuteAction.buttons.actionButton'.tr(),

    inProgressMsg: 'amMobilePolicies.modals.reexecuteAction.inProgressMessage'.tr(),
    successMsg: 'amMobilePolicies.modals.reexecuteAction.successMessage'.tr(),
    errorMsg: 'amMobilePolicies.modals.reexecuteAction.errorMessage'.tr(),

    confirmationView: Desktop.ModalActionConfirmView,

    initProperties: function()  {
      var model = this.get('model');

      this.setProperties({
        uuid: model.uuid,
        actions: model.actions
      });
    },

    buildAction: function() {
      return AmData.get('actions.AmMobilePolicyReexecuteAction').create({
        actionUuids: this.get('actions').map(function(action){return action.uuid;}),
        policyUuid: this.get('uuid')
      });
    }
  });
});
