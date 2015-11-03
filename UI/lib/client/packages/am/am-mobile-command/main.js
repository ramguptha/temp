define([
  'ember',
  'desktop',
  'am-desktop',
  'packages/platform/ui',
  'am-data',
  'packages/am/am-mobile-device',
  'packages/am/am-app-foundation',
  'packages/platform/nav-page-view',

  './lib/namespace',
  './lib/controllers/command_group_list_controller',

  './lib/controllers/command_mobile_history_list_controller',
  './lib/controllers/command_mobile_queue_list_controller',
  './lib/controllers/command_mobile_delete_command_from_history_controller',
  './lib/controllers/command_mobile_delete_command_from_queue_controller',

  './lib/controllers/command_computer_history_list_controller',
  './lib/controllers/command_computer_queue_list_controller',
  './lib/controllers/command_computer_details_controller',
  './lib/controllers/command_computer_delete_command_from_history_controller',
  './lib/controllers/command_computer_delete_command_from_queue_controller',

  'i18n!./nls/strings',
  'locale'
], function (
  Em,
  Desktop,
  AmDesktop,
  UI,
  AmData,
  AmMobileDevice,
  AmAppFoundation,
  NavPageView,

  AmMobileCommand,
  AmCommandGroupListController,

  AmMobileCommandHistoryListController,
  AmMobileCommandQueueListController,
  AmMobileCommandDeleteCommandFromHistoryController,
  AmMobileCommandDeleteCommandFromQueueController,

  AmComputerCommandHistoryListController,
  AmComputerCommandQueueListController,
  AmComputerCommandDetailsController,
  AmComputerCommandDeleteCommandFromHistoryController,
  AmComputerCommandDeleteCommandFromQueueController,

  strings,
  Locale
) {
  'use strict';

  var MobileCommandRoute = UI.NoSetupRoute.extend(UI.Route.HasBreadcrumbs, {
    actions: {
      gotoListItem: function(id) {
        // id is a pair of commandRecordId and deviceId, use deviceId
        this.send('gotoAmMobileDevice', id.split(':')[1]);
      },

      selectColumns: function (controller) {
        var controller = !Em.isNone(controller) ? controller : this.get('controller');
        this.showModal({ name: 'am_mobile_command_select_columns', model: { listController: controller } });
      },

      deleteCommandHistory: function (selectionsList) {
        var ids = Em.A([]);
        for (var i = 0; i < selectionsList.length; i++) {
          // id is a pair of commandRecordId and deviceId, use commandRecordId
          ids.pushObject(selectionsList[i].split(':')[0]);
        }
        this.showModal({ name: 'am_mobile_command_delete_command_from_history', model: { commandIds: ids } });
      },

      deleteCommandQueue: function (selectionsList) {
        var ids = Em.A([]);
        for (var i = 0; i < selectionsList.length; i++) {
          // id is a pair of commandRecordId and deviceId, use commandRecordId
          ids.pushObject(selectionsList[i].split(':')[0]);
        }
        this.showModal({ name: 'am_mobile_command_delete_command_from_queue', model: { commandIds: ids } });
      }

    }
  });

  var ComputerCommandRoute = UI.Route.extend(UI.Route.HasBreadcrumbs, {
    actions: {
      gotoListItem: function(id) {
        // id is a pair of commandRecordId and computerId, use computerId
        this.send('gotoAmComputerDevice', id.split(':')[1]);
      },

      selectColumns: function(controller) {
        var controller = !Em.isNone(controller) ? controller : this.get('controller');
        this.showModal({ name: 'am_mobile_command_select_columns', model: { listController: controller } });
      },

      deleteCommandHistory: function(selectionsList) {
        var ids = Em.A([]);
        for (var i = 0; i < selectionsList.length; i++) {
          // id is a pair of commandRecordId and computerId, use commandRecordId
          ids.pushObject(selectionsList[i].split(':')[0]);
        }
        this.showModal({ name: 'am_computer_command_delete_command_from_history', model: { commandIds: ids } });
      },

      deleteCommandQueue: function(selectionsList) {
        var ids = Em.A([]);
        for (var i = 0; i < selectionsList.length; i++) {
          // id is a pair of commandRecordId and computerId, use commandRecordId
          ids.pushObject(selectionsList[i].split(':')[0]);
        }
        this.showModal({ name: 'am_computer_command_delete_command_from_queue', model: { commandIds: ids } });
      }

    }
  });


  var appClasses = {

    // Shared Package Controllers
    AmCommandNavSizeController: NavPageView.NavSizeController.extend({
      storageKey: 'am.Command.NavViewSplitterSettings',
      defaultSettings: {
        width: 200
      }
    }),

    // Command Queue Type List
    AmCommandQueueRoute: UI.NoSetupRoute.extend({
      actions: {
        gotoNavItem: function(id) {
          this.transitionTo('am_command_queue.' + id);
        }
      }
    }),
    AmCommandQueueController: AmCommandGroupListController,
    AmCommandQueueView: NavPageView,

    // Command History Type List
    AmCommandHistoryRoute: UI.NoSetupRoute.extend({
      actions: {
        gotoNavItem: function(id) {
          this.transitionTo('am_command_history.' + id);
        }
      }
    }),
    AmCommandHistoryController: AmCommandGroupListController,
    AmCommandHistoryView: NavPageView,

    // Select Columns
    AmMobileCommandSelectColumnsController: AmAppFoundation.SelectColumnsController,
    AmMobileCommandSelectColumnsView: Desktop.ModalColumnChooserView,

    //
    // -----Mobile--------
    //
    // Mobile Command Queue
    AmCommandQueueMobileDevicesRoute: MobileCommandRoute.extend({
      setupController: function(controller) {
        this.activateBreadcrumbs(controller);
        this.controllerFor('am_command_queue').set('navSelectedItemId', 'mobile_devices');
      }
    }),
    AmCommandQueueMobileDevicesController: AmMobileCommandQueueListController,
    AmCommandQueueMobileDevicesView: AmDesktop.AmListView,

    // Mobile Command History
    AmCommandHistoryMobileDevicesRoute: MobileCommandRoute.extend({
      setupController: function(controller) {
        this.activateBreadcrumbs(controller);
        this.controllerFor('am_command_history').set('navSelectedItemId', 'mobile_devices');
      }
    }),
    AmCommandHistoryMobileDevicesController: AmMobileCommandHistoryListController,
    AmCommandHistoryMobileDevicesView: AmDesktop.AmListView,

    // Delete Command from History
    AmMobileCommandDeleteCommandFromHistoryController: AmMobileCommandDeleteCommandFromHistoryController,
    AmMobileCommandDeleteCommandFromHistoryView: Desktop.ModalActionView,

    // Delete Command from Queue
    AmMobileCommandDeleteCommandFromQueueController: AmMobileCommandDeleteCommandFromQueueController,
    AmMobileCommandDeleteCommandFromQueueView: Desktop.ModalActionView,

    //
    // -----Computer--------
    //
    // Computer Command Queue
    AmCommandQueueComputersRoute: ComputerCommandRoute.extend({
      actions: {
        commandDetailsAction: function(commandInfo) {
          var self = this;
          var controller = this.get('controller');

          controller.loadComputer(commandInfo, function(data, commandId) {
            self.showModal({ name: 'am_command_details',
              model: {
                model: data.get('content')[0],
                commandId: commandId
              }
            })
          });
        }
      },

      setupController: function(controller) {
        this.activateBreadcrumbs(controller);
        this.controllerFor('am_command_queue').set('navSelectedItemId', 'computers');
      }
    }),
    AmCommandQueueComputersController: AmComputerCommandQueueListController,
    AmCommandQueueComputersView: AmDesktop.AmListView,

    // Computer Command  History
    AmCommandHistoryComputersRoute: ComputerCommandRoute.extend({
      actions: {
        commandDetailsAction: function(commandInfo) {
          var self = this;
          var controller = this.get('controller');

          controller.loadComputer(commandInfo, function(data, commandId) {
            self.showModal({ name: 'am_command_details',
              model: {
                model: data.get('content')[0],
                commandId: commandId
              }
            })
          });
        }
      },

      setupController: function(controller) {
        this.activateBreadcrumbs(controller);
        this.controllerFor('am_command_history').set('navSelectedItemId', 'computers');
      }
    }),
    AmCommandHistoryComputersController: AmComputerCommandHistoryListController,
    AmCommandHistoryComputersView: AmDesktop.AmListView,

    // Delete Command from History
    AmComputerCommandDeleteCommandFromHistoryController: AmComputerCommandDeleteCommandFromHistoryController,
    AmComputerCommandDeleteCommandFromHistoryView: Desktop.ModalActionView,

    // Delete Command from Queue
    AmComputerCommandDeleteCommandFromQueueController: AmComputerCommandDeleteCommandFromQueueController,
    AmComputerCommandDeleteCommandFromQueueView: Desktop.ModalActionView,

    // Command Details
    AmCommandDetailsController: AmComputerCommandDetailsController,
    AmCommandDetailsView: Desktop.ModalActionView

  };

  return {
    buildRoutes: function(router) {
      router.resource('am_command_queue', { path: 'am_command_queue' }, function() {
        this.route('computers', { path: '/computers' });
        this.route('mobile_devices', { path: '/mobile_devices' });
      });

      router.resource('am_command_history', { path: 'am_command_history' }, function() {
        this.route('computers', { path: '/computers' });
        this.route('mobile_devices', { path: '/mobile_devices' });
      });
    },

    initialize: function() {
      AmMobileCommand.reopen({
        commandMobileHistorySpec: function() {
          return AmData.get('specs.AmCommandHistorySpec');
        }.property(),

        commandMobileHistoryStore: function() {
          return AmData.get('stores.commandMobileHistoryStore');
        }.property(),

        commandMobileQueueSpec: function() {
          return AmData.get('specs.AmCommandQueueSpec');
        }.property(),

        commandMobileQueueStore: function() {
          return AmData.get('stores.commandMobileQueueStore');
        }.property(),

        commandComputerHistorySpec: function() {
          return AmData.get('specs.AmCommandComputerHistorySpec');
        }.property(),

        commandComputerHistoryStore: function() {
          return AmData.get('stores.commandComputerHistoryStore');
        }.property(),

        commandComputerQueueSpec: function() {
          return AmData.get('specs.AmCommandComputerQueueSpec');
        }.property(),

        commandComputerQueueStore: function() {
          return AmData.get('stores.commandComputerQueueStore');
        }.property(),

        userPrefsStore: function() {
          return AmData.get('stores.userPrefsStore');
        }.property(),

        userPrefsSpec: function() {
          return AmData.get('specs.UserPrefsSpec');
        }.property(),

        commandGroupStore: function() {
          return AmData.get('stores.commandGroupStore');
        },

        commandGroupSpec: function() {
          return AmData.get('specs.AmCommandGroupSpec');
        }

      });
    },

    appClasses: appClasses,
    appStrings: strings,

    topNavSpec: {
      name: function () {
        return Locale.renderGlobals('amMobileCommand.topNavSpec.commandsTitle').toString();
      }.property(),

      noLandingAction: true,
      iconClassName: 'icon-commands',

      routes: [
        {
          name: 'amMobileCommand.topNavSpec.queuedCommandsTitle'.tr(),
          path: 'am_command_queue.' + AmData.get('specs.AmCommandGroupSpec.DEFAULT_ID'),
          breadcrumbButtonClassName: 'is-button-for-breadcrumb-am-mobile-command-queued'
        },
        {
          name: 'amMobileCommand.topNavSpec.commandHistoryTitle'.tr(),
          path: 'am_command_history.' + AmData.get('specs.AmCommandGroupSpec.DEFAULT_ID'),
          breadcrumbButtonClassName: 'is-button-for-breadcrumb-am-mobile-command-history'
        }
      ]
    }
  };
});
