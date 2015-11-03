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

    heading: 'amMobilePolicies.modals.removeInHouseApplication.heading'.tr(),
    actionDescription: 'amMobilePolicies.modals.removeInHouseApplication.description'.tr(),

    headingIconClass: 'icon-square-attention1',

    actionButtonLabel: 'amMobilePolicies.modals.removeInHouseApplication.buttons.removeApplication'.tr(),

    inProgressMsg: 'shared.modals.inProgressMessage'.tr(),
    successMsg: 'shared.modals.successMessage'.tr(),
    errorMsg: 'shared.modals.errorMessage'.tr(),

    confirmationView: Desktop.ModalActionConfirmView,

    policyId: null,
    apps: null,

    initProperties: function()  {
      var model = this.get('model');

      var apps = model.apps, policyId = model.policyId;

      this.setProperties({
        policyId: policyId,
        apps: apps
      });
    },

    buildAction: function() {
      var policyId = this.get('policyId'), apps = this.get('apps');

      return AmData.get('actions.AmMobilePolicyRemoveInHouseAppAction').create({
        inHouseAppIds: apps,
        policyId: Em.A([policyId])
      });
    }
  });
});
