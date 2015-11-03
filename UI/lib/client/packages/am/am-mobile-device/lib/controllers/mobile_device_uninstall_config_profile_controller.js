define([
  'ember',
  '../namespace',
  'desktop',
  'am-desktop',
  'am-data',

  '../views/mobile_device_uninstall_config_profile_view'
], function(
  Em,
  AmMobileDevice,
  Desktop,
  AmDesktop,
  AmData,

  MobileDeviceUninstallConfigProfileView
) {
  'use strict';

  return AmDesktop.ModalActionController.extend({

    tHeadingOneProfile: 'amMobileDevice.modals.uninstallConfigurationProfile.headingOneProfile'.tr(),
    tHeadingManyProfiles: 'amMobileDevice.modals.uninstallConfigurationProfile.headingManyProfiles'.tr(),
    tDescriptionOneDevice: 'amMobileDevice.modals.uninstallConfigurationProfile.descriptionOneDevice'.tr(),
    tDescriptionManyDevices: 'amMobileDevice.modals.uninstallConfigurationProfile.descriptionManyDevices'.tr(),
    tActionWarning:  'amMobileDevice.modals.uninstallConfigurationProfile.actionWarning'.tr(),

    heading: function() {
      return this.get('configProfiles').length == 1 ? this.get('tHeadingOneProfile') : this.get('tHeadingManyProfiles');
    }.property('configProfiles.[]'),

    headingIconClass: 'icon-uninstall-profile',

    actionButtonLabel: 'amMobileDevice.modals.uninstallConfigurationProfile.buttons.actionButtonLabel'.tr(),
    actionWarning: null,
    actionDescription: function() {
      return this.get('configProfiles').length == 1 ? this.get('tDescriptionOneDevice') : this.get('tDescriptionManyDevices');
    }.property('configProfiles.[]'),

    isActionBtnDisabled: false,

    inProgressMsg: 'amMobileDevice.modals.uninstallConfigurationProfile.inProgressMsg'.tr(),
    successMsg:'amMobileDevice.modals.uninstallConfigurationProfile.successMsg'.tr(),
    errorMsg: 'amMobileDevice.modals.uninstallConfigurationProfile.errorMsg'.tr(),
    errorDetailsMsg: 'amMobileDevice.modals.uninstallConfigurationProfile.errorDetailsMsg'.tr(),

    modalActionErrorMsgClass: 'modal-action-error',
    modalActionErrorDetailsClass: 'modal-action-details',
    confirmationView: MobileDeviceUninstallConfigProfileView,

    deviceId: null,
    configProfiles: null,

    initProperties: function()  {
      var model = this.get('model');
      var deviceId = model.deviceId;

      this.setProperties({
        deviceId: deviceId,
        configProfiles: model.configProfiles,
        actionWarning: this.get('tActionWarning')
      });
    },

    buildAction: function() {
      return AmData.get('actions.AmMobileDeviceUninstallConfigProfileAction').create({
        mobileDeviceId: this.get('deviceId'),
        configProfileIds: this.get('configProfiles')
      });
    }
  });
});
