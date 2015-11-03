define([
  'ember',
  'desktop',
  'am-desktop',

  '../namespace'
], function(
  Em,
  Desktop,
  AmDesktop,

  AmMobileDevice
) {
  'use strict';

  return AmDesktop.ListControllerColumns.extend({
    selectionEnabled: true,

    tRemoveMobileDevice: 'amMobileDevice.devicePage.mobilePoliciesTab.buttons.removeMobileDevice'.tr(),

    parentController: Em.inject.controller('amMobileDeviceItem'),

    relatedListTitle: 'amMobileDevice.devicePage.mobilePoliciesTab.title'.tr(),

    visibleColumnNames: 'name isSmartPolicy'.w(),

    userPrefsEndpointName: 'policyRelatedToDeviceListColumns',

    hasRowClick: true,

    dataStore: function() {
      return AmMobileDevice.get('relatedMobilePoliciesStore');
    }.property(),

    dataStoreContext: function() {
      return { mobileDeviceId: this.get('parentController.id') };
    }.property('parentController.id'),

    listActions: function() {
      var isManaged = this.get('parentController.model.data.isManaged');
      if (!Em.isNone(isManaged) && isManaged == true) {
        return [{
          labelResource: 'amMobileDevice.devicePage.mobilePoliciesTab.buttons.addMobileDevice',
          iconClassNames: 'plus-content icon-plus',
          actionName: 'addToPolicies'
        }];
      }
    }.property('parentController.model.data.isManaged'),

    selectionActions: function() {
      return this.getActionList();
    }.property('selections.[]'),

    getActionList: function() {
      var smartPolicySelected = false,
          selectedItems = this.get('selections'),
          item = null,
          data = this.getSelectionActionContext(selectedItems, this.get('listRowData'));


      for (var i = 0; i < this.get('selections.length'); i++) {
        item = data[i].get('data');

        if (item.get('isSmartPolicy')) {
          smartPolicySelected = true;
          break;
        }
      }

      return [{
        name: this.get('tRemoveMobileDevice'),
        actionName: 'removeFromPolicies',
        context: data,
        disabled: smartPolicySelected || selectedItems === null || selectedItems.length === 0,
        iconClassNames: 'icon-trashcan'
      }];
    }
  });
});
