define([
  'ember',
  'ui',
  'desktop',
  'am-desktop',
  'guid',
  'query',

  './lib/namespace',
  'packages/am/am-app-foundation',
  'packages/platform/nav-page-view',
  'packages/am/am-user-formatter',

  './lib/controllers/user_self_help_item_controller',
  './lib/controllers/user_self_help_list_controller',

  './lib/controllers/user_self_help_item_lock_controller',
  './lib/controllers/user_self_help_item_remote_erase_controller',
  './lib/controllers/user_self_help_item_reset_tracking_passcode_controller',
  './lib/controllers/user_self_help_item_track_device_controller',
  './lib/controllers/user_self_help_item_send_message_controller',
  './lib/controllers/user_self_help_item_clear_passcode_controller',

  './lib/views/user_self_help_device_details_view',

  'text!./lib/templates/user_self_help_list_no_data_to_display.handlebars',
  'text!./lib/templates/user_self_help_menu_item.handlebars',

  'am-data',
  'i18n!./nls/strings'
], function (
  Em,
  UI,
  Desktop,
  AmDesktop,
  Guid,
  Query,

  AmUserSelfHelpDevice,
  AmAppFoundation,
  NavPageView,
  AmUserFormatter,
  
  AmUserSelfHelpItemController,
  AmUserSelfHelpListController,

  AmUserSelfHelpDeviceItemLockController,
  AmUserSelfHelpDeviceItemRemoteEraseController,
  AmUserSelfHelpDeviceItemResetPasscodeController,
  AmUserSelfHelpDeviceItemTrackController,
  AmUserSelfHelpDeviceItemSendMessageController,
  AmUserSelfHelpDeviceItemClearPasscodeController,

  AmUserSelfHelpDeviceDetailsView,

  AmUserSelfHelpNoDataToDisplayTemplate,
  MenuItemTemplate,

  AmData,
  strings
  ) {
  'use strict';

  var mobileDeviceActions = {
    lockDevice: function () {
      this.showModal({ name: 'am_user_self_help_device_item_lock', model: this.get('controller.content') });
    },

    remoteErase: function () {
      this.showModal({ name: 'am_user_self_help_device_item_remote_erase', model: this.get('controller.content') });
    },

    // TODO Do not supported in this release
    resetTrackingPasscode: function () {
      this.showModal({ name: 'am_user_self_help_device_item_reset_passcode', model: this.get('controller.content') });
    },

    clearOrSetPasscode: function () {
      this.showModal({ name: 'am_user_self_help_device_item_clear_passcode', model: this.get('controller.content') });
    },

    // TODO Do not supported in this release
    trackDevice: function (selectionsList) {
      this.showModal({ name: 'am_user_self_help_device_item_track_device', model: Em.isNone(selectionsList) ? Em.makeArray(this.get('controller.content')) : selectionsList });
    },

    sendMessage: function () {
      this.showModal({ name: 'am_user_self_help_device_item_send_message', model: this.get('controller.content') });
    }

  };

  var appClasses = {
    AmUserSelfMenuItemComponent: NavPageView.NavItemView.extend({
      layout: Em.Handlebars.compile(MenuItemTemplate),

      osClass: null,

      listOsClass: null,

      iconPath: null,

      deviceName: function() {
        var valueArray = this.get('nodeData.data.name').toString().split('[****]');
        var deviceName = valueArray[0],  osPlatform = parseInt(valueArray[1]), isTablet = parseInt(valueArray[2]), model = valueArray[3];

        this.setProperties({
          iconPath: AmUserFormatter.getOsSmallIconPath(osPlatform),
          osClass: AmUserFormatter.getOsClass(osPlatform, isTablet, model),
          listOsClass: AmUserFormatter.getListOsClass(osPlatform, isTablet, model)
        });

        return deviceName;
      }.property('nodeData.data.name')
    }),

    // Shared Package Controllers
    // --------------------------

    AmUserSelfHelpNavSizeController: NavPageView.NavSizeController.extend({
      storageKey: 'am.UserSelfHelp.NavViewSplitterSettings'
    }),

    AmUserSelfHelpDeviceListRoute: UI.NoSetupRoute.extend({
      actions: {
        gotoNavItem: function (id) {
          this.transitionTo('am_user_self_help_device.item', id);
        }
      },

      setupController: function(controller, params) {
        var self = this;

        var deviceId = params ? params.deviceId :  null;

        // Get the first device id or from parameter
        if(!deviceId || deviceId === 'undefined') {
          var query = Query.Search.create({
          });

          AmUserSelfHelpDevice.get('store').acquire(Guid.generate(), query, function(data) {
              data.get('content').some(function(item) {
                deviceId = item.get('data.identifier');
                return true;
              });

              // Get the first available id
              if(deviceId && deviceId !== 'undefined') {
                var listController = self.controllerFor('am_user_self_help_device');
                listController.set('selectedItemId', deviceId);
                self.transitionTo('am_user_self_help_device.item', deviceId);
              }

            },
            null, null, true);
        } else {
          // If device id is in the parameter
          var listController = self.controllerFor('am_user_self_help_device');
          listController.set('selectedItemId', deviceId);
        }
      },

      renderTemplate: function() {
        this.render('am_user_self_help_device_list');
        this.render('am_user_self_help_device_list_content', { into: 'am_user_self_help_device_list' });
      }
    }),
    AmUserSelfHelpDeviceListController: AmUserSelfHelpListController,
    AmUserSelfHelpDeviceListView: NavPageView,
    AmUserSelfHelpDeviceListContentView: Em.View.extend({ defaultTemplate: Em.Handlebars.compile(AmUserSelfHelpNoDataToDisplayTemplate) }),

    // Device Details Page
    AmUserSelfHelpDeviceRoute: UI.NoSetupRoute.extend({
      actions: {
        gotoNavItem: function (id) {
          this.transitionTo('am_user_self_help_device.item', id);
        }
      },

      setupController: function(controller, params) {
        var self = this;

        var deviceId = params ? params.deviceId :  null;

        if(deviceId && deviceId !== 'undefined') {
          var listController = self.controllerFor('am_user_self_help_device');
          listController.set('selectedItemId', deviceId);
        }
      }
    }),
    AmUserSelfHelpDeviceController: AmUserSelfHelpListController,
    AmUserSelfHelpDeviceView: NavPageView,

    // Device Details Page - Item
    AmUserSelfHelpDeviceItemRoute: UI.NoSetupRoute.extend(UI.Route.HasBreadcrumbs,  {
      actions: mobileDeviceActions,

      setupController: function(controller) {
        var deviceId = this.controllerFor('am_user_self_help_device').get('selectedItemId');
        if(deviceId && deviceId !== 'undefined') {
          controller.set('displayControls', false);
          controller.loadOneDevice(deviceId,
          function() {
            controller.set('displayControls', true);
          })
        }
      }
    }),
    AmUserSelfHelpDeviceItemController: AmUserSelfHelpItemController,
    AmUserSelfHelpDeviceItemView: AmUserSelfHelpDeviceDetailsView,

    //
    // Commands
    //

    // Lock Device
    AmUserSelfHelpDeviceItemLockController: AmUserSelfHelpDeviceItemLockController,
    AmUserSelfHelpDeviceItemLockView: Desktop.ModalActionView,

    // Remote Erase
    AmUserSelfHelpDeviceItemRemoteEraseController: AmUserSelfHelpDeviceItemRemoteEraseController,
    AmUserSelfHelpDeviceItemRemoteEraseView: Desktop.ModalActionView,

    // Reset Tracking Passphrase
    AmUserSelfHelpDeviceItemResetPasscodeController: AmUserSelfHelpDeviceItemResetPasscodeController,
    AmUserSelfHelpDeviceItemResetPasscodeView: Desktop.ModalActionView,

    // Track Device
    AmUserSelfHelpDeviceItemTrackDeviceController: AmUserSelfHelpDeviceItemTrackController,
    AmUserSelfHelpDeviceItemTrackDeviceView: Desktop.ModalActionView,

    // Send Message
    AmUserSelfHelpDeviceItemSendMessageController: AmUserSelfHelpDeviceItemSendMessageController,
    AmUserSelfHelpDeviceItemSendMessageView: Desktop.ModalActionView,

    // Clear or Set Passphrase for Android or iOS only
    AmUserSelfHelpDeviceItemClearPasscodeController: AmUserSelfHelpDeviceItemClearPasscodeController,
    AmUserSelfHelpDeviceItemClearPasscodeView: Desktop.ModalActionView

  };

  return {
    buildRoutes: function(router) {
      router.route('am_user_self_help_device_list', { path: '/'  });
      router.resource('am_user_self_help_device', { path: '/:deviceId' }, function () {
        this.route('item');
      });
    },

    initialize: function() {
      AmUserSelfHelpDevice.reopen({
        AmData: AmData,

        store: function () {
          return this.get('AmData.stores.userSelfHelpDeviceListStore');
        }.property(),

        spec: function () {
          return this.get('AmData.specs.UserSelfHelpDeviceListSpec');
        }.property()
      });
    },

    appClasses: appClasses,
    appStrings: strings
  };
});
