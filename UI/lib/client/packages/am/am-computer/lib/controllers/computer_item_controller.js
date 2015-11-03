define([
  'ember',
  'help',
  'ui',
  'desktop',
  'am-desktop',
  'guid',
  'am-computer-formatter',
  'am-session',

  'am-data',
  'packages/platform/storage',
  'query',

  '../namespace',
  'packages/platform/paged-list-component',

  './computer_item_related_hardware_controller',
  './computer_item_related_system_software_controller',
  './computer_item_related_cpu_controller',

  'text!../templates/computer_nav_item.handlebars',
  'text!../templates/computer_item_warning.handlebars',
  'text!../templates/computer_item_button_block.handlebars'
], function (Em,
             Help,
             UI,
             Desktop,
             AmDesktop,
             Guid,
             AmComputerFormatter,
             AmSession,
             AmData,
             Storage,
             Query,
             AmComputer,
             PagedListComponent,
             AmComputerHardwareController,
             AmComputerSystemSoftwareController,
             AmComputerCpuController,
             navItemTemplate,
             warningItemTemplate,
             buttonBlockTemplate) {
  return Em.Controller.extend({

    // Navigation
    actions: {
      rowClick: function (row) {
        this.send('gotoNavItem', row.get('node.id'));
      }
    },

    navTitle: Em.computed.oneWay('computerListController.name'),
    tSnapContainerSubTitle: 'amComputer.computerListPage.snapContainerSubTitle'.tr('machineModel'),

    // Tabs: About Computer, Hardware...
    tabItemView: Desktop.TabItemView,

    computerListController: Em.inject.controller('amComputerGroupsShowGroup'),
    navSizeController: Em.inject.controller('amAssignableNavSize'),

    NavController: AmDesktop.AmNavController.extend({
      content: Em.computed.alias('parentController.computerListController'),
      dataStore: Em.computed.alias('parentController.computerListController.dataStore'),
      searchQuery: Em.computed.oneWay('parentController.computerListController.searchQuery'),

      // @override
      // Default itemComponent renders the value of the 'name' from nodeData
      // New component needed to be created.
      itemComponent: 'am-computer-nav-item'
    }),

    navController: function () {
      return this.NavController.create({
        parentController: this,
        selectedIdBinding: 'parentController.id'
      });
    }.property(),

    // Computer id
    id: null,
    lock: Guid.generate(),
    osPlatform: null,

    // Used for switching between tabs (volume)
    activeTab: null,

    urlForHelp: Help.uri(1054),

    namespace: function () {
      return AmComputer;
    }.property(),

    init: function () {
      this._super();

      this.setProperties({
        relatedHardwareController: AmComputerHardwareController.create({parentController: this}),
        relatedSystemSoftwareController: AmComputerSystemSoftwareController.create({parentController: this}),
        relatedCpuController: AmComputerCpuController.create({parentController: this})
      });
    },

    loadComputer: function (id, callReady) {
      var self = this;
      this.set('id', id);

      var query = Query.Search.create({
        context: {computerId: id}
      });

      AmData.get('stores.computerItemAgentInfoStore').acquire(this.get('lock'), query, function(data) {
        self.set('model', data.get('content')[0]);
        if (callReady) {
          callReady();
        }
      });
    },

    tabList: function () {
      var self = this;
      var model = this.get('model');
      if (Em.isNone(model)) { // data not yet loaded
        return null;
      }
      var tabList = [
        Em.Object.create({
          labelResource: 'amComputer.tabLabels.aboutComputer',
          item: 'about',
          id: 'about',
          itemClass: 'is-button-for-tab-about'
        })];

      tabList.pushObject(
        Em.Object.create({
          labelResource: 'amComputer.tabLabels.hardware',
          item: 'hardware',
          id: 'hardware',
          itemClass: 'is-button-for-tab-hardware'
        })
      );

      tabList.pushObject(
        Em.Object.create({
          labelResource: 'amComputer.tabLabels.cpu',
          item: 'cpu',
          id: 'cpu',
          itemClass: 'is-button-for-tab-cpu'
        })
      );

      tabList.pushObject(
        Em.Object.create({
          labelResource: 'amComputer.tabLabels.systemSoftware',
          item: 'system_software',
          id: 'system_software',
          itemClass: 'is-button-for-tab-system-software'
        })
      );

      tabList.pushObject(
        Em.Object.create({
          labelResource: 'amComputer.tabLabels.memory',
          item: 'memory',
          id: 'memory',
          itemClass: 'is-button-for-tab-memory'
        })
      );

      tabList.pushObject(
        Em.Object.create({
          labelResource: 'amComputer.tabLabels.volume',
          item: self.get('isMacPlatform') ? 'volume_mac' : 'volume_pc',
          id: 'volume',
          itemClass: 'is-button-for-tab-volume'
        })
      );

      tabList.pushObject(
        Em.Object.create({
          labelResource: 'amComputer.tabLabels.networkAdapter',
          item: 'network_adapter',
          id: 'network_adapter',
          itemClass: 'is-button-for-tab-network-adapter'
        })
      );

      tabList.pushObject(
        Em.Object.create({
          labelResource: 'amComputer.tabLabels.missingPatch',
          item: self.get('isMacPlatform') ? 'missing_patch_mac' : 'missing_patch_pc',
          id: 'missing_patch',
          itemClass: 'is-button-for-tab-missing-patch'
        })
      );

      tabList.pushObject(
        Em.Object.create({
          labelResource: 'amComputer.tabLabels.installedSoftware',
          item: self.get('isMacPlatform') ? 'installed_software_mac' : 'installed_software_pc',
          id: 'installed_software',
          itemClass: 'is-button-for-tab-installed-software'
        })
      );

      if (self.get('isMacPlatform')) {
        tabList.pushObject(
          Em.Object.create({
            labelResource: 'amComputer.tabLabels.installedProfiles',
            item: 'installed_profile',
            id: 'installed_profile',
            itemClass: 'is-button-for-tab-installed-profile'
          })
        );
      }

      return tabList;
    }.property('model'),

    buttonBlockView: AmDesktop.AmNavTabPageView.ButtonBlockView.extend({
      defaultTemplate: Em.Handlebars.compile(buttonBlockTemplate)
    }),

    breadcrumb: function () {
      return UI.Breadcrumb.create({
        parentBreadcrumb: this.get('computerListController.breadcrumb'),
        path: 'am_computer_item.about',

        titleResource: 'amComputer.aboutComputerTab.title',
        controller: this,
        contextBinding: 'controller.breadcrumbContext'
      });
    }.property('computerListController.breadcrumb'),

    breadcrumbContext: function () {
      return [this.get('id'), this.get('activeTab')];
    }.property('id', 'activeTab'),

    // Snap Container
    snapContainerSubTitle: function () {
      var machineModel = this.get('model.data.machineModel');
      return machineModel ? this.get('tSnapContainerSubTitle') : '';
    }.property('model.data.machineModel'),

    machineModel: function () {
      if (!Em.isEmpty(this.get('model.data.machineModel'))) {
        return this.get('model.data.machineModel');
      }
    }.property('model.data.machineModel'),

    onAgenetnameChange: function () {
      if (!Em.isEmpty(this.get('model.data.agentName'))) {
        this.set('model.name', this.get('model.data.agentName'));
      }
    }.observes('model.data.agentName'),

    snapContainerContentClass: function () {
      return (this.get('warningVisible') ? 'snap-container-content unmanaged-device' : 'snap-container-content');
    }.property('model.data.osPlatform'),

    snapContainerIconPath: function () {
      return AmComputerFormatter.getIconPathOsPlatform('32', this.get('model.data.osPlatformNumber'));
    }.property('model.data.osPlatformNumber'),

    //Additional information on the page
    deviceIconPath: function () {
      return AmComputerFormatter.getIconPathOsPlatform('64', this.get('model.data.osPlatformNumber'));
    }.property('model.data.osPlatformNumber'),

    osIconPath: function () {
      return AmComputerFormatter.getIconPathOsPlatform('16', this.get('model.data.osPlatformNumber'));
    }.property('model.data.osPlatformNumber'),

    // Warning panel
    hasWarning: function () {
      return this.get('warningVisible');
    }.property('model.data.computerDeviceFreezeStatusNumber'),

    warningView: AmDesktop.AmNavTabPageView.WarningView.extend({
      defaultTemplate: Em.Handlebars.compile(warningItemTemplate)
    }),

    deviceFreezeStatusClass: function () {
      return AmComputerFormatter.getIconClassFreezeStatus(this.get('model.data.computerDeviceFreezeStatusNumber'));
    }.property('model.data.computerDeviceFreezeStatusNumber'),

    // TODO Next release probably, do not localize
    computerDeviceFreezeStatusMessage: function () {
      var computerDeviceFreezeStatus = this.get('model.data.computerDeviceFreezeStatusNumber');

      var message;
      switch (computerDeviceFreezeStatus) {
        // 'None (Idle)'
        case 0:
          message = 'None (Idle)';
          break;
        // 'Freeze Requested'
        case 1:
          message = 'Device Freeze requested, awaiting connection';
          break;
        // 'Frozen Successfully'
        case 2:
          message = 'This device is Frozen';
          break;
        // 'Unfreeze Requested'
        case 3:
          message = 'This device is Frozen. An unfreeze command has been requested';
          break;
        // 'Unfrozen by User'
        case 4:
          message = 'Unfrozen by User';
          break;
        // 'Unfrozen by Admin'
        case 5:
          message = 'Unfrozen by Admin';
          break;
        // 'Freeze Error'
        case 6:
          message = 'Freeze Error';
          break;
        // 'Unfreeze Error'
        case 7:
          message = 'Unfreeze Error';
          break;
      }
      return message;
    }.property('model.data.computerDeviceFreezeStatus'),

    warningVisible: function () {
      var computerDeviceFreezeStatus = this.get('model.data.computerDeviceFreezeStatusNumber');
      return computerDeviceFreezeStatus && $.inArray(computerDeviceFreezeStatus, [0, 4, 5]) === -1;
    }.property('model.data.computerDeviceFreezeStatusNumber'),

    // Helpers
    isMacPlatform: function () {
      var osPlatform = this.get('model.data.osPlatformNumber');
      // Save it to use in some other tabs (Volumes, ...)
      this.set('osPlatform', osPlatform);
      return AmComputerFormatter.isMacPlatform(osPlatform);
    }.property('model.data.osPlatformNumber'),

    isComputerEsnEmpty: function () {
      return this.get('model.data.computerEsn') === null || Em.isEmpty(this.get('model.data.computerEsn'));
    }.property('model.data.computerEsn'),

    isFreezeCommandSupported: function () {
      var isSupported = false;

      // TODO revisit later, next release
      /*
       if(this.get('isMacPlatform')  !== true) {
       isSupported = true;
       }

       var freezeStatus = this.get('data.deviceFreezeStatusNumber');
       return $.inArray(freezeStatus,[0,4,5,6]) !== -1;
       */

      return isSupported;
    }.property(),

    isUnfreezeCommandSupported: function () {
      var isSupported = false;

      // TODO revisit later, next release
      /*
       if(this.get('isMacPlatform')  !== true) {
       isSupported = true;
       }

       var freezeStatus = this.get('data.deviceFreezeStatusNumber');
       return $.inArray(freezeStatus,[0,4,5,6]) !== -1;
       */
      return isSupported;
    }.property(),

    isDataDeleteCommandSupported: function () {
      // TODO revisit later, next release
      return false;//this.get('isMacPlatform') !== true;
    }.property()
  });
});
