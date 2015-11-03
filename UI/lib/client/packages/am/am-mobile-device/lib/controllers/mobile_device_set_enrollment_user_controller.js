define([
  'ember',
  'help',
  '../namespace',
  'desktop',
  'am-desktop',
  'am-data',

  './mobile_device_summary_list_controller',
  '../views/mobile_device_set_enrollment_user_view'
], function (
  Em,
  Help,
  AmMobileDevice,
  Desktop,
  AmDesktop,
  AmData,

  MobileDeviceSummaryListController,
  MobileDeviceSetEnrollmentView
  ) {
  'use strict';

  return AmDesktop.ModalActionController.extend({
    mobileDeviceItemController: Em.inject.controller('amMobileDeviceItem'),

    heading: 'amMobileDevice.modals.setDeviceEnrollmentUser.heading'.tr(),

    tConfirmPromptOneDevice: 'amMobileDevice.modals.setDeviceEnrollmentUser.confirmPromptOneDevice'.tr(),
    tConfirmPromptManyDevices: 'amMobileDevice.modals.setDeviceEnrollmentUser.confirmPromptManyDevices'.tr(),
    tErrorMessageUserNameBlank: 'amMobileDevice.modals.setDeviceEnrollmentUser.errorMessageUserNameBlank'.tr(),

    headingIconClass: 'icon-enrollment-user',
    addModalClass: 'device-command-window',

    inProgressMsg: 'amMobileDevice.modals.setDeviceEnrollmentUser.inProgressMsg'.tr(),
    successMsg:'amMobileDevice.modals.setDeviceEnrollmentUser.successMsg'.tr(),
    errorMsg: 'amMobileDevice.modals.setDeviceEnrollmentUser.errorMsg'.tr(),
    errorDetailsMsg: 'amMobileDevice.modals.setDeviceEnrollmentUser.errorDetailsMsg'.tr(),

    isActionBtnDisabled: false,
    paused: true,

    devices: null,
    deviceIds: Em.A(),

    username: '',
    domainname: '',
    originalUsername: '',
    originalDomainname: '',

    confirmationView: MobileDeviceSetEnrollmentView,

    urlForHelp: null,

    initProperties: function () {
      var devices = this.get('model');

      var deviceIds = Em.A([]);

      for (var i = 0; i < devices.length; i++) {
        var dev = devices[i];
        deviceIds.pushObject(dev.get('id'));
      }

      this.set('paused', true);

      var originalUsername = '', originalDomainname = '', username = '', domainname = '';
      if (devices.length === 1 ||
        AmMobileDevice.isIdenticalDeviceFields(devices, ['data.enrollmentUser', 'data.enrollmentDomain'])) {
        originalUsername = devices[0].get('data.enrollmentUser');
        originalDomainname = devices[0].get('data.enrollmentDomain');

        username = devices[0].get('data.enrollmentUser');
        domainname = devices[0].get('data.enrollmentDomain');
      }

      this.setProperties({
        devices: devices,
        deviceIds: deviceIds,
        urlForHelp: Help.uri(1014),

        isActionBtnDisabled: false,

        originalUsername: originalUsername,
        originalDomainname: originalDomainname,
        username: username,
        domainname: domainname
      });

      // Everything is set, release the paused
      this.set('paused', false);
    },

    confirmPropmpt: function(){
      return this.get('devices.length') > 1 ?
        this.get('tConfirmPromptManyDevices') :
        this.get('tConfirmPromptOneDevice');
    }.property('devices.[]'),

    deviceCountDetails: function() {
      return this.get('devices.length');
    }.property('devices.[]'),

    // Hacky solution to get around the fact that the DB isn't sync'ed properly right away
    // when the command is finished
    onSuccessCallback: function (contr) {
      // Reset the success related properties
      // Set them again when the next request is successfully done
      // fixme: This approach is not working with new Ember
      this.setProperties({
        //actionInProgress: true,
        //statusMsg: this.get('inProgressMsg'),
        //showOkBtn: false
      });

      Em.run.later(this, function () {
        var detailsController = this.get('mobileDeviceItemController');
        detailsController.forceUpdate(this, this.get('deviceIds')[0]);
      }, 3000);
    },

    onFieldChange: function () {
      if (this.get('paused')) { return; }

      var userName = this.get('username'), domainName = this.get('domainname');

      if (Em.isNone(userName) || userName.trim() == '') {
        if (!Em.isNone(domainName) && domainName.trim() != '') {
          this.set('isActionBtnDisabled', true);
          this.set('errorMessage', this.get('tErrorMessageUserNameBlank'));

          return;
        }
      }

      var buttonDisabled = (domainName === this.get('originalDomainname') && userName === this.get('originalUsername')) &&
        !(Em.isEmpty(domainName) && Em.isEmpty(userName));

      this.set('errorMessage', '');
      this.set('isActionBtnDisabled', buttonDisabled);
    }.observes('username', 'domainname'),



    buildAction: function () {
      this.set('urlForHelp', null);

      return AmData.get('actions.AmMobileDeviceSetEnrollmentUserAction').create({
        deviceIds: this.get('deviceIds'),
        username: this.get('username'),
        domain: this.get('domainname')
      });
    },

    actions: {
      clearFields: function () {
        this.setProperties({
          username: '',
          domainname: ''
        });
      }
    }
  });
});
