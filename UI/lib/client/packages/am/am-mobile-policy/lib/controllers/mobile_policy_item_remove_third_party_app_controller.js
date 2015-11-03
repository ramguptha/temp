define([
  'ember',
  '../namespace',

  'desktop',
  'am-desktop',
  'am-data'
], function (
  Em,
  AmMobileDevice,

  Desktop,
  AmDesktop,
  AmData
) {
  'use strict';

  return AmDesktop.ModalActionController.extend({
    heading: 'amMobilePolicies.modals.removeThirdPartyApplication.heading'.tr(),
    actionDescription: 'amMobilePolicies.modals.removeThirdPartyApplication.description'.tr(),

    headingIconClass: 'icon-square-attention1',

    actionButtonLabel: 'amMobilePolicies.modals.removeThirdPartyApplication.buttons.removeApplication'.tr(),

    inProgressMsg: 'shared.modals.inProgressMessage'.tr(),
    successMsg: 'shared.modals.successMessage'.tr(),
    errorMsg: 'shared.modals.errorMessage'.tr(),

    confirmationView: Desktop.ModalActionConfirmView,

    policyId: null,
    apps: null,

    initProperties: function () {
      var model = this.get('model');

      var apps = model.apps;
      var policyId = model.policyId;

      this.setProperties({
        policyId: policyId,
        apps: apps
      });
    },

    buildAction: function () {
      var policyId = this.get('policyId');
      var apps = this.get('apps');

      return AmData.get('actions.AmMobilePolicyRemoveThirdPartyAppAction').create({
        thirdPartyAppIds: apps,
        policyId: Em.A([policyId])
      });
    }
  });
});
