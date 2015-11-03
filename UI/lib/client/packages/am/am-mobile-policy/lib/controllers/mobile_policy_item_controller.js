define([
  'ember',
  'help',
  'ui',
  'guid',
  'desktop',
  'am-desktop',
  'am-data',

  '../namespace',

  './mobile_policy_item_related_mobile_device_controller',
  './mobile_policy_item_related_content_controller',
  './mobile_policy_item_related_config_profiles_controller',
  './mobile_policy_item_related_third_party_apps_controller',
  './mobile_policy_item_related_in_house_apps_controller',
  './mobile_policy_item_related_actions_controller',

  'text!../templates/mobile_policy_item_button_block.handlebars'

], function(
  Em,
  Help,
  UI,
  Guid,
  Desktop,
  AmDesktop,
  AmData,

  AmMobilePolicy,

  AmMobilePolicyItemRelatedMobileDeviceController,
  AmMobilePolicyItemRelatedContentController,
  AmMobilePolicyItemRelatedConfigProfileController,
  AmMobilePolicyItemRelatedThirdPartyAppsController,
  AmMobilePolicyItemRelatedInHouseAppsController,
  AmMobilePolicyItemRelatedActionsController,

  buttonBlockTemplate
) {
  'use strict';

  return Em.Controller.extend({

    actions: {
      rowClick: function(row) {
        this.send('gotoNavItem', row.get('nodeId'), row.get('nodeData.data.filterType'));
      }
    },

    tReadOnlyPolicy: 'amMobilePolicies.mobilePolicyDetailsPage.navigationPane.readOnlyPolicy'.tr(),

    tabItemView: Desktop.TabItemView,

    buttonBlockView: AmDesktop.AmNavTabPageView.ButtonBlockView.extend({
      defaultTemplate: Em.Handlebars.compile(buttonBlockTemplate)
    }),

    amMobilePolicyListController: Em.inject.controller('amMobilePolicyList'),

    relatedContentController: null,
    relatedMobileDeviceController: null,
    relatedConfigProfilesController: null,
    relatedThirdPartyAppsController: null,
    relatedInHouseAppsController: null,
    relatedActionsController: null,

    // ID of the requested Mobile Policy.
    id: null,
    isReadOnly: false,
    nameUnmanaged: null,
    urlForHelp: Help.uri(1026),
    activeTab: null,

    // Will be changed to true in the router if user clicked on a Read Only policy
    readOnlyPolicy: false,

    NavController: AmDesktop.AmNavController.extend({
      content: Em.computed.oneWay('parentController.amMobilePolicyListController'),
      dataStore: function () {
        return AmMobilePolicy.get('store');
      }.property()
    }),

    snapContainerIconPath: function() {
      return '../packages/platform/desktop/img/' + (this.get('model.data.isSmartPolicy') || this.get('isReadOnly') ? '32-Smart-Policy.png' : '32-Policy.png');
    }.property('model.data.isSmartPolicy', 'isReadOnly'),

    snapContainerContentClass: 'snap-container-content',
    snapContainerSubTitle: function() {
      if( this.get('isReadOnly') ) {
        return this.get('tReadOnlyPolicy');
      }
    }.property('isReadOnly'),

    breadcrumb: function() {
      return UI.Breadcrumb.create({
        parentBreadcrumb: this.get('amMobilePolicyListController.breadcrumb'),
        path: 'am_mobile_policy_item.devices',

        titleResource: 'amMobilePolicies.mobilePolicyDetailsPage.title',
        controller: this,
        contextBinding: 'controller.id'
      });
    }.property('amMobilePolicyListController.breadcrumb'),

    lock: function() { return Guid.generate(); }.property(),

    load: function(id) {
      if (this.get('id') === id) {
        return;
      }

      var controller = this;

      controller.relatedContentController.get('selections').clear();
      controller.relatedConfigProfilesController.get('selections').clear();
      controller.relatedThirdPartyAppsController.get('selections').clear();
      controller.relatedInHouseAppsController.get('selections').clear();
      controller.relatedActionsController.get('selections').clear();

      this.setProperties({
        id: id,
        model: AmMobilePolicy.get('store').acquireOne(this.get('lock'), id, function() {
          if( this.get('data.filterType') === 255 ) {
            var nameTitle = this.get('data.name');
            var valueArray = nameTitle.toString().split('|');
            // If tooltip information inside, display it
            if(valueArray.length === 2) {
              var name = valueArray[0];
              var tooltip = valueArray[1];
              nameTitle = Em.String.htmlSafe('<span class="tooltip-w" data-tooltip-attr="title" data-sticky-tooltip="true" title="' + tooltip + '">' + name +'</>');
            }

            // Special case for unmanaged device. Simple using of 'name' property for unmanaged device does not work. Title is disappearing after clicking to the next item and back.
            controller.setProperties({
              name:          nameTitle,
              nameUnmanaged: nameTitle,
              isReadOnly:    true
            });
          }
        })
     });
    },

    init: function() {
      this._super();

      this.setProperties({
        relatedContentController: AmMobilePolicyItemRelatedContentController.create({ parentController: this }),
        relatedMobileDeviceController: AmMobilePolicyItemRelatedMobileDeviceController.create({ parentController: this }),
        relatedConfigProfilesController: AmMobilePolicyItemRelatedConfigProfileController.create({ parentController: this }),
        relatedThirdPartyAppsController: AmMobilePolicyItemRelatedThirdPartyAppsController.create({ parentController: this }),
        relatedInHouseAppsController: AmMobilePolicyItemRelatedInHouseAppsController.create({ parentController: this }),
        relatedActionsController: AmMobilePolicyItemRelatedActionsController.create({ parentController: this })
      });
    },

    // Nav content and splitter
    navTitle: 'amMobilePolicies.mobilePolicyDetailsPage.navigationPane.title'.tr(),

    navController: function() {
      return this.NavController.create({
        parentController: this,
        selectedIdBinding: 'parentController.id'
      });
    }.property(),

    navSizeController: Em.inject.controller('amMobilePolicyNavSize'),

    tabList: function() {
      var rtn = [
        Em.Object.create({
          labelResource: 'amMobilePolicies.mobilePolicyDetailsPage.body.tabLabels.mobileDevices',
          item: 'devices',
          itemClass: 'is-button-for-tab-devices'
        }),
        Em.Object.create({
          labelResource: 'amMobilePolicies.mobilePolicyDetailsPage.body.tabLabels.inHouseApplications',
          item: 'inHouseApps',
          itemClass: 'is-button-for-tab-in-house-apps'
        }),
        Em.Object.create({
          labelResource: 'amMobilePolicies.mobilePolicyDetailsPage.body.tabLabels.thirdPartyApplications',
          item: 'thirdPartyApps',
          itemClass: 'is-button-for-tab-third-party-apps'
        }),
        Em.Object.create({
          labelResource: 'amMobilePolicies.mobilePolicyDetailsPage.body.tabLabels.configurationProfiles',
          item: 'configProfiles',
          itemClass: 'is-button-for-configuration-profiles'
        }),
        Em.Object.create({
          labelResource: 'amMobilePolicies.mobilePolicyDetailsPage.body.tabLabels.content',
          item: 'content',
          itemClass: 'is-button-for-tab-content'
        })
      ];

      if(this.get('model.data.isSmartPolicy') === 1) {
        rtn.push(Em.Object.create({
          labelResource: 'amMobilePolicies.mobilePolicyDetailsPage.body.tabLabels.actions',
          item: 'actions',
          itemClass: 'is-button-for-tab-actions'
        }));
      }

      return rtn;
    }.property('model.data')
  });
});
