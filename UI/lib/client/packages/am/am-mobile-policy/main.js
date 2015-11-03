define([
  'ember',
  'desktop',
  'am-desktop',
  'packages/platform/ui',
  'query',
  'am-data',
  'guid',
  'packages/am/am-app-foundation',
  'packages/am/am-assignable-item-foundation',
  'packages/platform/nav-page-view',

  './lib/namespace',
  './lib/controllers/mobile_policy_list_controller',
  './lib/controllers/mobile_policy_item_controller',

  './lib/controllers/mobile_policy_delete_policy_controller',
  './lib/controllers/mobile_policy_rename_standard_policy_controller',
  './lib/controllers/mobile_policy_create_standard_policy_controller',
  './lib/controllers/smart_policy_controller',
  './lib/controllers/mobile_policy_item_add_in_house_app_controller',
  './lib/controllers/mobile_policy_item_remove_in_house_app_controller',
  './lib/controllers/mobile_policy_item_add_third_party_app_controller',
  './lib/controllers/mobile_policy_item_remove_third_party_app_controller',

  './lib/controllers/mobile_policy_item_add_config_profile_controller',
  './lib/controllers/mobile_policy_remove_config_profile_controller',
  './lib/controllers/mobile_policy_edit_policy_assignment_config_profile_controller',

  './lib/controllers/mobile_policy_item_add_mobile_devices_controller',
  './lib/controllers/mobile_policy_item_move_mobile_devices_controller',
  './lib/controllers/mobile_policy_item_remove_mobile_devices_controller',
  './lib/controllers/mobile_policy_item_reexecute_action_controller',

  'text!./lib/templates/smart_policy_wizard_flow.handlebars',

  'i18n!./nls/strings',
  'locale'
], function (
  Em,
  Desktop,
  AmDesktop,
  UI,
  Query,
  AmData,
  Guid,
  AmAppFoundation,
  AmAssignableItemFoundation,
  NavPageView,

  AmMobilePolicy,
  AmMobilePolicyListController,
  AmMobilePolicyItemController,

  MobilePolicyDeletePolicyController,
  MobilePolicyRenamePolicyController,
  MobilePolicyNewFixedPolicyController,
  MobilePolicySmartPolicyController,
  MobilePolicyAddInHouseAppController,
  MobilePolicyRemoveInHouseAppController,
  MobilePolicyAddThirdPartyAppController,
  MobilePolicyRemoveThirdPartyAppController,

  MobilePolicyAddConfigProfileController,
  MobilePolicyRemoveConfigProfileController,
  MobilePolicyEditPolicyAssignmentConfigProfileController,

  MobilePolicyAddDevicesController,
  MobilePolicyMoveDevicesController,
  MobilePolicyRemoveDevicesController,
  MobilePolicyReexecuteActionController,

  MobilePolicySmartPolicyWizardFlowTemplate,

  strings,
  Locale
) {
  'use strict';

  // Policy Item Tabs
  // ----------------
  //
  // Tab routes set the tab name in the item controller, so that it renders as active.

  var PolicyItemTabRoute = UI.Route.extend({
    controllerName: 'amMobilePolicyItem',
    tabName: null,

    setupController: function(controller) {
      controller.set('activeTab', this.get('tabName'));
    }
  });

  var appClasses = {

    // Shared Package Controllers
    // --------------------------

    AmMobilePolicyNavSizeController: NavPageView.NavSizeController.extend({
      storageKey: 'am.MobilePolicy.NavViewSplitterSettings'
    }),

    // Landing - Mobile Policies List
    // ------------------------------

    AmMobilePolicyListRoute: UI.NoSetupRoute.extend(UI.Route.HasBreadcrumbs, {
      actions: {
        gotoListItem: function(id) {
          if( this.get('controller').getRowData([id])[0].get('data.filterType') === 255 ) {
            this.transitionTo('am_mobile_read_only_policy_item.devices', id);
          } else {
            this.transitionTo('am_mobile_policy_item.devices', id);
          }
        },

        deletePolicy: function (selectionsList) {
          this.showModal({ name: 'am_mobile_policy_delete_policy', model: selectionsList });
        },

        renamePolicy: function (selectionsList) {
          this.showModal({ name: 'am_mobile_policy_rename_policy', model: selectionsList });
        },

        newFixedPolicy: function () {
          this.showModal({ name: 'am_mobile_policy_new_fixed_policy' });
        },

        newSmartPolicyWizard: function () {
          this.showModal({ name: 'am_mobile_policy_new_smart_policy' });
        },

        editSmartPolicy: function (selectionsList) {
          this.showModal({ name: 'am_mobile_policy_edit_smart_policy', model: { policy: selectionsList } });
        }
      },

      setupController: function(controller) {
        this.activateBreadcrumbs(controller);
        controller.resetController();
      }
    }),
    AmMobilePolicyListController: AmMobilePolicyListController,
    AmMobilePolicyListView: AmDesktop.AmListView,

    // Delete Mobile Policy
    AmMobilePolicyDeletePolicyController: MobilePolicyDeletePolicyController,
    AmMobilePolicyDeletePolicyView: Desktop.ModalActionView,

    // Edit Standard Mobile Policy
    AmMobilePolicyRenamePolicyController: MobilePolicyRenamePolicyController,
    AmMobilePolicyRenamePolicyView: Desktop.ModalActionView,

    // New Standard Policy
    AmMobilePolicyNewFixedPolicyController: MobilePolicyNewFixedPolicyController,
    AmMobilePolicyNewFixedPolicyView: Desktop.ModalActionView,

    // New Smart Policy
    AmMobilePolicyNewSmartPolicyController: MobilePolicySmartPolicyController,
    AmMobilePolicyNewSmartPolicyView: Em.View.extend({
      defaultTemplate: Em.Handlebars.compile(MobilePolicySmartPolicyWizardFlowTemplate)
    }),

    // Edit Smart Policy
    AmMobilePolicyEditSmartPolicyController: MobilePolicySmartPolicyController,
    AmMobilePolicyEditSmartPolicyView: Em.View.extend({
      defaultTemplate: Em.Handlebars.compile(MobilePolicySmartPolicyWizardFlowTemplate)
    }),

    // Mobile Policies Item
    // --------------------

    // Resource

    AmMobilePolicyItemRoute: UI.Route.extend(UI.Route.HasBreadcrumbs, {
      actions: {
        gotoNavItem: function(id, filterType) {
          if( filterType === 255 ) {
            this.transitionTo('am_mobile_read_only_policy_item.devices', id);
          } else {
            this.transitionTo('am_mobile_policy_item.' + this.get('controller.activeTab'), id);
          }
        },

        gotoTab: function(tabName) {
          this.transitionTo('am_mobile_policy_item.' + tabName, this.get('controller.id'));
        },

        selectColumns: function (controller) {
          controller = !Em.isNone(controller) ? controller : this.get('controller');
          this.showModal({ name: 'am_mobile_policy_select_columns', model: { listController: controller } });
        },

        addInHouseApp: function () {
          this.showModal({ name: 'am_mobile_policy_add_in_house_app', model: Em.makeArray(this.get('controller')) });
        },

        removeInHouseApp: function (selectionsList) {
          var id = this.get('controller.id');
          this.showModal({ name: 'am_mobile_policy_remove_in_house_app',
            model: {
              policyId: id,
              apps: selectionsList
            }
          });
        },

        addThirdPartyApp: function () {
          this.showModal({ name: 'am_mobile_policy_add_third_party_app', model: Em.makeArray(this.get('controller')) });
        },

        removeThirdPartyApp: function (selectionsList) {
          var id = this.get('controller.id');
          this.showModal({ name: 'am_mobile_policy_remove_third_party_app',
            model: {
              policyId: id,
              apps: selectionsList
            }
          });
        },

        addConfigProfile: function () {
          this.showModal({ name: 'am_mobile_policy_add_config_profile', model: Em.makeArray(this.get('controller')) });
        },

        editPolicyAssignmentConfigProfile: function(selectionsList) {
          var id = this.get('controller.id');
          this.showModal({ name: 'am_mobile_policy_edit_policy_assignment_config_profile',
            model: {
              policyId: id,
              configProfiles: selectionsList
            }
          });
        },

        removeConfigProfile: function (selectionsList) {
          var id = this.get('controller.id');
          this.showModal({ name: 'am_mobile_policy_remove_config_profile',
            model: {
              policyId: id,
              configProfiles: selectionsList
            }
          });
        },

        addContentToPolicy: function() {
          var self = this, controller = this.get('controller');
          var id = controller.get('id');
          var name = controller.get('model.name').toString();

          var query = Query.Search.create({
            context: { mobilePolicyId: id }
          });

          AmMobilePolicy.get('relatedContentStore').acquire(null, query, function(assignedContents) {
            self.showModal({ name: 'am_mobile_policy_add_content_to_policy',
              model: {
                selectedContextName: name,

                contentIds: null,
                policyIds: Em.A([id]),

                assignedContents: assignedContents
              }
            })
          }, function(assignedContent) {
            self.send('showCommunicationError', { error: assignedContent.get('lastLoadError') });
          }, this);
        },

        editPolicyAssignments: function(selectionsList) {
          var controller = this.get('controller'),  id = controller.get('id');

          this.showModal({ name: 'am_mobile_policy_edit_policy_assignments',
            model: {
              contentIds: selectionsList.id,
              contentName: selectionsList.name,
              policyName: controller.get('model.name').toString(),
              policyIds: Em.A([id]),
              assignedContent: selectionsList
            }
          });
        },

        removeContentFromPolicy: function(selectionsList) {
          var id = this.get('controller.id');
          var contentIds = [];

          for (var i = 0; i < selectionsList.length; i++) {
            contentIds.pushObject(selectionsList[i]);
          }

          this.showModal({ name: 'am_mobile_policy_remove_from_policies',
            model: {
              contentIds: contentIds,
              policyId: id
            }
          });
        },

        addMobileDevices: function() {
          this.showModal({ name: 'am_mobile_policy_add_devices', model: this.get('controller.model.data') });
        },

        removeMobileDevices: function(selectionsList) {
          var id = this.get('controller.id');
          this.showModal({ name: 'am_mobile_policy_remove_devices',
            model: {
              policyId: id,
              devices: selectionsList
            }
          });
        },

        moveMobileDevices: function(selectionsList) {
          var id = this.get('controller.id');
          this.showModal({ name: 'am_mobile_policy_move_devices',
            model: {
              policyId: id,
              devices: selectionsList
            }
          });
        },

        addActionsToPolicy: function() {
          var context = this.get('controller.model.data');
          var policyName = context.name,
            policyId = context.id;

          var self = this;

          var query = Query.Search.create({
            context: { mobilePolicyId: policyId }
          });

          AmData.get('stores.actionsFromPolicyStore').acquire(null, query, function(assignedActions) {
            self.showModal({ name: 'am_mobile_policy_add_actions_to_policy',
              model: {
                policyId: policyId,
                policyName: policyName,

                assignedActions: assignedActions.get('content')
              }
            })
          }, function(assignedPolicies) {
            self.send('showCommunicationError', { error: assignedPolicies.get('lastLoadError') });
          }, this);
        },

        policyEditActionAssignments: function(model) {
          this.showModal({ name: 'am_mobile_policy_edit_action_assignments', model: model });
        },

        deleteActionPolicyAssignments: function(selectionsList) {
          var controller = this.get('controller');

          this.showModal({ name: 'am_mobile_policy_delete_action_policy_assignments',
            model: {
              isRemovingPolicy: false,

              contextId: controller.get('id'),
              contextName: controller.get('model.data.name'),
              assignments: selectionsList
            }
          });
        },

        reexecuteAction: function(selectionsList) {
          var controller = this.get('controller');

          this.showModal({ name: 'am_mobile_policy_reexecute_actions',
            model: {
              uuid: controller.get('model.data.guid'),
              actions: selectionsList
            }
          });
        }
      },

      setupController: function(controller, params) {
        this.activateBreadcrumbs(controller);
        controller.load(params.mobilePolicyId);
      }
    }),

    AmMobilePolicyItemController: AmMobilePolicyItemController,
    AmMobilePolicyItemView: AmDesktop.AmNavTabPageView,

    AmMobileReadOnlyPolicyItemRoute: UI.Route.extend(UI.Route.HasBreadcrumbs,{
      actions: {
        gotoNavItem: function (id) {
          if( this.get('controller.amMobilePolicyListController').getRowData([id]).get('data.filterType') === 255 ) {
            this.transitionTo('am_mobile_read_only_policy_item.devices', id);
          } else {
            this.transitionTo('am_mobile_policy_item.' + this.get('controller.activeTab'), id);
          }
        },

        selectColumns: function (controller) {
          controller = !Em.isNone(controller) ? controller : this.get('controller');
          this.showModal({ name: 'am_mobile_policy_select_columns', model: { listController: controller } });
        }
      },

      setupController: function(controller, params) {
        this.activateBreadcrumbs(controller);
        controller.setProperties({
          activeTab: 'devices',
          readOnlyPolicy: true
        });
        controller.load(params.mobilePolicyId);
      }
    }),

    AmMobileReadOnlyPolicyItemController: AmMobilePolicyItemController,
    AmMobileReadOnlyPolicyItemView: AmDesktop.AmNavTabPageView,

    // Related Mobile Devices

    AmMobilePolicyItemDevicesRoute: PolicyItemTabRoute.extend({
      actions: {
        gotoListItem: function(id) {
          this.send('gotoAmMobileDevice', id);
        }
      },

      tabName: 'devices',

      renderTemplate: function() {
        this.render({ controller: this.get('controller.relatedMobileDeviceController') });
      }
    }),
    AmMobilePolicyItemDevicesView: AmDesktop.AmListView,

    AmMobileReadOnlyPolicyItemDevicesRoute: UI.Route.extend({
      controllerName: 'amMobileReadOnlyPolicyItem',

      actions: {
        gotoListItem: function(id) {
          this.send('gotoAmMobileDevice', id);
        }
      },

      renderTemplate: function() {
        this.render({ controller: this.get('controller.relatedMobileDeviceController') });
      }
    }),
    AmMobileReadOnlyPolicyItemDevicesView: AmDesktop.AmListView,

    // Related In-House Apps

    AmMobilePolicyItemInHouseAppsRoute: PolicyItemTabRoute.extend({
      tabName: 'inHouseApps',

      renderTemplate: function() {
        this.render({ controller: this.get('controller.relatedInHouseAppsController') });
      }
    }),
    AmMobilePolicyItemInHouseAppsView: AmDesktop.AmListView,

    // Related Third-Party Apps

    AmMobilePolicyItemThirdPartyAppsRoute: PolicyItemTabRoute.extend({
      tabName: 'thirdPartyApps',

      renderTemplate: function() {
        this.render({ controller: this.get('controller.relatedThirdPartyAppsController') });
      }
    }),
    AmMobilePolicyItemThirdPartyAppsView: AmDesktop.AmListView.extend({
      didInsertElement: function() {
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

    // Related Configuration Profiles

    AmMobilePolicyItemConfigProfilesRoute: PolicyItemTabRoute.extend({
      tabName: 'configProfiles',

      renderTemplate: function() {
        this.render({ controller: this.get('controller.relatedConfigProfilesController') });
      }
    }),
    AmMobilePolicyItemConfigProfilesView: AmDesktop.AmListView,

    // Related Content

    AmMobilePolicyItemContentRoute: PolicyItemTabRoute.extend({
      actions: {
        gotoListItem: function(id) {
          this.send('gotoAmAssignableContent', id);
        }
      },

      tabName: 'content',

      renderTemplate: function() {
        this.render({ controller: this.get('controller.relatedContentController') });
      }
    }),
    AmMobilePolicyItemContentView: AmDesktop.AmListView,

    // Related Actions

    AmMobilePolicyItemActionsRoute: PolicyItemTabRoute.extend({
      actions: {
        gotoListItem: function(id) {
          this.send('gotoAmAssignableAction', id);
        }
      },

      tabName: 'actions',

      renderTemplate: function() {
        this.render({ controller: this.get('controller.relatedActionsController') });
      }
    }),
    AmMobilePolicyItemActionsView: AmDesktop.AmListView,

    // Modals
    // ------

    // Select Columns

    AmMobilePolicySelectColumnsController: AmAppFoundation.SelectColumnsController,
    AmMobilePolicySelectColumnsView: Desktop.ModalColumnChooserView,

    // Add In-House Application

    AmMobilePolicyAddInHouseAppController: MobilePolicyAddInHouseAppController,
    AmMobilePolicyAddInHouseAppView: Desktop.ModalActionView,

    // Remove In-House Application

    AmMobilePolicyRemoveInHouseAppController: MobilePolicyRemoveInHouseAppController,
    AmMobilePolicyRemoveInHouseAppView: Desktop.ModalActionView,

    // Add Third Party Application

    AmMobilePolicyAddThirdPartyAppController: MobilePolicyAddThirdPartyAppController,
    AmMobilePolicyAddThirdPartyAppView: Desktop.ModalActionView,

    // Remove Third Party Application

    AmMobilePolicyRemoveThirdPartyAppController: MobilePolicyRemoveThirdPartyAppController,
    AmMobilePolicyRemoveThirdPartyAppView: Desktop.ModalActionView,

    // Add Config Profile Application

    AmMobilePolicyAddConfigProfileController: MobilePolicyAddConfigProfileController,
    AmMobilePolicyAddConfigProfileView: Desktop.ModalActionView,

    // Edit Policy Assignment for Config Profile

    AmMobilePolicyEditPolicyAssignmentConfigProfileController:  MobilePolicyEditPolicyAssignmentConfigProfileController,
    AmMobilePolicyEditPolicyAssignmentConfigProfileView: AmAssignableItemFoundation.AmEditFlowView,

    // Remove Config Profile Application

    AmMobilePolicyRemoveConfigProfileController: MobilePolicyRemoveConfigProfileController,
    AmMobilePolicyRemoveConfigProfileView: Desktop.ModalActionView,

    // Add Content to a Policy

    AmMobilePolicyAddContentToPolicyController: AmAssignableItemFoundation.ContentAddPolicyAssignmentsController.extend({
      allowSaveOnNoSelection: false
    }),
    AmMobilePolicyAddContentToPolicyView: AmAssignableItemFoundation.AmEditFlowView,

    // Edit Policy Assignments for the Content/Contents

    AmMobilePolicyEditPolicyAssignmentsController: AmAssignableItemFoundation.EditPolicyAssignmentsController,
    AmMobilePolicyEditPolicyAssignmentsView: AmAssignableItemFoundation.AmEditFlowView,

    // Remove Policies

    AmMobilePolicyRemoveFromPoliciesController: AmAssignableItemFoundation.RemoveContentFromPoliciesController,
    AmMobilePolicyRemoveFromPoliciesView: Desktop.ModalActionView,

    // Add Mobile Devices

    AmMobilePolicyAddDevicesController: MobilePolicyAddDevicesController,
    AmMobilePolicyAddDevicesView: Desktop.ModalActionView,

    // Move Mobile Devices

    AmMobilePolicyMoveDevicesController: MobilePolicyMoveDevicesController,
    AmMobilePolicyMoveDevicesView: Desktop.ModalActionView,

    // Remove Mobile Devices

    AmMobilePolicyRemoveDevicesController: MobilePolicyRemoveDevicesController,
    AmMobilePolicyRemoveDevicesView: Desktop.ModalActionView,

    // Add Actions to a Policy

    AmMobilePolicyAddActionsToPolicyController: AmAssignableItemFoundation.AddActionPolicyAssignmentController,
    AmMobilePolicyAddActionsToPolicyView: AmAssignableItemFoundation.AmActionPolicyAssignmentFlowView,

    // Edit Policy Assignments of Actions

    AmMobilePolicyEditActionAssignmentsController: AmAssignableItemFoundation.EditActionPolicyAssignmentController,
    AmMobilePolicyEditActionAssignmentsView: AmAssignableItemFoundation.AmActionPolicyAssignmentFlowView,

    // Remove Policy Assignments of Actions

    AmMobilePolicyDeleteActionPolicyAssignmentsController: AmAssignableItemFoundation.DeleteActionPolicyAssignmentController,
    AmMobilePolicyDeleteActionPolicyAssignmentsView: Desktop.ModalActionView,

    // Remove Policy Assignments of Actions

    AmMobilePolicyReexecuteActionsController: MobilePolicyReexecuteActionController,
    AmMobilePolicyReexecuteActionsView: Desktop.ModalActionView
  };

  return {
    buildRoutes: function(router) {
      router.route('am_mobile_policy_list', { path: '/am_mobile_policies' });

      router.resource('am_mobile_read_only_policy_item', { path: '/am_mobile_read_only_policies/:mobilePolicyId' }, function() {
        this.route('devices', { path: '/devices' })
      });

      router.resource('am_mobile_policy_item', { path: '/am_mobile_policies/:mobilePolicyId' }, function() {
        this.route('devices', { path: '/devices' });
        this.route('inHouseApps', { path: '/in_house_apps' });
        this.route('thirdPartyApps', { path: '/third_party_apps' });
        this.route('configProfiles', { path: '/config_profiles' });
        this.route('content', { path: '/content' });
        this.route('actions', { path: '/actions' });
      });
    },

    initialize: function() {
      AmMobilePolicy.reopen({
        store: function () {
          return AmData.get('stores.mobilePolicyStore');
        }.property(),

        spec: function () {
          return AmData.get('specs.AmMobilePolicySpec');
        }.property(),

        relatedContentStore: function () {
          return AmData.get('stores.contentFromMobilePolicyStore');
        }.property(),

        relatedContentSpec: function () {
          return AmData.get('specs.AmContentFromMobilePolicySpec');
        }.property(),

        relatedMobileDevicesStore: function () {
          return AmData.get('stores.mobileDeviceFromMobilePolicyStore');
        }.property(),

        relatedMobileDevicesSpec: function () {
          return AmData.get('specs.AmMobileDeviceFromMobilePolicySpec');
        }.property(),

        userPrefsStore: function () {
          return AmData.get('stores.userPrefsStore');
        }.property(),

        relatedActionStore: function () {
          return AmData.get('stores.actionsFromPolicyStore');
        }.property()
      });
    },

    appClasses: appClasses,
    appStrings: strings,

    appActions: {
      gotoAmMobilePolicy: function(id) {
        this.transitionTo('am_mobile_policy_item.devices', id);
      }
    },

    topNavSpec: {
      name: function () {
        return Locale.renderGlobals('amMobilePolicies.topNavSpec.mobilePoliciesTitle').toString();
      }.property(),

      noLandingAction: true,
      iconClassName: 'icon-policy-page',
      landingPath: 'am_mobile_policy_list',
      landingIcon: '../packages/am/am-mobile-policy/img/icon-policy-retina.png',
      landingButtonClassName: 'is-button-for-landing-page-am-mobile-policy',

      routes: [
        {
          name: 'amMobilePolicies.topNavSpec.allMobilePoliciesTitle'.tr(),
          path: 'am_mobile_policy_list'
        }
      ]
    }
  };
});
