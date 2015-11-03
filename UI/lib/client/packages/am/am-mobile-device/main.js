define([
  'ember',
  'ui',
  'desktop',
  'am-desktop',
  'packages/am/am-app-foundation',
  'packages/platform/nav-page-view',

  './lib/namespace',
  './lib/controllers/mobile_device_item_controller',
  './lib/controllers/mobile_device_list_controller',
  './lib/controllers/mobile_device_group_list_controller',

  './lib/controllers/mobile_device_lock_controller',
  './lib/controllers/mobile_device_clear_passcode_controller',
  './lib/controllers/mobile_device_remote_erase_controller',
  './lib/controllers/mobile_device_send_message_controller',
  './lib/controllers/mobile_device_update_info_controller',
  './lib/controllers/mobile_device_set_roaming_options_controller',
  './lib/controllers/mobile_device_install_application_controller',
  './lib/controllers/mobile_device_uninstall_application_controller',
  './lib/controllers/mobile_device_install_config_profile_controller',
  './lib/controllers/mobile_device_uninstall_config_profile_controller',
  './lib/controllers/mobile_device_install_provisioning_profile_controller',
  './lib/controllers/mobile_device_uninstall_provisioning_profile_controller',
  './lib/controllers/mobile_device_set_ownership_controller',
  './lib/controllers/mobile_device_set_enrollment_user_controller',
  './lib/controllers/mobile_device_set_device_name_controller',
  './lib/controllers/mobile_device_set_organization_info_controller',
  './lib/controllers/mobile_device_retry_all_controller',
  './lib/controllers/mobile_device_set_activation_lock_options_controller',

  './lib/controllers/mobile_device_item_add_to_policies_controller',
  './lib/controllers/mobile_device_item_remove_from_policies_controller',

  './lib/views/mobile_device_policies_view',
  './lib/views/mobile_device_settings_view',
  './lib/views/mobile_device_user_view',

  './lib/controllers/mobile_device_item_related_content_controller',
  './lib/controllers/mobile_device_item_related_mobile_policies_controller',
  './lib/controllers/mobile_device_item_related_applications_controller',
  './lib/controllers/mobile_device_item_related_config_profiles_controller',
  './lib/controllers/mobile_device_item_related_provisioning_profiles_controller',
  './lib/controllers/mobile_device_item_related_assigned_apps_controller',
  './lib/controllers/mobile_device_item_related_assigned_in_house_apps_controller',
  './lib/controllers/mobile_device_item_related_assigned_profiles_controller',
  './lib/controllers/mobile_device_item_related_administrators_controller',
  './lib/controllers/mobile_device_item_related_certificates_controller',
  './lib/controllers/mobile_device_item_related_custom_field_data_controller',
  './lib/controllers/mobile_device_item_related_user_controller',
  './lib/controllers/mobile_device_item_related_actions_controller',

  './lib/controllers/mobile_device_item_related_custom_field_data_delete_controller',
  './lib/controllers/mobile_device_item_related_custom_field_data_edit_controller',
  './lib/controllers/mobile_device_item_related_actions_remove_controller',
  './lib/controllers/mobile_device_item_related_actions_reapply_controller',

  'am-data',

  'i18n!./nls/strings',
  'locale'
], function (
  Em,
  UI,
  Desktop,
  AmDesktop,
  AmAppFoundation,
  NavPageView,

  AmMobileDevice,
  MobileDeviceItemController,
  MobileDeviceListController,
  MobileDeviceGroupListController,

  MobileDeviceLockController,
  MobileDeviceClearPasscodeController,
  MobileDeviceRemoteEraseController,
  MobileDeviceSendMessageController,
  MobileDeviceUpdateInfoController,
  MobileDeviceSetRoamingOptionsController,
  MobileDeviceInstallApplicationController,
  MobileDeviceUninstallApplicationController,
  MobileDeviceInstallConfigProfileController,
  MobileDeviceUninstallConfigProfileController,
  MobileDeviceInstallProvisioningProfileController,
  MobileDeviceUninstallProvisioningProfileController,
  MobileDeviceSetOwnershipController,
  MobileDeviceSetEnrollmentUserController,
  MobileDeviceSetDeviceNameController,
  MobileDeviceSetDeviceOrganizationInfoController,
  MobileDeviceRetryAllController,
  MobileDeviceSetActivationLockOptionsController,

  MobileDeviceAddDeviceToPoliciesController,
  MobileDeviceRemoveDeviceFromPoliciesController,

  MobileDevicePoliciesView,
  MobileDeviceSettingsView,
  MobileDeviceUserView,

  MobileDeviceItemRelatedContentController,
  MobileDeviceItemRelatedMobilePoliciesController,
  MobileDeviceItemRelatedApplicationsController,
  MobileDeviceItemRelatedConfigProfilesController,
  MobileDeviceItemRelatedProvisioningProfilesController,
  MobileDeviceItemRelatedAssignedThirdPartyAppsController,
  MobileDeviceItemRelatedAssignedInHouseAppsController,
  MobileDeviceItemRelatedAssignedProfilesController,
  MobileDeviceItemRelatedAdministratorsController,
  MobileDeviceItemRelatedCertificatesController,
  MobileDeviceItemRelatedCustomFieldDataController,
  MobileDeviceItemRelatedUserController,
  MobileDeviceItemRelatedPerformedActionsController,

  MobileDeviceItemRelatedCustomFieldDataDeleteController,
  MobileDeviceItemRelatedCustomFieldDataEditController,
  AmMobileDeviceRelatedItemPerformedActionRemoveController,
  AmMobileDeviceRelatedItemPerformedActionReapplyController,

  AmData,

  strings,
  Locale
  ) {
  'use strict';

  var MobileDeviceItemTabRoute = UI.Route.extend({
    tabName: null,

    setupController: function(controller, params) {
      var tabName = this.get('tabName');

      this.set('tabName', tabName);
      this.controllerFor('am_mobile_device_item').set('activeTab', tabName);
    }
  });

  var mobileDeviceActions = {
    gotoDeviceNavItem: function(deviceId) {
      var groupId = this.controllerFor('am_mobile_device_groups_show_group').get('mobileDeviceGroup.id'),
        tab = this.controllerFor('am_mobile_device_item').get('activeTab'),
        deviceData = this.get('controller.content'),
        isManaged = deviceData.get('data.isManaged'),
        osPlatform = deviceData.get('data.osPlatform');

      // Don't allow routing to tabs other than settings, applications or administrators for unmanaged devices
      // Also don't allow for viewing of provisioning profile and certificates tabs for Android devices
      if ((isManaged || (tab === 'settings' || tab === 'applications' || tab === 'administrators')) &&
        (osPlatform === 'iOS' || (osPlatform === 'Android' && (tab !== 'certificates' && tab !== 'provisioningProfiles')))) {
        this.transitionTo('am_mobile_device_item.' + tab, groupId, deviceId);
      } else {
        this.transitionTo('am_mobile_device_item.settings', groupId, deviceId);
      }
    },

    gotoTab: function(tab) {
      var groupId = this.controllerFor('am_mobile_device_groups.show_group').get('id');
      var deviceId = this.get('controller.id');
      this.transitionTo('am_mobile_device_item.' + tab, groupId, deviceId);
    },

    gotoListItem: function(id) {
      var groupId = this.controllerFor('am_mobile_device_groups.show_group').get('id');
      this.transitionTo('am_mobile_device_item.settings', groupId, id);
    },

    setActivationLock: function (selectionsList) {
      this.showModal({ name: 'am_mobile_device_set_activation_lock', model: Em.isNone(selectionsList) ? Em.makeArray(this.get('controller.content')) : selectionsList });
    },

    lockDevice: function (selectionsList) {
      this.showModal({ name: 'am_mobile_device_lock', model: Em.isNone(selectionsList) ? Em.makeArray(this.get('controller.content')) : selectionsList });
    },

    clearPasscode: function (selectionsList) {
      this.showModal({ name: 'am_mobile_device_clear_passcode', model: Em.isNone(selectionsList) ? Em.makeArray(this.get('controller.content')) : selectionsList });
    },

    remoteErase: function (selectionsList) {
      this.showModal({ name: 'am_mobile_device_remote_erase', model: Em.isNone(selectionsList) ? Em.makeArray(this.get('controller.content')) : selectionsList });
    },

    sendMessage: function (selectionsList) {
      this.showModal({ name: 'am_mobile_device_send_message', model: Em.isNone(selectionsList) ? Em.makeArray(this.get('controller.content')) : selectionsList });
    },

    updateDeviceInfo: function (selectionsList) {
      this.showModal({ name: 'am_mobile_device_update_info', model: Em.isNone(selectionsList) ? Em.makeArray(this.get('controller.content')) : selectionsList });
    },

    setRoamingOptions: function (selectionsList) {
      this.showModal({ name: 'am_mobile_device_set_roaming_options', model: Em.isNone(selectionsList) ? Em.makeArray(this.get('controller.content')) : selectionsList });
    },

    installApplication: function (selectionsList) {
      this.showModal({ name: 'am_mobile_device_install_application', model: Em.isNone(selectionsList) ? Em.makeArray(this.get('controller.content')) : selectionsList });
    },

    installConfigProfile: function (selectionsList) {
      this.showModal({ name: 'am_mobile_device_install_config_profile', model: Em.isNone(selectionsList) ? Em.makeArray(this.get('controller.content')) : selectionsList });
    },

    installProvisioningProfile: function (selectionsList) {
      this.showModal({ name: 'am_mobile_device_install_provisioning_profile', model: Em.isNone(selectionsList) ? Em.makeArray(this.get('controller.content')) : selectionsList });
    },

    setDeviceOwnership: function(selectionsList) {
      this.showModal({ name: 'am_mobile_device_set_device_ownership', model: Em.isNone(selectionsList) ? Em.makeArray(this.get('controller.content')) : selectionsList });
    },

    setDeviceEnrollmentUser: function(selectionsList) {
      this.showModal({ name: 'am_mobile_device_set_device_enrollment_user', model: Em.isNone(selectionsList) ? Em.makeArray(this.get('controller.content')) : selectionsList });
    },

    setDeviceName: function(selectionsList) {
      this.showModal({ name: 'am_mobile_device_set_device_name', model: Em.isNone(selectionsList) ? Em.makeArray(this.get('controller.content')) : selectionsList });
    },

    setDeviceOrganizationInfo: function(selectionsList) {
      this.showModal({ name: 'am_mobile_device_set_device_organization_info', model: Em.isNone(selectionsList) ? Em.makeArray(this.get('controller.content')) : selectionsList });
    },

    retryAllCommand: function(selectionsList) {
      this.showModal({ name: 'am_mobile_device_retry_all', model: Em.isNone(selectionsList) ? Em.makeArray(this.get('controller.content')) : selectionsList });
    },

    selectColumns: function (controller) {
      controller = !Em.isNone(controller) ? controller : this.get('controller');
      this.showModal({ name: 'am_mobile_device_select_columns', model: { listController: controller } });
    },

    addToPolicies: function() {
      this.showModal({ name: 'am_mobile_device_add_device_to_policies', model: this.get('controller.id') });
    },

    removeFromPolicies: function(context) {
      var id = this.get('controller.id');
      this.showModal({ name: 'am_mobile_device_remove_device_from_policies',
        model: {
          deviceId: id,
          policies: context
        }
      });
    },

    uninstallApplication: function (selectionsList) {
      var id = this.get('controller.id');
      this.showModal({ name: 'am_mobile_device_uninstall_application',
        model: {
          deviceId: id,
          applications: selectionsList
        }
      });
    },

    uninstallConfigProfile: function(selectionsList) {
      var id = this.get('controller.id');
      this.showModal({ name: 'am_mobile_device_uninstall_config_profile',
        model: {
          deviceId: id,
          configProfiles: selectionsList
        }
      });
    },

    uninstallProvisioningProfile: function(selectionsList) {
      var id = this.get('controller.id');
      this.showModal({ name: 'am_mobile_device_uninstall_provisioning_profile',
        model: {
          deviceId: id,
          provisioningProfiles: selectionsList
        }
      });
    },

    deleteCustomFieldDataAction: function (context) {
      var id = this.get('controller.id'), ids = Em.A([]);

      context.forEach(function(item) {
        if(!Em.isNone(item.get('content.data.dataValue'))) {
          ids.pushObject(item.get('content.data.id'));
        }
      });

      this.showModal({
        name: 'am_mobile_device_item_related_custom_field_data_delete',
        model: {
          deviceId: id,
          itemIds: ids
        }
      });
    },

    editCustomFieldDataAction: function (context) {
      var controller = this.get('controller');
      var id = controller.get('id'), deviceName = controller.get('content.name');

      this.showModal({
        name: 'am_mobile_device_item_related_custom_field_data_edit',
        model: {
          deviceId: id,
          item: context,
          deviceName: deviceName
        }
      });
    }
  };

  var appClasses = {

    // Shared Package Controllers
    // --------------------------

    AmMobileDeviceNavSizeController: NavPageView.NavSizeController.extend({
      storageKey: 'am.MobileDevice.NavViewSplitterSettings'
    }),

    // Mobile Device Groups
    // --------------------

    AmMobileDeviceGroupsRoute: UI.NoSetupRoute.extend({
      actions: {
        gotoNavItem: function(id) {
          this.transitionTo('am_mobile_device_groups.show_group', id);
        }
      }
    }),
    AmMobileDeviceGroupsController: MobileDeviceGroupListController,
    AmMobileDeviceGroupsView: NavPageView,

    AmMobileDeviceGroupsShowGroupRoute: UI.Route.extend(UI.Route.HasBreadcrumbs, {
      actions: mobileDeviceActions,

      setupController: function(controller, params) {
        this.activateBreadcrumbs(controller);

        controller.resetController();
        controller.loadMobileDeviceList(params.group_id);
      }
    }),
    AmMobileDeviceGroupsShowGroupController: MobileDeviceListController,
    AmMobileDeviceGroupsShowGroupView: AmDesktop.AmListView,

    // Set Activation Lock

    AmMobileDeviceSetActivationLockController: MobileDeviceSetActivationLockOptionsController,
    AmMobileDeviceSetActivationLockView: Desktop.ModalActionView,

    // Lock Device

    AmMobileDeviceLockController: MobileDeviceLockController,
    AmMobileDeviceLockView: Desktop.ModalActionView,

    // Clear Passcode

    AmMobileDeviceClearPasscodeController: MobileDeviceClearPasscodeController,
    AmMobileDeviceClearPasscodeView: Desktop.ModalActionView,

    // Remote Erase

    AmMobileDeviceRemoteEraseController: MobileDeviceRemoteEraseController,
    AmMobileDeviceRemoteEraseView: Desktop.ModalActionView,

    // Send Message

    AmMobileDeviceSendMessageController: MobileDeviceSendMessageController,
    AmMobileDeviceSendMessageView: Desktop.ModalActionView,

    // Update Info

    AmMobileDeviceUpdateInfoController: MobileDeviceUpdateInfoController,
    AmMobileDeviceUpdateInfoView: Desktop.ModalActionView,

    // Set Roaming Options

    AmMobileDeviceSetRoamingOptionsController: MobileDeviceSetRoamingOptionsController,
    AmMobileDeviceSetRoamingOptionsView: Desktop.ModalActionView,

    // Install Application

    AmMobileDeviceInstallApplicationController: MobileDeviceInstallApplicationController,
    AmMobileDeviceInstallApplicationView: Desktop.ModalActionView.extend({
      didInsertElement: function() {
        this._super();

        // it's not possible to style this via imported CSS only since the grid displays data via two tables:
        // 'paged-table frozen' ( for check marks ) and 'paged-table free' ( for the data )
        var style = Em.$('<style>.paged-table th, .paged-table td { height: 35px; }</style>');
        this.set('cssAddedToHead', Em.$('html > head').append(style));
      },
      willDestroyElement: function() {
        // remove all the styles in head so that we don't populate the DOM every time this modal is opened
        // may break things if we start putting other styles in head
        Em.$(Em.$('head style').get(0)).remove();
      }
    }),

    // Uninstall Application

    AmMobileDeviceUninstallApplicationController: MobileDeviceUninstallApplicationController,
    AmMobileDeviceUninstallApplicationView: Desktop.ModalActionView,

    // Install Config Profile

    AmMobileDeviceInstallConfigProfileController: MobileDeviceInstallConfigProfileController,
    AmMobileDeviceInstallConfigProfileView: Desktop.ModalActionView,

    // Uninstall Config Profile

    AmMobileDeviceUninstallConfigProfileController: MobileDeviceUninstallConfigProfileController,
    AmMobileDeviceUninstallConfigProfileView: Desktop.ModalActionView,

    // Install Provisioning Profile

    AmMobileDeviceInstallProvisioningProfileController: MobileDeviceInstallProvisioningProfileController,
    AmMobileDeviceInstallProvisioningProfileView: Desktop.ModalActionView,

    // Uninstall Provisioning Profile

    AmMobileDeviceUninstallProvisioningProfileController: MobileDeviceUninstallProvisioningProfileController,
    AmMobileDeviceUninstallProvisioningProfileView: Desktop.ModalActionView,

    // Set Device Ownership

    AmMobileDeviceSetDeviceOwnershipController: MobileDeviceSetOwnershipController,
    AmMobileDeviceSetDeviceOwnershipView: Desktop.ModalActionView,

    // Set Device Enrollment User

    AmMobileDeviceSetDeviceEnrollmentUserController: MobileDeviceSetEnrollmentUserController,
    AmMobileDeviceSetDeviceEnrollmentUserView: Desktop.ModalActionView,

    // Set Device Name

    AmMobileDeviceSetDeviceNameController: MobileDeviceSetDeviceNameController,
    AmMobileDeviceSetDeviceNameView: Desktop.ModalActionView,

    // Set Device Organization Info

    AmMobileDeviceSetDeviceOrganizationInfoController: MobileDeviceSetDeviceOrganizationInfoController,
    AmMobileDeviceSetDeviceOrganizationInfoView: Desktop.ModalActionView,

    // Retry All Command

    AmMobileDeviceRetryAllController: MobileDeviceRetryAllController,
    AmMobileDeviceRetryAllView: Desktop.ModalActionView,

    // Select Columns

    AmMobileDeviceSelectColumnsController: AmAppFoundation.SelectColumnsController,
    AmMobileDeviceSelectColumnsView: Desktop.ModalColumnChooserView,

    // Add Mobile Device to Policies

    AmMobileDeviceAddDeviceToPoliciesController: MobileDeviceAddDeviceToPoliciesController,
    AmMobileDeviceAddDeviceToPoliciesView: Desktop.ModalActionView,

    // Remove Mobile Device from Policies

    AmMobileDeviceRemoveDeviceFromPoliciesController: MobileDeviceRemoveDeviceFromPoliciesController,
    AmMobileDeviceRemoveDeviceFromPoliciesView: Desktop.ModalActionView,

    // Custom Field Data Delete
    AmMobileDeviceItemRelatedCustomFieldDataDeleteController: MobileDeviceItemRelatedCustomFieldDataDeleteController,
    AmMobileDeviceItemRelatedCustomFieldDataDeleteView:  Desktop.ModalActionView,

    // Custom Field Data Edit
    AmMobileDeviceItemRelatedCustomFieldDataEditController: MobileDeviceItemRelatedCustomFieldDataEditController,
    AmMobileDeviceItemRelatedCustomFieldDataEditView: Desktop.ModalActionView,

    // Remove Performed action
    AmMobileDeviceItemRelatedPerformedActionRemoveController: AmMobileDeviceRelatedItemPerformedActionRemoveController,
    AmMobileDeviceItemRelatedPerformedActionRemoveView: Desktop.ModalActionView,

    // Reapply (re-execute) Performed action
    AmMobileDeviceItemRelatedPerformedActionReapplyController: AmMobileDeviceRelatedItemPerformedActionReapplyController,
    AmMobileDeviceItemRelatedPerformedActionReapplyView: Desktop.ModalActionView,

    // Mobile Device Item
    // ------------------

    // Resource

    AmMobileDeviceItemRoute: UI.Route.extend(UI.Route.HasBreadcrumbs, {
      actions: mobileDeviceActions,

      setupController: function(controller, params) {
        this.controllerFor('am_mobile_device_groups_show_group').loadMobileDeviceList(params.group_id);

        //controller.set('activeTab', params.tab_id);
        controller.loadMobileDevice(params.device_id);
        this.activateBreadcrumbs(controller);
      }
    }),
    AmMobileDeviceItemController: MobileDeviceItemController,
    AmMobileDeviceItemView: AmDesktop.AmNavTabPageView,

    // Settings Tab

    AmMobileDeviceItemSettingsRoute: MobileDeviceItemTabRoute.extend({
      controllerName: 'am_mobile_device_item',
      tabName: 'settings',
      setupController: function(controller) {
        this._super();
        controller.loadMobileDevice(controller.get('id'), true);
      }
    }),
    AmMobileDeviceItemSettingsView: MobileDeviceSettingsView,

    // Related Mobile Policies Tab

    AmMobileDeviceItemMobilePoliciesRoute: MobileDeviceItemTabRoute.extend({
      actions: {
        gotoListItem: function (id) {
          this.send('gotoAmMobilePolicy', id);
        }
      },

      tabName: 'mobilePolicies',
      setupController: function(controller, params) {
        this._super(controller, params);
        controller.clearSelections();
      }
    }),
    AmMobileDeviceItemMobilePoliciesController: MobileDeviceItemRelatedMobilePoliciesController,
    AmMobileDeviceItemMobilePoliciesView: AmDesktop.AmListView,

    // Related Content Tab

    AmMobileDeviceItemContentRoute: MobileDeviceItemTabRoute.extend({
      actions: {
        gotoListItem: function(id) {
          this.send('gotoAmAssignableContent', id);
        }
      },

      tabName: 'content'
    }),
    AmMobileDeviceItemContentController: MobileDeviceItemRelatedContentController,
    AmMobileDeviceItemContentView: AmDesktop.AmListView,

    // Related Applications Tab

    AmMobileDeviceItemApplicationsRoute: MobileDeviceItemTabRoute.extend({
      tabName: 'applications',
      setupController: function(controller, params) {
        this._super(controller, params);
        controller.clearSelections();
      }
    }),
    AmMobileDeviceItemApplicationsController: MobileDeviceItemRelatedApplicationsController,
    AmMobileDeviceItemApplicationsView: AmDesktop.AmListView,

    // Related Certificates Tab

    AmMobileDeviceItemCertificatesRoute: MobileDeviceItemTabRoute.extend({
      tabName: 'certificates'
    }),
    AmMobileDeviceItemCertificatesController: MobileDeviceItemRelatedCertificatesController,
    AmMobileDeviceItemCertificatesView: AmDesktop.AmListView,

    // Related Custom Fields Tab

    AmMobileDeviceItemCustomFieldsRoute: MobileDeviceItemTabRoute.extend({
      tabName: 'customFields',
      setupController: function(controller, params) {
        this._super(controller, params);
        controller.clearSelections();
      }
    }),
    AmMobileDeviceItemCustomFieldsController: MobileDeviceItemRelatedCustomFieldDataController,
    AmMobileDeviceItemCustomFieldsView: AmDesktop.AmListView,

    // Related Configuration Profiles Tab

    AmMobileDeviceItemConfigProfilesRoute: MobileDeviceItemTabRoute.extend({
      tabName: 'configProfiles',
      setupController: function(controller, params) {
        this._super(controller, params);
        controller.clearSelections();
      }
    }),
    AmMobileDeviceItemConfigProfilesController: MobileDeviceItemRelatedConfigProfilesController,
    AmMobileDeviceItemConfigProfilesView: AmDesktop.AmListView,

    // Related Provisioning Profiles Tab

    AmMobileDeviceItemProvisioningProfilesRoute: MobileDeviceItemTabRoute.extend({
      tabName: 'provisioningProfiles',
      setupController: function(controller, params) {
        this._super(controller, params);
        controller.clearSelections();
      }
    }),
    AmMobileDeviceItemProvisioningProfilesController: MobileDeviceItemRelatedProvisioningProfilesController,
    AmMobileDeviceItemProvisioningProfilesView: AmDesktop.AmListView,

    // Assigned Third Party Applications Tab

    AmMobileDeviceItemAssignedThirdPartyAppsRoute: MobileDeviceItemTabRoute.extend({
      tabName: 'assignedThirdPartyApps'
    }),
    AmMobileDeviceItemAssignedThirdPartyAppsController: MobileDeviceItemRelatedAssignedThirdPartyAppsController,
    AmMobileDeviceItemAssignedThirdPartyAppsView: AmDesktop.AmListView,

    // Assigned In-House Applications Tab

    AmMobileDeviceItemAssignedInHouseAppsRoute: MobileDeviceItemTabRoute.extend({
      tabName: 'assignedInHouseApps'
    }),
    AmMobileDeviceItemAssignedInHouseAppsController: MobileDeviceItemRelatedAssignedInHouseAppsController,
    AmMobileDeviceItemAssignedInHouseAppsView: AmDesktop.AmListView,

    // Assigned Profiles Tab

    AmMobileDeviceItemAssignedProfilesRoute: MobileDeviceItemTabRoute.extend({
      tabName: 'assignedProfiles'
    }),
    AmMobileDeviceItemAssignedProfilesController: MobileDeviceItemRelatedAssignedProfilesController,
    AmMobileDeviceItemAssignedProfilesView: AmDesktop.AmListView,

    // Performed Actions Tab

    AmMobileDeviceItemActionsRoute: MobileDeviceItemTabRoute.extend({
      actions: {
        gotoListItem: function(id) {
          // id is the id of the 'performed' action here and must be mapped to its actual action id here
          this.send('gotoAmAssignableAction', this.get('controller.dataStore.materializedObjectsById')[id].get('data.actionId'));
        },
        removeAction: function (selectionsList) {
          var ids = Em.A([]);
          for (var i = 0; i < selectionsList.length; i++) {
            ids.pushObject(selectionsList[i]);
          }
          var deviceId = this.controllerFor('amMobileDeviceItem').get('id');
          this.showModal({ name: 'am_mobile_device_item_related_performed_action_remove', model: { actionIds: ids, deviceId: deviceId } });
        },
        reapplyAction: function (selectionsList) {
          var ids = Em.A([]);
          for (var i = 0; i < selectionsList.length; i++) {
            ids.pushObject(selectionsList[i].get('content.data.actionUniqueId'));
          }
          var deviceId = this.controllerFor('amMobileDeviceItem').get('id');
          this.showModal({ name: 'am_mobile_device_item_related_performed_action_reapply', model: { actionIds: ids, deviceId: deviceId } });
        }
      },

      tabName: 'actions',

      setupController: function(controller, params) {
        this._super(controller, params);
        controller.clearSelections();
      }
    }),
    AmMobileDeviceItemActionsController: MobileDeviceItemRelatedPerformedActionsController,
    AmMobileDeviceItemActionsView: AmDesktop.AmListView,

    // Assigned Administrators Tab

    AmMobileDeviceItemAdministratorsRoute: MobileDeviceItemTabRoute.extend({
      tabName: 'administrators'
    }),
    AmMobileDeviceItemAdministratorsController: MobileDeviceItemRelatedAdministratorsController,
    AmMobileDeviceItemAdministratorsView: AmDesktop.AmListView,

    // Device Owner Tab

    AmMobileDeviceItemUserRoute: MobileDeviceItemTabRoute.extend({
      tabName: 'user',
      setupController: function(controller, model) {
        this._super(controller, model);
        controller.loadUserInfo(this.controllerFor('amMobileDeviceItem').get('id'));
      }
    }),
    AmMobileDeviceItemUserController: MobileDeviceItemRelatedUserController,
    AmMobileDeviceItemUserView: MobileDeviceUserView
  };

  return {
    buildRoutes: function(router) {
      router.resource('am_mobile_device_groups', { path: '/am_mobile_device_groups' }, function () {
        this.route('show_group', { path: '/:group_id' });
      });

      router.resource('am_mobile_device_item', { path: '/am_mobile_device_groups/:group_id/devices/:device_id' }, function() {
          this.route('settings', { path: '/settings' });
          this.route('mobilePolicies', { path: '/mobile_policies' });
          this.route('content', { path: '/content' });
          this.route('applications', { path: '/applications' });
          this.route('certificates', { path: '/certificates' });
          this.route('customFields', { path: '/customFields' });
          this.route('configProfiles', { path: '/config_profiles' });
          this.route('provisioningProfiles', { path: '/provisioning_profiles' });
          this.route('assignedThirdPartyApps', { path: '/assigned_third_party_apps' });
          this.route('assignedInHouseApps', { path: '/assigned_in_house_apps' });
          this.route('assignedProfiles', { path: '/assigned_profiles' });
          this.route('administrators', { path: '/administrators' });
          this.route('user', { path: '/user' });
          this.route('actions', { path: '/actions' });
        });
    },

    initialize: function() {
      AmMobileDevice.reopen({
        AmData: AmData,

        store: function () {
          return this.get('AmData.stores.mobileDeviceStore');
        }.property(),

        itemstore: function () {
          return this.get('AmData.stores.mobileDeviceItemStore');
        }.property(),

        spec: function () {
          return this.get('AmData.specs.AmMobileDeviceSpec');
        }.property(),

        groupStore: function () {
          return this.get('AmData.stores.mobileDeviceGroupStore');
        }.property(),

        groupSpec: function () {
          return this.get('AmData.specs.AmMobileDeviceGroupSpec');
        }.property(),

        relatedContentStore: function () {
          return this.get('AmData.stores.contentFromMobileDeviceStore');
        }.property(),

        relatedContentSpec: function () {
          return this.get('AmData.specs.AmContentFromMobileDeviceSpec');
        }.property(),

        relatedMobilePoliciesStore: function () {
          return this.get('AmData.stores.mobilePolicyFromMobileDeviceStore');
        }.property(),

        relatedMobilePoliciesSpec: function () {
          return this.get('AmData.specs.AmMobilePolicyFromMobileDeviceSpec');
        }.property(),

        relatedActionsStore: function () {
          return this.get('AmData.stores.mobileDevicePerformedActionsStore');
        }.property(),

        relatedActionsSpec: function () {
          return this.get('AmData.specs.AmMobileDevicePerformedActionsSpec');
        }.property(),

        userPrefsStore: function () {
          return this.get('AmData.stores.userPrefsStore');
        }.property(),

        userPrefsSpec: function () {
          return this.get('AmData.specs.UserPrefsSpec');
        }.property()
      });
    },

    appClasses: appClasses,
    appStrings: strings,

    appActions: {
      gotoAmMobileDevice: function(id) {
        this.transitionTo(
          'am_mobile_device_item.settings', AmMobileDevice.get('groupSpec.DEFAULT_ID'), id
        );
      }
    },

    topNavSpec: {
      name: function () {
        return Locale.renderGlobals('amMobileDevice.topNavSpec.mobileDevicesTitle').toString();
      }.property(),

      noLandingAction: true,
      iconClassName: 'icon-device',
      landingPath: 'am_mobile_device_groups.show_group',
      landingIcon: '../packages/am/am-mobile-device/img/icon-devices-retina.png',
      landingButtonClassName: 'is-button-for-landing-page-am-mobile-device',

      routes: [
        {
          name: 'amMobileDevice.topNavSpec.allMobileDevicesTitle'.tr(),
          path: 'am_mobile_device_groups.show_group',
          context: function() {
            return [AmMobileDevice.get('groupSpec.DEFAULT_ID')];
          }
        }
      ]
    }
  };
});
