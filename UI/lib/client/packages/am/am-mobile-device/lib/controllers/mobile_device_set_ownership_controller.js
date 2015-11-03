define([
  'ember',
  'help',
  '../namespace',
  'desktop',
  'am-desktop',
  'am-data',

  './mobile_device_summary_list_controller',
  '../views/mobile_device_commands_container_view',
  'text!../templates/mobile_device_set_ownership.handlebars'
], function (
  Em,
  Help,
  AmMobileDevice,
  Desktop,
  AmDesktop,
  AmData,

  MobileDeviceSummaryListController,
  MobileDeviceCommandsContainerView,
  MobileDeviceSetOwnershipTemplate
  ) {
  'use strict';

  return AmDesktop.ModalActionController.extend({
    mobileDeviceItemController: Em.inject.controller('amMobileDeviceItem'),

    headingIconClass: 'icon-square-attention1',
    addModalClass: 'device-command-window',

    heading: 'amMobileDevice.modals.setDeviceOwnership.heading'.tr(),
    confirmPrompt: 'amMobileDevice.modals.setDeviceOwnership.confirmPrompt'.tr(),

    actionWarning: null,
    actionDescription: null,

    actionButtonLabel: 'amMobileDevice.modals.setDeviceOwnership.buttons.actionButtonLabel'.tr(),

    inProgressMsg: 'amMobileDevice.modals.setDeviceOwnership.inProgressMsg'.tr(),
    successMsg: 'amMobileDevice.modals.setDeviceOwnership.successMsg'.tr(),
    errorMsg: 'amMobileDevice.modals.setDeviceOwnership.errorMsg'.tr(),
    errorDetailsMsg: 'amMobileDevice.modals.setDeviceOwnership.errorDetailsMsg'.tr(),

    tIsUndefined:'amMobileDevice.modals.setDeviceOwnership.isUndefined'.tr(),
    tIsCompany: 'amMobileDevice.modals.setDeviceOwnership.isCompany'.tr(),
    tIsUser: 'amMobileDevice.modals.setDeviceOwnership.isUser'.tr(),
    tIsGuest: 'amMobileDevice.modals.setDeviceOwnership.isGuest'.tr(),

    MobileDevicePropertiesView: Em.View.extend({
      defaultTemplate: Em.Handlebars.compile(MobileDeviceSetOwnershipTemplate)
    }),

    confirmationView: MobileDeviceCommandsContainerView,

    urlForHelp: null,

    devices: Em.computed.oneWay('model'),
    deviceIds: null,

    inProgress: false,

    ownership: '0',
    ownershipOptions: function() {
      return Em.A([
        {
          value: '0',
          label: this.get('tIsUndefined'),
          class: 'is-radio-checked-isUndefined'
        }, {
          value: '1',
          label: this.get('tIsCompany'),
          class: 'is-radio-checked-isCompany'
        },
        {
          value: '2',
          label: this.get('tIsUser'),
          class: 'is-radio-checked-isUser'
        }, {
          value: '3',
          label: this.get('tIsGuest'),
          class: 'is-radio-checked-isGuest'
        }
      ]);
    }.property(),

    initProperties: function (devices) {
      var deviceIds = devices.mapBy('id');

      this.setProperties({
        inProgress: true,
        urlForHelp: Help.uri(1013)
      });

      if ((devices.get('length') === 1 || AmMobileDevice.isIdenticalDeviceFields(devices, ['data.ownershipNumeric'])) && !Em.isEmpty(devices[0].get('data.ownershipNumeric'))) {
        this.set('ownership', devices[0].get('data.ownershipNumeric').toString());
      }

      this.setProperties({
        deviceIds: deviceIds,

        actionWarning: null,
        actionDescription: null,

        inProgress: false
      });
    },

    deviceCountDetails: function() {
      var count = this.get('devices.length');
      return (count > 1) ? count : null;
    }.property('devices.[]'),

    buildAction: function () {
      var self = this;
      this.set('urlForHelp', null);

      return AmData.get('actions.AmMobileDeviceSetOwnershipAction').create({
        deviceIds: self.get('deviceIds'),
        ownership: self.get('ownership')
      });
    },

    // Force the details page to refresh after the command is executed
    onSuccessCallback: function () {
      // Reset the success related properties
      // Set them again when the next request (forceUpdate) is successfully done
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
    }
  });
});
