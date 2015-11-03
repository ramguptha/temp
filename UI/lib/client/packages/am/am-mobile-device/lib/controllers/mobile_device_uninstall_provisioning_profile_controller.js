define([
  'ember',
  '../namespace',
  'desktop',
  'am-desktop',
  'am-data',

  '../views/mobile_device_uninstall_provisioning_profile_view'
], function(
  Em,
  AmMobileDevice,
  Desktop,
  AmDesktop,
  AmData,

  MobileDeviceUninstallProvisioningProfileView
  ) {
  'use strict';

  return AmDesktop.ModalActionController.extend({

    tHeadingOneProfile: 'amMobileDevice.modals.uninstallProvisioningProfile.headingOneProfile'.tr(),
    tHeadingManyProfiles: 'amMobileDevice.modals.uninstallProvisioningProfile.headingManyProfiles'.tr(),
    tDescriptionOneDevice: 'amMobileDevice.modals.uninstallProvisioningProfile.descriptionOneDevice'.tr(),
    tDescriptionManyDevices: 'amMobileDevice.modals.uninstallProvisioningProfile.descriptionManyDevices'.tr(),
    tActionWarning: 'amMobileDevice.modals.uninstallProvisioningProfile.actionWarning'.tr(),

    heading: function() {
      return this.get('provisioningProfiles').length == 1 ? this.get('tHeadingOneProfile') : this.get('tHeadingManyProfiles');
    }.property('provisioningProfiles.[]'),

    headingIconClass: 'icon-uninstall-profile',

    actionButtonLabel: 'amMobileDevice.modals.uninstallProvisioningProfile.buttons.actionButtonLabel'.tr(),
    actionWarning: null,
    actionDescription: function() {
      return this.get('provisioningProfiles').length == 1 ? this.get('tDescriptionOneDevice') : this.get('tDescriptionManyDevices');
    }.property('provisioningProfiles.[]'),

    isActionBtnDisabled: false,

    inProgressMsg: 'amMobileDevice.modals.uninstallProvisioningProfile.inProgressMsg'.tr(),
    successMsg:'amMobileDevice.modals.uninstallProvisioningProfile.successMsg'.tr(),
    errorMsg: 'amMobileDevice.modals.uninstallProvisioningProfile.errorMsg'.tr(),
    errorDetailsMsg: 'amMobileDevice.modals.uninstallProvisioningProfile.errorDetailsMsg'.tr(),

    modalActionErrorMsgClass: 'modal-action-error',
    modalActionErrorDetailsClass: 'modal-action-details',
    confirmationView: MobileDeviceUninstallProvisioningProfileView,

    deviceId: null,
    provisioningProfiles: null,

    initProperties: function()  {
      var model = this.get('model');
      var deviceId = model.deviceId;

      this.setProperties({
        deviceId: deviceId,
        provisioningProfiles: model.provisioningProfiles,
        actionWarning: this.get('tActionWarning')
      });
    },

    buildAction: function() {
      return AmData.get('actions.AmMobileDeviceUninstallProvisioningProfileAction').create({
        mobileDeviceId: this.get('deviceId'),
        provisioningProfileIds: this.get('provisioningProfiles')
      });
    }
  });
});