define([
  'ember',
  'help',
  '../namespace',
  'desktop',
  'am-desktop',
  'am-data',

  './mobile_device_summary_list_controller',
  '../views/mobile_device_retry_all_view'
], function(
  Em,
  Help,
  AmMobileDevice,
  Desktop,
  AmDesktop,
  AmData,

  MobileDeviceSummaryListController,
  MobileDeviceRetryAllView
  ) {
  'use strict';

  return AmDesktop.ModalActionController.extend({
    heading: null,
    headingIconClass: 'icon-square-attention1',

    confirmPrompt: '',
    actionButtonLabel: 'amMobileDevice.modals.retryAll.buttons.actionButtonLabel'.tr(),

    inProgressMsg: 'amMobileDevice.modals.retryAll.inProgressMsg'.tr(),
    successMsg: 'amMobileDevice.modals.retryAll.successMsg'.tr(),
    errorMsg: 'amMobileDevice.modals.retryAll.errorMsg'.tr(),
    errorDetailsMsg: 'amMobileDevice.modals.retryAll.errorDetailsMsg'.tr(),

    devices: null,
    deviceName: null,
    deviceIds: null,

    actionWarning: null,
    actionDescription: null,

    tProfiles: 'amMobileDevice.modals.retryAll.btnProfiles'.tr(),
    tApps: 'amMobileDevice.modals.retryAll.btnApps'.tr(),
    btnToken: 'amMobileDevice.modals.retryAll.btnToken'.tr(),

    tDefaultHeading: 'amMobileDevice.modals.retryAll.heading'.tr(),
    tActionDescriptionProfile: 'amMobileDevice.modals.retryAll.actionDescriptionProfile'.tr(),
    tHeadingProfile: 'amMobileDevice.modals.retryAll.headingProfile'.tr(),
    tActionWarningProfile: 'amMobileDevice.modals.retryAll.actionWarningProfile'.tr(),
    tActionDescriptionApps: 'amMobileDevice.modals.retryAll.actionDescriptionApps'.tr(),
    tHeadingApps: 'amMobileDevice.modals.retryAll.headingApps'.tr(),
    tActionWarningApps: 'amMobileDevice.modals.retryAll.actionWarningApps'.tr(),
    tActionDescriptionToken: 'amMobileDevice.modals.retryAll.actionDescriptionToken'.tr(),
    tHeadingToken: 'amMobileDevice.modals.retryAll.headingToken'.tr(),
    tActionWarningToken: 'amMobileDevice.modals.retryAll.actionWarningToken'.tr(),

    inProgress: false,
    paused: null,
    confirmationView: MobileDeviceRetryAllView,

    urlForHelp: null,

    isNotChanged: null,
    retryOption: null,

    retryOptions: function() {
      return Em.A([
        {
          value: 'profiles',
          label: this.get('tProfiles'),
          class: 'is-radio-checked-profiles'
        }, {
          value: 'apps',
          label: this.get('tApps'),
          class: 'is-radio-checked-apps'
        }
      ]);
    }.property(),

    initProperties: function(devices)  {
      this.setProperties({
        urlForHelp: Help.uri(1017),
        heading: this.get('tDefaultHeading'),

        paused: true,
        inProgress: true
      });

      var deviceIds = Em.A([]);
      for (var i = 0; i < devices.length; i++) {
        var dev = devices[i];
        deviceIds.pushObject(dev.get('id'));
      }

      this.setProperties({
        devices: devices,
        deviceIds: deviceIds,

        actionWarning: null,
        actionDescription: null,
        inProgress: false,
        paused: false,

        isNotChanged: true,
        retryOption: null
      });
    },

    deviceCountDetails: function() {
      return this.get('devices.length');
    }.property('devices.[]'),


    buildAction: function() {
      var option = this.get('retryOption');
      var deviceIds = this.get('deviceIds');

      this.set('urlForHelp', null);

      if (option === 'profiles') {
        return AmData.get('actions.AmMobileDeviceRetryAllFailedProfilesAction').create({
          mobileDeviceIds: deviceIds
        });
      } else if (option === 'apps') {
        return AmData.get('actions.AmMobileDeviceRetryAllFailedAppsAction').create({
          mobileDeviceIds: deviceIds
        });
      }
    },

    onRetryOptionChanged: function () {
      var option = this.get('retryOption');

      if (this.get('paused') || !option) { return; }

      this.setProperties({
        isNotChanged: false,
        actionDescription: option === 'profiles' ? this.get('tActionDescriptionProfile') : this.get('tActionDescriptionApps'),
        heading: option === 'profiles' ? this.get('tHeadingProfile') : this.get('tHeadingApps'),
        actionWarning: option === 'profiles' ? this.get('tActionWarningProfile') : this.get('tActionWarningApps')
      });

    }.observes('retryOption')
  });
});
