define([
  'ember',
  'env',
  'ui',
  'guid',
  'query',
  'desktop',
  'am-desktop',
  'packages/am/am-app-foundation',
  'am-computer-formatter',
  'packages/platform/nav-page-view',

  './lib/namespace',
  './lib/controllers/computer_list_controller',
  './lib/controllers/computer_group_list_controller',
  './lib/controllers/computer_item_controller',
  './lib/controllers/computer_send_message_controller',
  './lib/controllers/computer_gather_inventory_controller',
  './lib/controllers/computer_device_freeze_controller',
  './lib/controllers/computer_data_delete_controller',
  './lib/controllers/computer_device_unfreeze_controller',
  'packages/am/am-mobile-command/lib/controllers/command_computer_details_controller',
  './lib/controllers/computer_data_delete_service_agreement_controller',
  './lib/controllers/computer_item_related_memory_controller',
  './lib/controllers/computer_item_related_installed_software_pc_controller',
  './lib/controllers/computer_item_related_installed_software_mac_controller',
  './lib/controllers/computer_item_related_missing_patch_pc_controller',
  './lib/controllers/computer_item_related_missing_patch_mac_controller',
  './lib/controllers/computer_item_related_volume_pc_controller',
  './lib/controllers/computer_item_related_volume_mac_controller',
  './lib/controllers/computer_item_related_network_adapter_controller',
  './lib/controllers/computer_item_related_installed_profile_controller',

  './lib/views/computer_item_about_computer_view',
  './lib/views/computer_item_hardware_view',
  './lib/views/computer_item_system_software_view',
  './lib/views/computer_item_cpu_view',
  './lib/views/computer_data_delete_service_agreement_view',

  'text!./lib/templates/computer_device_freeze_wizard_flow.handlebars',
  'text!./lib/templates/computer_device_delete_wizard_flow.handlebars',

  'am-data',
  'i18n!./nls/strings',
  'locale'
], function (
  Em,
  Env,
  UI,
  Guid,
  Query,
  Desktop,
  AmDesktop,
  AmAppFoundation,
  AmComputerFormatter,
  NavPageView,

  AmComputer,
  ComputerListController,
  ComputerGroupListController,
  ComputerItemController,
  ComputerSendMessageController,
  ComputerGatherInventoryController,
  ComputerDeviceFreezeController,
  ComputerDataDeleteController,
  ComputerDeviceUnfreezeController,
  AmCommandDetailsController,
  ComputerDataDeleteServiceAgreementController,
  ComputerItemMemoryController,
  ComputerItemInstalledSoftwarePcController,
  ComputerItemInstalledSoftwareMacController,
  ComputerItemMissingPatchPcController,
  ComputerItemMissingPatchMacController,
  ComputerItemVolumePcController,
  ComputerItemVolumeMacController,
  ComputerItemNetworkAdapterController,
  ComputerItemInstalledProfileController,

  AmComputerAboutComputerView,
  AmComputerHardwareView,
  AmComputerSystemSoftwareView,
  AmComputerCpuView,
  ComputerDataDeleteServiceAgreementView,

  AmComputerDeviceFreezeWizardFlowTemplate,
  AmComputerDeviceDeleteWizardFlowTemplate,

  AmData,
  strings,
  Locale
) {
  'use strict';

  // Computer Item Tabs
  // ------------------
  //
  // Tab routes set the tab name in the item controller, so that it renders as active.

  var ComputerItemTabRoute = UI.Route.extend({
    controllerName: 'amComputerItem',
    tabRoutId: null,

    setupController: function(controller, model) {
      var self = this;

      var itemController =  this.controllerFor('amComputerItem');
      var computerId = itemController.get('id');
      var tabRoutId = this.get('tabRoutId');
      var activeTab = tabRoutId;

      var groupId = this.controllerFor('am_computer_groups.show_group').get('id');

      // Have to reload item information with correct isMacPlatform flag
      itemController.loadComputer(computerId, function() {
          transitionToRoute();
          itemController.set('activeTab', activeTab);
        }
      );

      function transitionToRoute() {
        var isMacPlatform = itemController.get('isMacPlatform');

        if (isMacPlatform) {
          // Wrong or none existing rout/activeTab, change it to the right/default one
          switch (tabRoutId) {
            case 'volume_pc':
              activeTab = 'volume_mac';
              break;
            case 'missing_patch_pc':
              activeTab = 'missing_patch_mac';
              break;
            case 'installed_software_pc':
              activeTab = 'installed_software_mac';
              break;
          }

        } else {
          switch (tabRoutId) {
            case 'installed_profile':
              activeTab = 'about';
              break;
            case 'volume_mac':
              activeTab = 'volume_pc';
              break;
            case 'missing_patch_mac':
              activeTab = 'missing_patch_pc';
              break;
            case 'installed_software_mac':
              activeTab = 'installed_software_pc';
              break;
          }
        }

        self.transitionTo('am_computer_item.' + activeTab, groupId, computerId);
      }
    }
  });

  var computerActions = {
    gotoListItem: function(id) {
      this.transitionTo('am_computer_item.about', this.get('controller.id'), id);
    },

    sendMessageCommand: function(selectionsList) {
      this.showModal({ name: 'am_computer_groups_send_message', model: Em.isNone(selectionsList) ? Em.makeArray(this.get('controller.content')) : selectionsList  });
    },

    gatherInventoryCommand: function(selectionsList) {
      this.showModal({ name: 'am_computer_groups_gather_inventory', model: Em.isNone(selectionsList) ? Em.makeArray(this.get('controller.content')) : selectionsList  });
    },

    deviceFreezeCommand: function(selectionsList) {
      this.showModal({ name: 'am_computer_groups_device_freeze', model: Em.isNone(selectionsList) ? Em.makeArray(this.get('controller.content')) : selectionsList  });
    },

    dataDeleteCommand: function(selectionsList) {
      this.showModal({ name: 'am_computer_groups_data_delete', model: Em.isNone(selectionsList) ? Em.makeArray(this.get('controller.content')) : selectionsList  });
    },

    deviceUnfreezeCommand: function(selectionsList) {
      this.showModal({ name: 'am_computer_groups_device_unfreeze', model: Em.isNone(selectionsList) ? Em.makeArray(this.get('controller.content')) : selectionsList  });
    },

    showSeviceAgreementModal: function() {
      this.showModal({ name: 'am_computer_groups_show_service_agreement' })
    },

    selectColumns: function (controller) {
      var listController = !Em.isNone(controller) ? controller : this.get('controller');
      this.showModal({ name: 'am_computer_groups_select_columns', model: { listController: listController } });
    }

  };

  var tabActions = {
    selectColumns: function (controller) {
      var listController = !Em.isNone(controller) ? controller : this.get('controller');
      this.showModal({ name: 'am_computer_groups_select_columns', model: { listController: listController } });
    }
  };

  var appClasses = {

    // Components
    // ----------

    AmComputerNavItemComponent: NavPageView.NavItemView.extend({
      layout: Em.Handlebars.compile('{{nodeData.data.agentName}}')
    }),

    // Shared Package Controllers
    // --------------------------

    AmComputerNavSizeController: NavPageView.NavSizeController.extend({
      storageKey: 'am.Computer.NavViewSplitterSettings'
    }),

    // Computer List
    // -------------

    AmComputerGroupsRoute: UI.NoSetupRoute.extend({
      actions: {
        gotoNavItem: function(id) {
          this.transitionTo('am_computer_groups.show_group', id);
        }
      }
    }),
    AmComputerGroupsController: ComputerGroupListController,
    AmComputerGroupsView: NavPageView,

    AmComputerGroupsShowGroupRoute: UI.Route.extend(UI.Route.HasBreadcrumbs, {
      actions: computerActions,

      setupController: function(controller, params) {
        controller.resetController();
        controller.loadComputerList(params.group_id);
        this.activateBreadcrumbs(controller);
      }
    }),
    AmComputerGroupsShowGroupController: ComputerListController,
    AmComputerGroupsShowGroupView: AmDesktop.AmListView,

    // Send Message
    AmComputerGroupsSendMessageController: ComputerSendMessageController,
    AmComputerGroupsSendMessageView: Desktop.ModalActionView,

    // Gather Inventory
    AmComputerGroupsGatherInventoryController: ComputerGatherInventoryController,
    AmComputerGroupsGatherInventoryView: Desktop.ModalActionView,

    // Device Freeze
    AmComputerGroupsDeviceFreezeController: ComputerDeviceFreezeController,
    AmComputerGroupsDeviceFreezeView: Em.View.extend({
      defaultTemplate: Em.Handlebars.compile(AmComputerDeviceFreezeWizardFlowTemplate)
    }),

    // Data Delete
    AmComputerGroupsDataDeleteController: ComputerDataDeleteController,
    AmComputerGroupsDataDeleteView: Em.View.extend({
      defaultTemplate: Em.Handlebars.compile(AmComputerDeviceDeleteWizardFlowTemplate)
    }),

    // Device Unfreeze
    AmComputerGroupsDeviceUnfreezeController: ComputerDeviceUnfreezeController,
    AmComputerGroupsDeviceUnfreezeView: Desktop.ModalActionView,

    // Show Agreement
    AmComputerGroupsShowServiceAgreementController: ComputerDataDeleteServiceAgreementController,
    AmComputerGroupsShowServiceAgreementView: ComputerDataDeleteServiceAgreementView,

    // Select Columns
    AmComputerGroupsSelectColumnsController: AmAppFoundation.SelectColumnsController,
    AmComputerGroupsSelectColumnsView: Desktop.ModalColumnChooserView,

    // Computer Item
    // -------------
    AmComputerItemRoute: UI.Route.extend(UI.Route.HasBreadcrumbs, {
      actions: {
        gotoNavItem: function(computerId) {
          var groupId = this.controllerFor('am_computer_groups.show_group').get('id');
          var activeTab = this.get('controller.activeTab');

          this.transitionTo('am_computer_item.' + activeTab, groupId, computerId);
        },

        gotoTab: function(tabRoutId, tabName) {
          var groupId = this.controllerFor('am_computer_groups.show_group').get('id');
          var controller = this.get('controller');
          var computerId = controller.get('id');

          controller.set('activeTab', tabName ? tabName : tabRoutId);
          this.transitionTo('am_computer_item.' + tabRoutId, groupId, computerId);
        },

        commandDetailsAction: function () {
          this.showModal({ name: 'am_computer_item.command_details', model: this.get('controller') });
        },

        openInNewTab: function(esn) {
          if(!Em.isEmpty(esn)) {
            window.open(Env.customerCenterRoot + 'Pages/Administration/ComputerSummary.aspx?esn=' + esn,'_blank');
          }
        }
      },

      setupController: function(controller, params) {
        this.controllerFor('amComputerGroupsShowGroup').loadComputerList(params.group_id);
        // Do not delete this line
        controller.loadComputer(params.computer_id);
        this.activateBreadcrumbs(controller);
      }
    }),

    AmComputerItemController: ComputerItemController,
    AmComputerItemView: AmDesktop.AmNavTabPageView,

    // Command Details
    AmComputerItemCommandDetailsController: AmCommandDetailsController,
    AmComputerItemCommandDetailsView: Desktop.ModalActionView,

    // About
    AmComputerItemAboutRoute: ComputerItemTabRoute.extend({
      tabRoutId: 'about',
      actions: computerActions
    }),
    AmComputerItemAboutView: AmComputerAboutComputerView,

    // Hardware
    AmComputerItemHardwareRoute: ComputerItemTabRoute.extend({
      tabRoutId: 'hardware',

      setupController: function(controller, model) {
        this._super(controller, model);
        controller.get('relatedHardwareController').loadHardwareInfo(controller.id);
      }
    }),
    AmComputerItemHardwareView: AmComputerHardwareView,

    // System Software
    AmComputerItemSystemSoftwareRoute: ComputerItemTabRoute.extend({
      tabRoutId: 'system_software',

      setupController: function(controller, model) {
        this._super(controller, model);
        controller.get('relatedSystemSoftwareController').loadSoftwareInfo(controller.id);
      }

    }),
    AmComputerItemSystemSoftwareView: AmComputerSystemSoftwareView,

    // CPU
    AmComputerItemCpuRoute: ComputerItemTabRoute.extend({
      tabRoutId: 'cpu',

      setupController: function(controller, model) {
        this._super(controller, model);
        controller.get('relatedCpuController').loadCpu(controller.id);
      }

    }),
    AmComputerItemCpuView: AmComputerCpuView,

    // Memory
    AmComputerItemMemoryRoute: ComputerItemTabRoute.extend({
      controllerName: 'amComputerItemMemory',
      tabRoutId: 'memory',
      actions: tabActions,

      setupController: function(controller, model) {
        var itemController =  this.controllerFor('amComputerItem');
        this._super(itemController, model);

        var id = itemController.get('id');
        controller.loadMemory(id);
      }
    }),
    AmComputerItemMemoryController: ComputerItemMemoryController,
    AmComputerItemMemoryView: AmDesktop.AmListView,

    // Volumes PC
    AmComputerItemVolumePcRoute: ComputerItemTabRoute.extend({
      controllerName: 'amComputerItemVolumePc',
      tabRoutId: 'volume_pc',
      actions: tabActions,

      setupController: function(controller, model) {
        var itemController =  this.controllerFor('amComputerItem');
        this._super(itemController, model);

        var id = itemController.get('id');
        controller.loadVolume(id);
      }
    }),
    AmComputerItemVolumePcController: ComputerItemVolumePcController,
    AmComputerItemVolumePcView: AmDesktop.AmListView,

    // Volumes MAC
    AmComputerItemVolumeMacRoute: ComputerItemTabRoute.extend({
      controllerName: 'amComputerItemVolumeMac',
      tabRoutId: 'volume_mac',
      actions: tabActions,

      setupController: function(controller, model) {
        var itemController =  this.controllerFor('amComputerItem');
        this._super(itemController, model);

        var id = itemController.get('id');
        controller.loadVolume(id);
      }
    }),
    AmComputerItemVolumeMacController: ComputerItemVolumeMacController,
    AmComputerItemVolumeMacView: AmDesktop.AmListView,

    // Network Adapter
    AmComputerItemNetworkAdapterRoute: ComputerItemTabRoute.extend({
      controllerName: 'amComputerItemNetworkAdapter',
      tabRoutId: 'network_adapter',
      actions: tabActions,

      setupController: function(controller, model) {
        var itemController =  this.controllerFor('amComputerItem');
        this._super(itemController, model);

        var id = itemController.get('id');
        controller.loadNetworkadapter(id);
      }
    }),
    AmComputerItemNetworkAdapterController: ComputerItemNetworkAdapterController,
    AmComputerItemNetworkAdapterView: AmDesktop.AmListView,

    // Missing Patches PC
    AmComputerItemMissingPatchPcRoute: ComputerItemTabRoute.extend({
      controllerName: 'amComputerItemMissingPatchPc',
      tabRoutId: 'missing_patch_pc',
      actions: tabActions,

      setupController: function(controller, model) {
        var itemController =  this.controllerFor('amComputerItem');
        this._super(itemController, model);

        var id = itemController.get('id');
        controller.loadMissigPatch(id);
      }
    }),
    AmComputerItemMissingPatchPcController: ComputerItemMissingPatchPcController,
    AmComputerItemMissingPatchPcView: AmDesktop.AmListView,

    // Missing Patches MAC
    AmComputerItemMissingPatchMacRoute: ComputerItemTabRoute.extend({
      controllerName: 'amComputerItemMissingPatchMac',
      tabRoutId: 'missing_patch_mac',
      actions: tabActions,

      setupController: function(controller, model) {
        var itemController =  this.controllerFor('amComputerItem');
        this._super(itemController, model);

        var id = itemController.get('id');
        controller.loadMissigPatch(id);
      }
    }),
    AmComputerItemMissingPatchMacController: ComputerItemMissingPatchMacController,
    AmComputerItemMissingPatchMacView: AmDesktop.AmListView,

    // Installed Software PC
    AmComputerItemInstalledSoftwarePcRoute: ComputerItemTabRoute.extend({
      controllerName: 'amComputerItemInstalledSoftwarePc',
      tabRoutId: 'installed_software_pc',
      actions: tabActions,

      setupController: function(controller, model) {
        var itemController =  this.controllerFor('amComputerItem');
        this._super(itemController, model);

        var id = itemController.get('id');
        controller.loadInstalledSoftware(id);
      }
    }),
    AmComputerItemInstalledSoftwarePcController: ComputerItemInstalledSoftwarePcController,
    AmComputerItemInstalledSoftwarePcView: AmDesktop.AmListView,

    // Installed Software MAC
    AmComputerItemInstalledSoftwareMacRoute: ComputerItemTabRoute.extend({
      controllerName: 'amComputerItemInstalledSoftwareMac',
      tabRoutId: 'installed_software_mac',
      actions: tabActions,

      setupController: function(controller, model) {
        var itemController =  this.controllerFor('amComputerItem');
        this._super(itemController, model);

        var id = itemController.get('id');
        controller.loadInstalledSoftware(id);
      }
    }),
    AmComputerItemInstalledSoftwareMacController: ComputerItemInstalledSoftwareMacController,
    AmComputerItemInstalledSoftwareMacView: AmDesktop.AmListView,

    // Installed Profiles
    AmComputerItemInstalledProfileRoute: ComputerItemTabRoute.extend({
      controllerName: 'amComputerItemInstalledProfile',
      tabRoutId: 'installed_profile',
      actions: tabActions,

      setupController: function(controller, model) {
        var itemController =  this.controllerFor('amComputerItem');
        this._super(itemController, model);

        var id = itemController.get('id');
        controller.loadInstalledProfile(id);
      }
    }),
    AmComputerItemInstalledProfileController: ComputerItemInstalledProfileController,
    AmComputerItemInstalledProfileView: AmDesktop.AmListView

  };

  return {
    buildRoutes: function(router) {
      router.resource('am_computer_groups', { path: '/am_computer_groups' }, function() {
        this.route('show_group', { path: '/:group_id/computers' });
      });

      router.resource('am_computer_item', { path: '/am_computer_groups/:group_id/computers/:computer_id' }, function() {
        this.route('about');
        this.route('hardware');
        this.route('system_software');
        this.route('cpu');
        this.route('memory');
        this.route('volume_pc');
        this.route('volume_mac');
        this.route('network_adapter');
        this.route('missing_patch_pc');
        this.route('missing_patch_mac');
        this.route('installed_software_pc');
        this.route('installed_software_mac');
        this.route('installed_profile');
      });
    },

    initialize: function() {
      AmComputer.reopen({
        AmData: AmData
      });
    },

    appClasses: appClasses,
    appStrings: strings,

    appActions: {
      gotoAmComputerDevice: function(computerId) {
        var groupId = AmData.specs.AmComputerGroupSpec.get('DEFAULT_ID');
        this.transitionTo('am_computer_item.about', groupId, computerId);
      }
    },

    topNavSpec: {
      name: function () {
        return Locale.renderGlobals('amComputer.computerListPage.computersTitle').toString();
      }.property(),

      noLandingAction: true,
      iconClassName: 'icon-device-computer',
      landingPath: 'am_computer_groups.show_group',
      landingIcon: '../packages/am/am-computer/img/icon-computers.png',
      landingButtonClassName: 'is-button-for-landing-page-am-computer',

      routes: [
        {
          name: 'amComputer.computerListPage.allComputersTitle'.tr(),
          path: 'am_computer_groups.show_group',
          context: [AmData.specs.AmComputerGroupSpec.get('DEFAULT_ID')]
        }
      ]
    }
  };
});
