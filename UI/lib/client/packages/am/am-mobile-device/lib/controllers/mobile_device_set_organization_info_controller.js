define([
  'ember',
  'help',
  '../namespace',
  'desktop',
  'am-desktop',
  'am-data',

  './mobile_device_summary_list_controller',
  'text!../templates/mobile_device_set_organization_info.handlebars'
], function (
  Em,
  Help,
  AmMobileDevice,
  Desktop,
  AmDesktop,
  AmData,

  MobileDeviceSummaryListController,
  SetOrganizationInfoTemplate
  ) {
  'use strict';

  return AmDesktop.ModalActionController.extend({
    modalActionWindowClass: 'modal-action-window set-organization-info-window',
    addModalClass: 'set-organization-info-window',

    heading:  'amMobileDevice.modals.setOrganizationInfo.heading'.tr(),
    headingIconClass: 'icon-org-info',

    actionButtonLabel: 'amMobileDevice.modals.setOrganizationInfo.buttons.actionButtonLabel'.tr(),

    inProgressMsg: 'amMobileDevice.modals.setOrganizationInfo.inProgressMsg'.tr(),
    successMsg: 'amMobileDevice.modals.setOrganizationInfo.successMsg'.tr(),
    errorMsg: 'amMobileDevice.modals.setOrganizationInfo.errorMsg'.tr(),
    errorDetailsMsg: 'amMobileDevice.modals.setOrganizationInfo.errorDetailsMsg'.tr(),

    isActionBtnDisabled: false,
    paused: true,

    orgName: null,
    phone: null,
    email: null,
    address: null,
    custom: null,

    emailErrorMessage: '',
    phoneErrorMessage: '',

    confirmationView: Em.View.extend({
      template: Em.Handlebars.compile(SetOrganizationInfoTemplate),
      layout: Desktop.ModalWizardLayoutTemplate
    }),

    urlForHelp: null,

    initProperties: function () {
      var devices = this.get('model'), dev;

      var deviceIds = Em.A([]);

      for (var i = 0; i < devices.length; i++) {
        dev = devices[i];
        deviceIds.pushObject(dev.get('id'));
      }

      this.set('paused', true);

      if (devices.length === 1 ||
        AmMobileDevice.isIdenticalDeviceFields(devices, ['data.organizationName', 'data.organizationPhone', 'data.organizationEMail', 'data.organizationAddress', 'data.organizationCustom'])) {
        dev = devices[0];
      } else {
        dev = this;
      }

      this.setProperties({
        devices: devices,
        deviceIds: deviceIds,
        urlForHelp: Help.uri(1015),

        // By default, the Set Info button should be enabled.
        isActionBtnDisabled: false,

        emailErrorMessage: '',
        phoneErrorMessage: '',

        orgName: dev.get('data.organizationName') || '',
        phone: dev.get('data.organizationPhone') || '',
        email: dev.get('data.organizationEMail') || '',
        address: dev.get('data.organizationAddress') || '',
        custom: dev.get('data.organizationCustom') || '',
        originalOrgName: dev.get('data.organizationName') || '',
        originalPhone: dev.get('data.organizationPhone') || '',
        originalEmail: dev.get('data.organizationEMail') || '',
        originalAddress: dev.get('data.organizationAddress') || '',
        originalCustom: dev.get('data.organizationCustom') || ''
      });

      // Everything is set, release the paused
      this.set('paused', false);
    },

    deviceCountDetails: function() {
      var count = this.get('devices.length');
      return (count > 1) ? count : null;
    }.property('devices.[]'),

    requiredNameErrorMsg: 'shared.validationMessages.requiredOrgName'.tr(),
    nameErrorMessage: function() {
      if((this.get('phone.length') !== 0 || this.get('email.length') !== 0 || this.get('address.length') !== 0 || this.get('custom.length') !== 0) && this.get('orgName.length') === 0) {
        return this.get('requiredNameErrorMsg');
      }

      return '';
    }.property('orgName', 'phone', 'email', 'address', 'custom'),

    onFieldChange: function () {
      if (this.get('paused')) { return; }

      var orgName = this.get('orgName'), phone = this.get('phone'), email = this.get('email'),
         address = this.get('address'), custom = this.get('custom'),
        emailErrorMessage = this.get('emailErrorMessage'),
        phoneErrorMessage = this.get('phoneErrorMessage'),
        nameErrorMessage = this.get('nameErrorMessage');

      var fieldsChanged = this.get('originalOrgName') !== orgName || this.get('originalPhone') !== phone ||
        this.get('originalEmail') !== email || this.get('originalAddress') !== address ||
        this.get('originalCustom') !== custom;

      var isButtonEnabled = (fieldsChanged && emailErrorMessage === '' && phoneErrorMessage === '' && nameErrorMessage === '') ||
        (Em.isEmpty(orgName) && Em.isEmpty(phone) && Em.isEmpty(email) && Em.isEmpty(address) &&
          Em.isEmpty(custom));

      this.set('isActionBtnDisabled', !isButtonEnabled);
    }.observes('orgName', 'phone', 'email', 'address', 'custom', 'emailErrorMessage', 'phoneErrorMessage'),

    buildAction: function () {
      this.set('urlForHelp', null);
      return AmData.get('actions.AmMobileDeviceSetOrganizationInfoAction').create({
        deviceIds: this.get('deviceIds'),
        name: this.get('orgName'),
        phone: this.get('phone'),
        email: this.get('email'),
        address: this.get('address'),
        custom: this.get('custom')
      });
    },

    actions: {
      clearFields: function () {
        this.setProperties({
          emailErrorMessage: '',
          phoneErrorMessage: '',

          orgName: '',
          phone: '',
          email: '',
          address: '',
          custom: ''
        });
      }
    }
  });
});
