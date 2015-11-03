define([
  'ember',
  '../namespace',
  'desktop',
  'am-desktop',
  'am-data',

  'am-multi-select',
  '../views/mobile_device_uninstall_application_view'
], function(
  Em,
  AmMobileDevice,
  Desktop,
  AmDesktop,
  AmData,

  AmMultiSelect,
  MobileDeviceUninstallAppView
  ) {
  'use strict';

  return AmDesktop.ModalActionController.extend({

    tDescriptionOneDevice: 'amMobileDevice.modals.uninstallApplications.descriptionOneDevice'.tr(),
    tDescriptionManyDevices: 'amMobileDevice.modals.uninstallApplications.descriptionManyDevices'.tr(),
    tActionWarning: 'amMobileDevice.modals.uninstallApplications.actionWarning'.tr(),

    heading: 'amMobileDevice.modals.uninstallApplications.heading'.tr(),
    headingIconClass: 'icon-uninstall-app-1',

    actionButtonLabel: 'amMobileDevice.modals.uninstallApplications.buttons.actionButtonLabel'.tr(),
    actionDescription: function() {
      return this.get('applications').length == 1 ? this.get('tDescriptionOneDevice') : this.get('tDescriptionManyDevices');
    }.property('applications.[]'),

    actionWarning: null,

    isActionBtnDisabled: false,

    inProgressMsg: 'amMobileDevice.modals.uninstallApplications.inProgressMsg'.tr(),
    successMsg: 'amMobileDevice.modals.uninstallApplications.successMsg'.tr(),
    errorMsg: 'amMobileDevice.modals.uninstallApplications.errorMsg'.tr(),
    errorDetailsMsg: 'amMobileDevice.modals.uninstallApplications.errorDetailsMsg'.tr(),

    modalActionErrorMsgClass: 'modal-action-error',
    modalActionErrorDetailsClass: 'modal-action-details',
    confirmationView: MobileDeviceUninstallAppView,

    deviceId: null,
    applications: null,

    initProperties: function()  {
      var model = this.get('model');
      var deviceId = model.deviceId;

      this.setProperties({
        deviceId: deviceId,
        applications: model.applications,
        actionWarning: this.get('tActionWarning')
      });
    },

    buildAction: function() {
      return AmData.get('actions.AmMobileDeviceUninstallApplicationAction').create({
        mobileDeviceId: this.get('deviceId'),
        applicationIds: this.get('applications')
      });
    }
  });
});
