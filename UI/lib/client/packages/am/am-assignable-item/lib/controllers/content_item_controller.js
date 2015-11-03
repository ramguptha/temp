define([
  'ember',
  'help',
  'ui',
  'desktop',
  'am-desktop',
  'guid',

  'am-data',
  '../namespace',

  'packages/platform/child-controller',

  './content_item_related_mobile_policies_controller',
  './content_item_related_mobile_devices_controller',

  'text!../templates/content_item_button_block.handlebars',

  'formatter'
], function (
  Em,
  Help,
  UI,
  Desktop,
  AmDesktop,
  Guid,

  AmData,
  AmContent,

  ChildController,

  AmContentItemRelatedMobilePoliciesController,
  AmContentItemRelatedMobileDevicesController,

  buttonBlockTemplate,

  Formatter
) {
  'use strict';

  return Em.Controller.extend({

    actions: {
      rowClick: function(row) {
        this.send('gotoNavItem', row.get('node.id'));
      }
    },

    navTitle: 'amAssignableItem.assignableContentDetailsPage.navigationPane.title'.tr(),

    amAssignableListContentController: Em.inject.controller('amAssignableListContent'),

    urlForHelp: Help.uri(1034),
    id: null,
    lock: Guid.property(),
    activeTab: null,

    tabItemView: Desktop.TabItemView,
    buttonBlockView: AmDesktop.AmNavTabPageView.ButtonBlockView.extend({
      defaultTemplate: Em.Handlebars.compile(buttonBlockTemplate)
    }),

    NavController: AmDesktop.AmNavController.extend({
      content: Em.computed.oneWay('parentController.amAssignableListContentController'),
      dataStore: function () {
        return AmContent.get('store');
      }.property()
    }),

    relatedMobilePoliciesController: null,
    relatedMobileDevicesController: null,

    navSizeController: Em.inject.controller('amAssignableNavSize'),
    amContentListController: function () {
      return AmContent.get('amContentListController');
    }.property(),

    navController: function() {
      return this.NavController.create({
        parentController: this,
        selectedIdBinding: 'parentController.id'
      });
    }.property(),

    breadcrumb: function() {
      return UI.Breadcrumb.create({
        parentBreadcrumb: this.get('amAssignableListContentController.breadcrumb'),
        path: 'am_assignable_list.content',

        titleResource: 'amAssignableItem.assignableContentDetailsPage.title',
        controller: this,
        contextBinding: 'controller.id'
      });
    }.property('amAssignableListContentController.breadcrumb'),

    tabList: function () {
      return [
        Em.Object.create({
          labelResource: 'amAssignableItem.assignableContentDetailsPage.tabList.details',
          item: 'details'
        }),
        Em.Object.create({
          labelResource: 'amAssignableItem.assignableContentDetailsPage.tabList.policies',
          item: 'mobilePolicies'
        }),
        Em.Object.create({
          labelResource: 'amAssignableItem.assignableContentDetailsPage.tabList.mobileDevices',
          item: 'mobileDevices'
        })
      ];
    }.property(),

    hasPassword: function () {
      var hasPassword = !Em.isNone(this.get('model.data.hashedPassword'));
      return Formatter.formatBoolean(hasPassword);
    }.property('model.data.hashedPassword'),

    passcodePresentIconClass: function () {
      var hasPassword = !Em.isNone(this.get('model.data.hashedPassword'));
      return (hasPassword ? 'icon-locked' : 'icon-unlocked');
    }.property('model.data.hashedPassword'),

    snapContainerContentClass: 'snap-container-content',

    snapContainerIconPath: function () {
      var iconSrcPath = null;
      var mediaFileName = this.get('model.data.mediaFileName');
      if (!Em.isNone(mediaFileName)) {
        var extIndex = mediaFileName.lastIndexOf('.');
        if (extIndex !== -1) {
          var fileExt = (mediaFileName.substring(extIndex + 1, mediaFileName.length)).toLowerCase();
          iconSrcPath = AmData.get('urlRoot') + '/api/content/icons/32-' + fileExt + '.png';
        }
      }
      return iconSrcPath;
    }.property('model.data.mediaFileName'),

    mediaType: function () {
      var mediaType = this.get('model.data.type');
      if (!Em.isNone(mediaType)) {
        var valueArray = mediaType.toString().split('[****]');
        mediaType = valueArray[1];
      }
      return mediaType;
    }.property('model.data.type'),

    init: function () {
      this._super();

      this.setProperties({
        relatedMobilePoliciesController: AmContentItemRelatedMobilePoliciesController.create({ parentController: this }),
        relatedMobileDevicesController: AmContentItemRelatedMobileDevicesController.create({ parentController: this })
      });
    },

    loadContentItem: function (id) {
      this.set('id', id);
      this.set('model', AmContent.get('store').acquireOne(this.get('lock'), id, null, null, false, false));
    }
  });
});
