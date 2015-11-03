define([
  'ember',
  'ui',
  'desktop',
  'am-desktop',
  'query',
  'am-data',
  'packages/am/am-app-foundation',
  'packages/am/am-assignable-item-foundation',

  './lib/namespace',

  './lib/controllers/content_item_controller',
  './lib/controllers/action_item_controller',
  './lib/controllers/assignable_content_list_controller',
  './lib/controllers/assignable_item_menu_controller',
  './lib/controllers/assignable_in_house_apps_list_controller',
  './lib/controllers/assignable_third_party_apps_list_controller',
  './lib/controllers/assignable_books_list_controller',
  './lib/controllers/assignable_config_profiles_list_controller',
  './lib/controllers/assignable_provisioning_profiles_list_controller',
  './lib/controllers/assignable_actions_list_controller',

  './lib/controllers/content_item_create_controller',
  './lib/controllers/content_item_edit_properties_controller',
  './lib/controllers/content_item_delete_controller',

  './lib/controllers/action_properties_send_message_controller',
  './lib/controllers/action_properties_send_email_controller',
  './lib/controllers/action_properties_send_sms_controller',

  './lib/controllers/action_properties_set_roaming_controller',
  './lib/controllers/action_properties_set_activation_lock_controller',
  './lib/controllers/action_properties_set_wallpaper_controller',
  './lib/controllers/action_properties_set_device_name_controller',
  './lib/controllers/action_properties_set_custom_field_controller',
  './lib/controllers/action_properties_update_device_info_controller',

  './lib/controllers/action_properties_attention_mode_controller',
  './lib/controllers/action_properties_freeze_device_controller',

  './lib/controllers/action_properties_send_vpp_invitation_controller',
  './lib/controllers/action_properties_register_user_controller',
  './lib/controllers/action_properties_retire_user_controller',

  './lib/controllers/action_properties_remove_configuration_controller',
  './lib/controllers/action_properties_demote_controller',

  './lib/controllers/action_item_delete_controller',

  './lib/views/content_details_view',
  './lib/views/action_details_view',
  'packages/platform/nav-page-view',

  './lib/views/action_item_base_modal_view',

  'text!./lib/templates/content_item_create_flow.handlebars',
  'text!./lib/templates/action_item_work_flow.handlebars',

  'i18n!./nls/strings',
  'locale'
], function (
  Em,
  UI,
  Desktop,
  AmDesktop,
  Query,
  AmData,
  AmAppFoundation,
  AmAssignableItemFoundation,

  AmAssignableItem,

  AmContentItemController,
  AmActionItemController,
  AmAssignableContentListController,
  AmAssignableItemMenuController,
  AmAssignableInHouseAppsController,
  AmAssignableThirdPartyAppsController,
  AmAssignableBooksController,
  AmAssignableConfigProfilesController,
  AmAssignableProvisioningProfilesController,
  AmAssignableActionsController,

  AmContentItemCreateController,
  AmContentEditPropertiesController,
  AmContentDeleteController,

  ActionSendMessageController,
  ActionSendEmailController,
  ActionSendSmsController,

  ActionSetRoamingController,
  ActionActivationLockController,
  ActionSetWallpaperController,
  ActionSetDeviceNameController,
  ActionSetCustomFieldController,
  ActionUpdateDeviceController,

  ActionAttentionModeController,
  ActionFreezeDeviceController,

  ActionSendVppInvitationController,
  ActionRegisterUserController,
  ActionRetireUserController,

  ActionRemoveConfigurationController,
  ActionDemoteController,

  ActionDeleteController,

  AmContentItemDetailsView,
  AmActionItemDetailsView,
  NavPageView,

  ActionBaseModalView,

  AmContentItemCreateFlow,
  ActionItemWorkFlow,

  strings,
  Locale
) {
  'use strict';

  var BaseItemTabRoute = UI.Route.extend({
    controllerName: null,
    tabName: null,

    setupController: function(controller) {
      controller.set('activeTab', this.get('tabName'));
    }
  });

  var ContentItemTabRoute = BaseItemTabRoute.extend({
    controllerName: 'am_assignable_content_item'
  });

  var ActionItemTabRoute = BaseItemTabRoute.extend({
    controllerName: 'am_assignable_actions_item'
  });

  var HasSelectColumns = Em.Mixin.create({
    actions: {
      selectColumns: function() {
        var settings = { listController: this.get('controller') };
        this.showModal({ name: 'am_assignable_select_columns', model: settings });
      }
    }
  });

  // Assignable List Base Route

  var AssignableListRoute = UI.NoSetupRoute.extend(UI.Route.HasBreadcrumbs, HasSelectColumns, {
    menuId: null,

    actions: {
      gotoListItem: function(id) {
        this.transitionTo('am_assignable_' + this.get('menuId') + '_item.details', id);
      }
    },

    setupController: function(controller) {
      this.activateBreadcrumbs(controller);
      controller.clearSelections();

      this.controllerFor('am_assignable_list').set('selectedId', this.get('menuId'));
    }
  });

  var assignableActionsList = {
    newSendMessageAction: function (router, context) {
      router.showModal({ name: 'am_assignable_action_send_message', model: context });
    },

    newSendEmailAction: function (router, context) {
      router.showModal({ name: 'am_assignable_action_send_email', model: context });
    },

    newSendSmsAction: function (router, context) {
      router.showModal({ name: 'am_assignable_action_send_sms', model: context });
    },

    newSetRoamingAction: function (router, context) {
      router.showModal({ name: 'am_assignable_action_set_roaming', model: context });
    },

    newSetActivationAction: function (router, context) {
      router.showModal({ name: 'am_assignable_action_setActivation', model: context });
    },

    newSetWallpaperAction: function (router, context) {
      router.showModal({ name: 'am_assignable_action_set_wallpaper', model: context });
    },

    newSetDeviceNameAction: function (router, context) {
      router.showModal({ name: 'am_assignable_action_set_device_name', model: context });
    },

    newSetCustomFieldAction: function (router, context) {
      router.showModal({ name: 'am_assignable_action_set_custom_field', model: context });
    },

    newUpdateDeviceInfoAction: function (router, context) {
      router.showModal({ name: 'am_assignable_action_update_device_info', model: context });
    },

    newAttentionModeAction: function (router, context) {
      router.showModal({ name: 'am_assignable_action_attention_mode', model: context });
    },

    newFreezeDeviceAction: function (router, context) {
      router.showModal({ name: 'am_assignable_action_freeze_device', model: context });
    },

    newSendVppInvitationAction: function (router, context) {
      router.showModal({ name: 'am_assignable_action_send_vpp_invitation', model: context });
    },

    newRegisterUserAction: function (router, context) {
      router.showModal({ name: 'am_assignable_action_register_user', model: context });
    },

    newRetireUserAction: function (router, context) {
      router.showModal({ name: 'am_assignable_action_retire_user', model: context });
    },

    newRemoveConfigurationAction: function (router, context) {
      router.showModal({ name: 'am_assignable_action_remove_configuration', model: context });
    },

    newDemoteAction: function (router, context) {
      router.showModal({ name: 'am_assignable_action_demote', model: context });
    }
  };

  var actionModalView = Em.View.extend({
    defaultTemplate: Em.Handlebars.compile(ActionItemWorkFlow)
  });

  var getNewActionIdAndShowAddPolicyModal = function(self, actionId, actionName) {
    // If policy assignment is in process right after creation of a new action,
    // get the id by providing the action's name.
    var actionQuery = {searchFilter: actionName};

    AmData.get('stores.actionsStore').acquire(null, actionQuery, function (dataSource) {
      var selectedContext = dataSource.get('content').filter(function (item) {
        return item.get('name').toString() === actionName;
      });

      actionId = selectedContext[0].get('id');

      setAndShowAddPolicyModal(self, actionId, actionName);
    });
  };

  // After the proper actionId and actionName are retrieved and set, show the modal
  // and set the already assigned policies on it
  var setAndShowAddPolicyModal = function(self, actionId, actionName) {
    var policyQuery = Query.Search.create({
      context: { actionId: actionId }
    });

    AmData.get('stores.mobilePolicyFromActionStore').acquire(null, policyQuery, function(assignedPolicies) {
      self.showModal({ name: 'am_assignable_add_action_to_policies',
        model: {
          actionId: actionId,
          actionName: actionName,

          isSelectingPolicy: true,
          assignedPolicies: assignedPolicies.get('content')
        }
      })
    }, null, this);
  };

  var appClasses = {

    AmActionBaseModalView: ActionBaseModalView,

    // Shared Package Controllers
    // --------------------------

    AmAssignableNavSizeController: NavPageView.NavSizeController.extend({
      storageKey: 'am.AssignableItems.NavViewSplitterSettings',
      defaultSettings: {
        width: 200,
        isCollapsed: false
      }
    }),

    AmAssignableSelectColumnsController: AmAppFoundation.SelectColumnsController,
    AmAssignableSelectColumnsView: Desktop.ModalColumnChooserView,

    // Assignable Item List
    // --------------------

    // Categories Resource

    AmAssignableListRoute: UI.NoSetupRoute.extend({
      actions: {
        gotoNavItem: function(id) {
          var self = this;

          AmAssignableItem.get('assignableItemMenuStore').acquireOne(null, id, function(item) {
            self.transitionTo(item.objectAt(0).get('data.route'));
          });
        },

        newContent: function () {
          this.showModal({ name: 'am_assignable_new_content' });
        },

        editContentProperties: function (selectionsList) {
          var id = selectionsList[0];
          this.showModal({ name: 'am_assignable_edit_content_properties', model: { contentId: id } });
        },

        deleteContent: function (selectionsList) {
          var ids = Em.A([]);
          for (var i = 0; i < selectionsList.length; i++) {
            ids.pushObject(selectionsList[i]);
          }
          this.showModal({ name: 'am_assignable_delete_content', model: { contentIds: ids } });
        },

        newSendMessageAction: function (context) {
          assignableActionsList.newSendMessageAction(this, context);
        },

        newSendEmailAction: function (context) {
          assignableActionsList.newSendEmailAction(this, context);
        },

        newSendSmsAction: function (context) {
          assignableActionsList.newSendSmsAction(this, context);
        },

        newSetRoamingAction: function (context) {
          assignableActionsList.newSetRoamingAction(this, context);
        },

        newSetActivationAction: function (context) {
          assignableActionsList.newSetActivationAction(this, context);
        },

        newSetWallpaperAction: function (context) {
          assignableActionsList.newSetWallpaperAction(this, context);
        },

        newSetDeviceNameAction: function (context) {
          assignableActionsList.newSetDeviceNameAction(this, context);
        },

        newSetCustomFieldAction: function (context) {
          assignableActionsList.newSetCustomFieldAction(this, context);
        },

        newUpdateDeviceInfoAction: function (context) {
          assignableActionsList.newUpdateDeviceInfoAction(this, context);
        },

        newAttentionModeAction: function (context) {
          assignableActionsList.newAttentionModeAction(this, context);
        },

        newFreezeDeviceAction: function (context) {
          assignableActionsList.newFreezeDeviceAction(this, context);
        },

        newSendVppInvitationAction: function (context) {
          assignableActionsList.newSendVppInvitationAction(this, context);
        },

        newRegisterUserAction: function (context) {
          assignableActionsList.newRegisterUserAction(this, context);
        },

        newRetireUserAction: function (context) {
          assignableActionsList.newRetireUserAction(this, context);
        },

        newRemoveConfigurationAction: function (context) {
          assignableActionsList.newRemoveConfigurationAction(this, context);
        },

        newDemoteAction: function (context) {
          assignableActionsList.newDemoteAction(this, context);
        },

        deleteAction: function (selectionsList) {
          var ids = Em.A([]);
          for (var i = 0; i < selectionsList.length; i++) {
            ids.pushObject(selectionsList[i]);
          }

          this.showModal({ name: 'am_assignable_delete_action', model: { actionIds: ids } });
        },

        gotoAddActionToPolicies: function(context) {
          var actionId = context.actionId,
            actionName = context.actionName;

          if (!actionId) {
            getNewActionIdAndShowAddPolicyModal(this, actionId, actionName);
          } else {
            setAndShowAddPolicyModal(this, actionId, actionName);
          }
        }
      }
    }),

    AmAssignableListController: AmAssignableItemMenuController,
    AmAssignableListView: NavPageView,

    // Assignable List Content

    AmAssignableListContentRoute: AssignableListRoute.extend({
      menuId: 'content'
    }),
    AmAssignableListContentController: AmAssignableContentListController,
    AmAssignableListContentView: AmDesktop.AmListView,

    // Assignable List In-House Applications

    AmAssignableListInHouseAppsRoute: AssignableListRoute.extend({
      menuId: 'inHouseApps'
    }),
    AmAssignableListInHouseAppsController: AmAssignableInHouseAppsController,
    AmAssignableListInHouseAppsView: AmDesktop.AmListView,

    // Assignable List Third Party Applications

    AmAssignableListThirdPartyAppsRoute: AssignableListRoute.extend({
      menuId: 'thirdPartyApps'
    }),
    AmAssignableListThirdPartyAppsController: AmAssignableThirdPartyAppsController,
    AmAssignableListThirdPartyAppsView: AmDesktop.AmListView,

    // Assignable List Books

    AmAssignableListBooksRoute: AssignableListRoute.extend({
      menuId: 'books'
    }),
    AmAssignableListBooksController: AmAssignableBooksController,
    AmAssignableListBooksView: AmDesktop.AmListView,

    // Assignable List Configuration Profiles

    AmAssignableListConfigProfilesRoute: AssignableListRoute.extend({
      menuId: 'configProfiles'
    }),
    AmAssignableListConfigProfilesController: AmAssignableConfigProfilesController,
    AmAssignableListConfigProfilesView: AmDesktop.AmListView,

    // Assignable List Provisioning Profiles

    AmAssignableListProvisioningProfilesRoute: AssignableListRoute.extend({
      menuId: 'provisioningProfiles'
    }),
    AmAssignableListProvisioningProfilesController: AmAssignableProvisioningProfilesController,
    AmAssignableListProvisioningProfilesView: AmDesktop.AmListView,

    // Assignable List Actions

    AmAssignableListActionsRoute: AssignableListRoute.extend({
      menuId: 'actions'
    }),
    AmAssignableListActionsController: AmAssignableActionsController,
    AmAssignableListActionsView: AmDesktop.AmListView.extend({
      classNames: ['assignable-actions-page']
    }),

    // New Content Wizard

    AmAssignableNewContentView: Em.View.extend({
      defaultTemplate: Em.Handlebars.compile(AmContentItemCreateFlow)
    }),

    // Edit Content

    AmAssignableEditContentPropertiesController: AmContentEditPropertiesController,
    AmAssignableEditContentPropertiesView: AmAssignableItemFoundation.AmEditFlowView,

    // Delete Content

    AmAssignableDeleteContentController: AmContentDeleteController,
    AmAssignableDeleteContentView: Desktop.ModalActionView,

    // Delete Action

    AmAssignableDeleteActionController: ActionDeleteController,
    AmAssignableDeleteActionView: Desktop.ModalActionView,

    // Different Action types

    AmAssignableActionSendMessageController: ActionSendMessageController,
    AmAssignableActionSendMessageView: actionModalView,

    AmAssignableActionSendEmailController: ActionSendEmailController,
    AmAssignableActionSendEmailView: actionModalView,

    AmAssignableActionSendSmsController: ActionSendSmsController,
    AmAssignableActionSendSmsView: actionModalView,

    AmAssignableActionSetRoamingController: ActionSetRoamingController,
    AmAssignableActionSetRoamingView: actionModalView,

    AmAssignableActionSetActivationController: ActionActivationLockController,
    AmAssignableActionSetActivationView: actionModalView,

    AmAssignableActionSetWallpaperController: ActionSetWallpaperController,
    AmAssignableActionSetWallpaperView: actionModalView,

    AmAssignableActionSetDeviceNameController: ActionSetDeviceNameController,
    AmAssignableActionSetDeviceNameView: actionModalView,

    AmAssignableActionSetCustomFieldController: ActionSetCustomFieldController,
    AmAssignableActionSetCustomFieldView: actionModalView,

    AmAssignableActionUpdateDeviceInfoController: ActionUpdateDeviceController,
    AmAssignableActionUpdateDeviceInfoView: actionModalView,

    AmAssignableActionAttentionModeController: ActionAttentionModeController,
    AmAssignableActionAttentionModeView: actionModalView,

    AmAssignableActionFreezeDeviceController: ActionFreezeDeviceController,
    AmAssignableActionFreezeDeviceView: actionModalView,

    AmAssignableActionSendVppInvitationController: ActionSendVppInvitationController,
    AmAssignableActionSendVppInvitationView: actionModalView,

    AmAssignableActionRegisterUserController: ActionRegisterUserController,
    AmAssignableActionRegisterUserView: actionModalView,

    AmAssignableActionRetireUserController: ActionRetireUserController,
    AmAssignableActionRetireUserView: actionModalView,

    AmAssignableActionRemoveConfigurationController: ActionRemoveConfigurationController,
    AmAssignableActionRemoveConfigurationView: actionModalView,

    AmAssignableActionDemoteController: ActionDemoteController,
    AmAssignableActionDemoteView: actionModalView,

    // Assignable Content & Action Item
    // -----------------------

    // Resource

    AmAssignableContentItemRoute: UI.Route.extend(UI.Route.HasBreadcrumbs, {
      actions: {
        gotoTab: function(tabName) {
          this.transitionTo('am_assignable_content_item.' + tabName, this.get('controller.id'));
        },

        gotoNavItem: function(id) {
          this.transitionTo('am_assignable_content_item.details', id);
        },

        gotoEditContentProperties: function () {
          var id = this.get('controller.id');
          this.showModal({ name: 'am_assignable_edit_content_properties', model: { contentId: id } });
        },

        gotoDeleteContent: function () {
          var id = this.get('controller.id');
          this.showModal({ name: 'am_assignable_delete_content', model: { contentIds: Em.makeArray(id) } });
        },

        assignPoliciesToContent: function() {
          var self = this;
          var controller = this.get('controller');
          var id = controller.get('id');
          var name = controller.get('model.name').toString();

          var query = Query.Search.create({
            context: { contentId: id }
          });

          AmAssignableItem.get('relatedMobilePoliciesStore').acquire(null, query, function(assignedPolicies) {
            self.showModal({ name: 'am_assignable_assign_policies_to_content',
              model: {
                selectedContextName: name,

                contentIds: Em.A([id]),
                policyIds: null,

                isSelectingPolicy: true,
                assignedPolicies: assignedPolicies
              }
            })
          }, function(assignedPolicies) {
            self.send('showCommunicationError', { error: assignedPolicies.get('lastLoadError') });
          }, this);
        },

        editPolicyAssignments: function(selectionsList) {
          var controller = this.get('controller');
          var id = controller.get('id');
          var assignedContent = selectionsList.get('firstObject');

          this.showModal({ name: 'am_assignable_edit_policy_assignments',
            model: {
              contentIds: Em.makeArray(id),
              contentName: controller.get('model.name').toString(),
              policyIds: selectionsList.mapBy('id'),
              policyName: assignedContent.name,
              assignedContent: assignedContent
            }
          });
        },

        removeFromPolicies: function(selectionsList) {
          var id = this.get('controller.id');
          this.showModal({ name: 'am_assignable_remove_from_policies',
            model: {
              contentIds: Em.makeArray(id),
              policyId: null,
              policyAssignments: selectionsList
            }
          });
        }
      },

      setupController: function(controller, params) {
        this.activateBreadcrumbs(controller);
        controller.loadContentItem(params.contentId);
      }
    }),
    AmAssignableContentItemController: AmContentItemController,
    AmAssignableContentItemView: AmDesktop.AmNavTabPageView,

    AmAssignableActionsItemRoute: UI.Route.extend(UI.Route.HasBreadcrumbs, {
      actions: {
        gotoTab: function(tabName) {
          this.transitionTo('am_assignable_actions_item.' + tabName, this.get('controller.id'));
        },

        gotoNavItem: function(id) {
          this.transitionTo('am_assignable_actions_item.details', id);
        },

        gotoEditAction: function () {
          var context = this.get('controller').getContext();
          var actionName = context.actionName;

          assignableActionsList[actionName](this, { model: context, isEditMode: true });
        },

        gotoDeleteAction: function () {
          var id = this.get('controller.id');
          this.showModal({ name: 'am_assignable_delete_action', model: { actionIds: Em.makeArray(id) } });
        },

        gotoDuplicateAction: function () {
          var context = this.get('controller').getContext();
          var actionName = context.actionName;

          assignableActionsList[actionName](this, { model: context, isEditMode: true, isDuplicateMode: true });
        },

        gotoAddActionToPolicies: function(model) {
          // Depends on the context we may already have the model set, or we need to set it again
          var controller = this.get('controller');
          var actionId = controller.get('id'),
            actionName = controller.get('model.name').toString();

          if (model) {
            actionId = model.actionId;
            actionName = model.actionName;
          }

          if (!actionId) {
            getNewActionIdAndShowAddPolicyModal(this, actionId, actionName);
          } else {
            setAndShowAddPolicyModal(this, actionId, actionName);
          }
        },

        actionEditPolicyAssignments: function(model) {
          this.showModal({ name: 'am_assignable_action_edit_policy_assignments', model: model });
        },

        deleteActionPolicyAssignments: function(selectionsList) {
          var id = this.get('controller.id');

          this.showModal({ name: 'am_assignable_action_delete_policy_assignments',
            model: {
              isRemovingPolicy: true,

              contextId: id,
              contextName: null,
              assignments: selectionsList
            }
          });
        }
      },

      setupController: function(controller, params) {
        this.activateBreadcrumbs(controller);
        controller.loadActionItem(params.actionId);
      }
    }),
    AmAssignableActionsItemController: AmActionItemController,
    AmAssignableActionsItemView: AmDesktop.AmNavTabPageView,

    // Content Item Details

    AmAssignableContentItemDetailsRoute: ContentItemTabRoute.extend({
      tabName: 'details'
    }),
    AmAssignableContentItemDetailsView: AmContentItemDetailsView,

    // Action Item Details

    AmAssignableActionsItemDetailsRoute: ActionItemTabRoute.extend({
      tabName: 'details'
    }),
    AmAssignableActionsItemDetailsView: AmActionItemDetailsView,

    // Content Item Mobile Policies

    AmAssignableContentItemMobilePoliciesRoute: ContentItemTabRoute.extend({
      actions: {
        selectColumns: function() {
          var settings = { listController: this.get('controller.relatedMobilePoliciesController') };
          this.showModal({ name: 'am_assignable_select_columns', model: settings });
        },

        gotoListItem: function(id) {
          this.send('gotoAmMobilePolicy', id);
        }
      },

      tabName: 'mobilePolicies',

      renderTemplate: function() {
        this.render({ controller: this.get('controller.relatedMobilePoliciesController') });
      },

      setupController: function(controller, params) {
        this._super(controller, params);
        var relatedMobilePoliciesController = this.get('controller.relatedMobilePoliciesController');
        relatedMobilePoliciesController.resetController();
      }

    }),
    AmAssignableContentItemMobilePoliciesView: AmDesktop.AmListView,

    AmAssignableActionsItemMobilePoliciesRoute: ActionItemTabRoute.extend({
      actions: {
        selectColumns: function() {
          var settings = { listController: this.get('controller.relatedMobilePoliciesController') };
          this.showModal({ name: 'am_assignable_select_columns', model: settings });
        },

        gotoListItem: function(id) {
          this.send('gotoAmMobilePolicy', id);
        }
      },

      tabName: 'mobilePolicies',

      renderTemplate: function() {
        this.render({ controller: this.get('controller.relatedMobilePoliciesController') });
      },

      setupController: function(controller, params) {
        this._super(controller, params);
        var relatedMobilePoliciesController = this.get('controller.relatedMobilePoliciesController');
        relatedMobilePoliciesController.resetController();
      }

    }),
    AmAssignableActionsItemMobilePoliciesView: AmDesktop.AmListView,

    // Mobile Devices

    AmAssignableContentItemMobileDevicesRoute: ContentItemTabRoute.extend({
      actions: {
        selectColumns: function() {
          var settings = { listController: this.get('controller.relatedMobileDevicesController') };
          this.showModal({ name: 'am_assignable_select_columns', model: settings });
        },

        gotoListItem: function(id) {
          this.send('gotoAmMobileDevice', id);
        }
      },

      tabName: 'mobileDevices',

      renderTemplate: function() {
        this.render({ controller: this.get('controller.relatedMobileDevicesController') });
      }
    }),
    AmAssignableContentItemMobileDevicesView: AmDesktop.AmListView,

    // Add the Content to Policies (Assign Policies to Content)

    AmAssignableAssignPoliciesToContentController: AmAssignableItemFoundation.ContentAddPolicyAssignmentsController.extend({
      allowSaveOnNoSelection: false
    }),
    AmAssignableAssignPoliciesToContentView: AmAssignableItemFoundation.AmEditFlowView,

    // Edit Policy Assignments for a Policy/Policies

    AmAssignableEditPolicyAssignmentsController: AmAssignableItemFoundation.EditPolicyAssignmentsController,
    AmAssignableEditPolicyAssignmentsView: AmAssignableItemFoundation.AmEditFlowView,

    // Remove Policies

    AmAssignableRemoveFromPoliciesController: AmAssignableItemFoundation.RemoveContentFromPoliciesController,
    AmAssignableRemoveFromPoliciesView: Desktop.ModalActionView,

    // Add Action to Policies

    AmAssignableAddActionToPoliciesController: AmAssignableItemFoundation.AddActionPolicyAssignmentController,
    AmAssignableAddActionToPoliciesView: AmAssignableItemFoundation.AmActionPolicyAssignmentFlowView,

    // Edit Policy Assignments of Actions

    AmAssignableActionEditPolicyAssignmentsController: AmAssignableItemFoundation.EditActionPolicyAssignmentController,
    AmAssignableActionEditPolicyAssignmentsView: AmAssignableItemFoundation.AmActionPolicyAssignmentFlowView,

    // Remove Policy Assignments of Actions

    AmAssignableActionDeletePolicyAssignmentsController: AmAssignableItemFoundation.DeleteActionPolicyAssignmentController,
    AmAssignableActionDeletePolicyAssignmentsView: Desktop.ModalActionView
  };

  return {
    buildRoutes: function(router) {
      router.resource('am_assignable_list', { path: '/am_assignable_items' }, function() {
        this.route('content');
        this.route('in_house_apps');
        this.route('third_party_apps');
        this.route('books');
        this.route('config_profiles');
        this.route('provisioning_profiles');
        this.route('actions');
      });

      router.resource('am_assignable_content_item', { path: '/am_assignable_items/:contentId' }, function() {
        this.route('details');
        this.route('mobilePolicies', { path: '/mobile_policies' });
        this.route('mobileDevices', { path: '/mobile_devices' });
      });

      router.resource('am_assignable_actions_item', { path: '/am_assignable_items/actions/:actionId' }, function() {
        this.route('details');
        this.route('mobilePolicies', { path: '/mobile_policies' });
      });
    },

    initialize: function() {
      // Make sure we instantiate newContentWizard controller every time we call the action.
      window.App.register('controller:am_assignable_new_content', AmContentItemCreateController, { singleton: false });

      AmAssignableItem.reopen({
        assignableItemMenuStore: function () {
          return AmData.get('stores.assignableItemMenuStore');
        }.property(),

        assignableItemMenuSpec: function () {
          return AmData.get('specs.AmAssignableItemMenuSpec');
        }.property(),

        store: function () {
          return AmData.get('stores.contentStore');
        }.property(),

        userPrefsStore: function () {
          return AmData.get('stores.userPrefsStore');
        }.property(),

        userPrefsSpec: function () {
          return AmData.get('specs.UserPrefsSpec');
        }.property(),

        model: function () {
          return AmData.get('models.AmContent');
        }.property(),

        spec: function () {
          return AmData.get('specs.AmContentSpec');
        }.property(),

        relatedMobilePoliciesFromActionStore: function () {
          return AmData.get('stores.mobilePolicyFromActionStore');
        }.property(),

        relatedMobilePoliciesStore: function () {
          return AmData.get('stores.mobilePolicyFromContentStore');
        }.property(),

        relatedMobileDevicesStore: function () {
          return AmData.get('stores.mobileDeviceFromContentStore');
        }.property(),

        assignedInHouseAppsStore: function () {
          return AmData.get('stores.inHouseApplicationStore');
        }.property(),

        assignedInHouseAppsSpec: function () {
          return AmData.get('specs.AmInHouseApplicationSpec');
        }.property(),

        assignedThirdPartyAppsStore: function () {
          return AmData.get('stores.thirdPartyApplicationStore');
        }.property(),

        assignedThirdPartyAppsSpec: function () {
          return AmData.get('specs.AmThirdPartyApplicationSpec');
        }.property(),

        assignedBooksStore: function () {
          return AmData.get('stores.bookStore');
        }.property(),

        assignedBooksSpec: function () {
          return AmData.get('specs.AmBookSpec');
        }.property(),

        assignedConfigProfilesStore: function () {
          return AmData.get('stores.configurationProfileStore');
        }.property(),

        assignedConfigProfilesSpec: function () {
          return AmData.get('specs.AmConfigurationProfileSpec');
        }.property(),

        assignedProvisioningProfilesStore: function () {
          return AmData.get('stores.provisioningProfileStore');
        }.property(),

        assignedProvisioningProfilesSpec: function () {
          return AmData.get('specs.AmProvisioningProfileSpec');
        }.property(),

        assignedActionsStore: function () {
          return AmData.get('stores.actionsStore');
        }.property(),

        assignedActionsSpec: function () {
          return AmData.get('specs.AmActionsSpec');
        }.property()
      });
    },

    appClasses: appClasses,
    appStrings: strings,

    appActions: {
      gotoAmAssignableContent: function(id) {
        this.transitionTo('am_assignable_content_item.details', id);
      },
      gotoAmAssignableAction: function(id) {
        this.transitionTo('am_assignable_actions_item.details', id);
      }
    },

    topNavSpec: {
      name: function () {
        return Locale.renderGlobals('amAssignableItem.topNavSpec.assignableItemsTitle').toString();
      }.property(),

      noLandingAction: true,
      iconClassName: 'icon-content',
      landingPath: 'am_assignable_list.content',
      landingIcon: '../packages/am/am-assignable-item/img/icon-content-logo-retina.png',
      landingButtonClassName: 'is-button-for-landing-page-am-assignable-item',

      routes: [
        {
          name: function () {
            return Locale.renderGlobals('amAssignableItem.topNavSpec.list.content').toString();
          }.property(),
          path: 'am_assignable_list.content'
        },
        {
          name: function () {
            return Locale.renderGlobals('amAssignableItem.topNavSpec.list.inHouseApplications').toString();
          }.property(),
          path: 'am_assignable_list.in_house_apps'
        },
        {
          name: function () {
            return Locale.renderGlobals('amAssignableItem.topNavSpec.list.thirdPartyApplications').toString();
          }.property(),
          path: 'am_assignable_list.third_party_apps'
        },
        {
          name: function () {
            return Locale.renderGlobals('amAssignableItem.topNavSpec.list.bookstoreBooks').toString();
          }.property(),
          path: 'am_assignable_list.books'
        },
        {
          name: function () {
            return Locale.renderGlobals('amAssignableItem.topNavSpec.list.configurationProfiles').toString();
          }.property(),
          path: 'am_assignable_list.config_profiles'
        },
        {
          name: function () {
            return Locale.renderGlobals('amAssignableItem.topNavSpec.list.provisioningProfiles').toString();
          }.property(),
          path: 'am_assignable_list.provisioning_profiles'
        },
        {
          name: function () {
            return Locale.renderGlobals('amAssignableItem.topNavSpec.list.actions').toString();
          }.property(),
          path: 'am_assignable_list.actions'
        }
      ]
    }
  };
});
