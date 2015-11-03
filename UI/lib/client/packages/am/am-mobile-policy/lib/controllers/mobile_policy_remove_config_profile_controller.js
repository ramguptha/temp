define([
  'ember',
  '../namespace',
  'desktop',
  'am-desktop',
  'am-data',

  '../views/mobile_policy_remove_config_profile_view'
], function (
  Em,
  AmMobilePolicy,
  Desktop,
  AmDesktop,
  AmData,

  MobilePolicyRemoveConfigProfileView
  ) {
  'use strict';

  return AmDesktop.ModalActionController.extend({
    heading: 'amMobilePolicies.modals.removeConfigurationProfile.heading'.tr(),
    actionDescription: 'amMobilePolicies.modals.removeConfigurationProfile.description'.tr(),
    actionWarning: 'amMobilePolicies.modals.removeConfigurationProfile.descriptionWarning'.tr(),
    headingIconClass: 'icon-uninstall-config',
    actionButtonLabel: 'amMobilePolicies.modals.removeConfigurationProfile.buttons.removeProfile'.tr(),

    inProgressMsg: 'shared.modals.inProgressMessage'.tr(),
    successMsg: 'shared.modals.successMessage'.tr(),
    errorMsg: 'shared.modals.errorMessage'.tr(),

    confirmationView: MobilePolicyRemoveConfigProfileView,
    modalActionErrorMsgClass: 'modal-action-error',
    modalActionErrorDetailsClass: 'modal-action-details',
    
    isActionBtnDisabled: false,

    initProperties: function () {
      var model = this.get('model');

      var configProfiles = model.configProfiles;
      var policyId = model.policyId;

      this.setProperties({ 
        policyId: policyId,
        configProfiles: configProfiles
      });
    },

    buildAction: function () {
      return AmData.get('actions.AmMobilePolicyRemoveConfigProfileAction').create({
        policyId: [this.get('policyId')],
        configProfileIds: this.get('configProfiles')
      });
    }
  });
});
