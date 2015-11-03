define([
  'ember',

  'desktop',
  'am-desktop',
  'am-data',
  'packages/platform/query',

  '../views/mobile_policy_move_devices_view',

  'am-multi-select'
], function (Em,
             Desktop,
             AmDesktop,
             AmData,
             Query,
             MoveDevicesView,
             AmMultiSelect) {
  'use strict';

  var PolicySelectionController = AmMultiSelect.MobilePolicyMultiSelectController.extend(Desktop.ChildController, Desktop.TransientController, {
    visibleColumnNames: 'name'.w(),

    getFilteredData: function(data) {
      var id = Number(this.get('policyId'));

      var filteredData = data.filter(function (data) {
        return 255 !== data.get('data.filterType') && data.get('data.isSmartPolicy') === 0 && data.get('data.id') !== id;
      });

      return this._super(filteredData);
    }
  });

  return AmDesktop.ModalActionController.extend({
    heading: 'amMobilePolicies.modals.moveDevices.heading'.tr(),
    headingIconClass: "icon-move-to",

    actionDescription: '',
    actionButtonLabel: 'amMobilePolicies.modals.moveDevices.buttons.moveMobileDevices'.tr(),

    inProgressMsg: 'shared.modals.inProgressMessage'.tr(),
    successMsg: 'shared.modals.successMessage'.tr(),
    errorMsg: 'shared.modals.errorMessage'.tr(),

    confirmationView: MoveDevicesView,
    PolicySelectionView: AmDesktop.AmSelectionListView,

    PolicySelectionController: PolicySelectionController,
    policySelectionController: null,

    isActionBtnDisabled: true,

    initProperties: function () {
      var model = this.get('model');
      var policyId = model.policyId, devices = model.devices;

      this.setProperties({
        policyId: policyId,
        devices: devices,

        policySelectionController: this.PolicySelectionController.create({
          parentController: this,
          policyId: policyId
        })
      })
    },

    onPolicySelectionChanged: function () {
      this.set('isActionBtnDisabled', this.get('policySelectionController.selections').length === 0);
    }.observes('policySelectionController.selections.[]'),

    sendActionRequest: function () {
      var self = this;

      this.setProperties({
        actionInProgress: true,
        confirmingAction: false,
        statusMsg: this.get('inProgressMsg')
      });

      var onError = function (ajaxError) {
        self.set('actionInProgress', false);
        self.actionDidFail(ajaxError);
      };

      // UGLY CODE WARNING
      // EP: removeDevicesAction() and addDevicesAction() return ember objects but for some weird reason
      // I can't extend() them. As such, I use reopen() which changes the class definition within the
      // scope of this controller. This is a bad idea as now invoke() has to follow strictly after the
      // reopen() as any subsequent reopens here will override the endpoint for the previous actions. :(
      var action1 = this.removeDevicesAction().reopen({
        onError: onError
      });
      action1.invoke();

      var action2 = this.addDevicesAction().reopen({
        onSuccess: function (rsp) {
          self.set('actionInProgress', false);
          self.actionDidSucceed(rsp);
        },

        onError: onError
      });
      action2.invoke();

      return true;
    },

    removeDevicesAction: function () {
      var self = this;
      return AmData.get('actions.AmMobilePolicyToMobileDeviceMapDeleteAction').create({
        mobileDeviceIds: this.get('devices'),
        mobilePolicyIds: Em.A([self.get('policyId')])
      });
    },

    addDevicesAction: function () {
      return AmData.get('actions.AmMobilePolicyToMobileDeviceMapCreateAction').create({
        mobileDeviceIds: this.get('devices'),
        mobilePolicyIds: this.get('policySelectionController.selections')
      });
    }
  });
});
