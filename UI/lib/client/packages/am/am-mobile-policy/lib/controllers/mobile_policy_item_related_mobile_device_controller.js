define([
  'ember',
  'desktop',
  'am-desktop',
  '../namespace'
], function(
  Em,
  Desktop,
  AmDesktop,
  AmMobilePolicy
) {
  'use strict';

  return AmDesktop.ListControllerColumns.extend(Desktop.ChildController, {

    tRemoveFromPolicy: 'amMobilePolicies.mobilePolicyDetailsPage.body.actionsMenu.options.removeFromPolicy'.tr(),
    tMoveToAnotherPolicy: 'amMobilePolicies.mobilePolicyDetailsPage.body.actionsMenu.options.moveToAnotherPolicy'.tr(),
    tDescriptions: 'amMobilePolicies.mobilePolicyDetailsPage.body.tabDescriptions.mobileDevices'.tr(),

    userPrefsEndpointName: 'deviceRelatedToPolicyListColumns',

    hasRowClick: true,

    relatedListTitle: function() {
      if(!this.get('isReadOnlyPolicy')) {
        return this.get('tDescriptions');
      }
    }.property('isReadOnlyPolicy'),

    visibleColumnNames: function() {
      if( !this.get('isReadOnlyPolicy') ) {
        return 'name model osPlatform osVersion serialNumber phoneNumber lastContact'.w();
      }

      return 'name model osPlatform osVersion lastContact'.w();
    }.property('isReadOnlyPolicy'),

    selectionEnabled: function() {
      var isSmartPolicy = this.get('parentController.model.data.isSmartPolicy');
      if (!Em.isNone(isSmartPolicy)) {
        return isSmartPolicy === 0 && !this.get('isReadOnlyPolicy');
      }
    }.property('parentController.model.data.isSmartPolicy', 'isReadOnlyPolicy'),

    dataStore: function() {
      return AmMobilePolicy.get('relatedMobileDevicesStore');
    }.property(),

    dataStoreContext: function() {
      return { mobilePolicyId: this.get('parentController.id') };
    }.property('parentController.id'),

    isReadOnlyPolicy: function() {
      return this.get('parentController.isReadOnly');
    }.property('parentController.isReadOnly'),

    listActions: function() {
      var isSmartPolicy = this.get('parentController.model.data.isSmartPolicy');
      if (!Em.isNone(isSmartPolicy) && isSmartPolicy == false && !this.get('isReadOnlyPolicy')) {
        return [{ labelResource: 'amMobilePolicies.mobilePolicyDetailsPage.body.buttons.addMobileDevicesToPolicy',
          iconClassNames: 'plus-content icon-plus',
          actionName: 'addMobileDevices' }];
      }
    }.property('parentController.model.data.isSmartPolicy'),

    selectionActionsDisabled: function() {
      var isSmartPolicy = this.get('parentController.model.data.isSmartPolicy');
      return !Em.isNone(isSmartPolicy) && isSmartPolicy == true && this.get('isReadOnlyPolicy');
    }.property('parentController.model.data.isSmartPolicy', 'isReadOnlyPolicy'),

    selectionActions: function() {
      var selectedItems = this.get('selections'), isSmartPolicy = this.get('parentController.model.data.isSmartPolicy'),
        actions = null;

      // Hide if it is no checkbox to select
      if (!isSmartPolicy && !this.get('isReadOnlyPolicy')) {
        actions = [
          {
            name: this.get('tRemoveFromPolicy'),
            actionName: 'removeMobileDevices',
            contextPath: 'selections',
            disabled: selectedItems === null || selectedItems.length === 0,
            iconClassNames: 'icon-trashcan'
          },
          {
            name: this.get('tMoveToAnotherPolicy'),
            actionName: 'moveMobileDevices',
            contextPath: 'selections',
            disabled: selectedItems === null || selectedItems.length === 0,
            iconClassNames: 'icon-edit2'
          }
        ];
      }

      return actions;
    }.property('selections.[]', 'parentController.model.data.isSmartPolicy', 'isReadOnlyPolicy')
  });
});