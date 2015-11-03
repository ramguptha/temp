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

    tHeaderOneDevice: 'amMobileDevice.modals.removeMobileDeviceFromPolicy.headingOneDevice'.tr(),
    tHeaderManyDevices: 'amMobileDevice.modals.removeMobileDeviceFromPolicy.headingManyDevices'.tr(),
    tActionWarning: 'amMobileDevice.modals.removeMobileDeviceFromPolicy.actionWarning'.tr(),

    heading: function () {
      return (this.get('model.policies').length === 1) ? this.get('tHeaderOneDevice') : this.get('tHeaderManyDevices');
    }.property('model.policies.[]'),

    headingIconClass: 'icon-square-attention1',

    actionWarning: null,
    actionDescription: null,

    inProgressMsg: 'shared.modals.inProgressMessage'.tr(),
    successMsg: 'shared.modals.successMessage'.tr(),
    errorMsg: 'shared.modals.errorMessage'.tr(),

    actionButtonLabel:  'amMobileDevice.modals.removeMobileDeviceFromPolicy.buttons.actionButtonLabel'.tr(),

    confirmationView: Desktop.ModalActionConfirmView,

    deviceId: null,
    nonSmartPolicies: Em.A(),

    policyName: null,
    smartPoliciesLength: null,

    initProperties: function () {
      var model = this.get('model');

      var deviceId = model.deviceId;
      var policies = model.policies;

      var policyIds = '';
      var smartPolicies = Em.A([]);
      var nonSmartPolicies = Em.A([]);

      for (var i = 0; i < policies.length; i++) {
        policyIds += policies[i].get('id') + ', ';
        if ((policies[i]).get('data.isSmartPolicy')) {
          smartPolicies.pushObject(policies[i]);
        } else {
          nonSmartPolicies.pushObject(policies[i]);
        }
      }

      if (policies.length === 1) {
        this.set('policyName', policies[0].get('name'));
      }

      this.setProperties({
        deviceId: deviceId,
        nonSmartPolicies: nonSmartPolicies,
        actionDescription: null,
        actionWarning: this.get('tActionWarning')
      });
    },

    buildAction: function () {
      return AmData.get('actions.AmMobilePolicyToMobileDeviceMapDeleteAction').create({
        mobileDeviceIds: Em.A([this.get('deviceId')]),
        mobilePolicyIds: this.get('nonSmartPolicies').mapBy('id')
      });
    }
  });
});
