define([
  'ember',
  '../namespace',
  'desktop',
  'am-desktop',
  'am-data'
], function(
  Em,
  AmMobileDevice,
  Desktop,
  AmDesktop,
  AmData
) {
  'use strict';

  return AmDesktop.AmListController.extend({
    parentController: Em.inject.controller('amMobileDeviceItem'),

    relatedListTitle: 'amMobileDevice.devicePage.customFieldsTab.title'.tr(),

    tDeleteCommand: 'amMobileDevice.devicePage.customFieldsTab.commands.deleteCustomFieldData'.tr(),
    tEditCommand: 'amMobileDevice.devicePage.customFieldsTab.commands.editCustomFieldData'.tr(),

    hasRowClick: true,
    selectOnRowClick: true,
    selectionEnabled: true,

    dataStore: function() {
      return AmData.get('stores.mobileDeviceCustomFieldStore');
    }.property(),

    sort: Em.A([{
      attr: 'information',
      dir: 'asc'
    }]),

    selectionActions: function () {
      var selectedItems = this.get('selections'), actions = [];

      // The id in this case is concatenated with ':' character because it was a joined id in Spec.idNames
      // In order to get the context for the specified id we need to get rid of this last character
      selectedItems = selectedItems.map(function(item) {
        var colonIndex = item.indexOf(':');
        return item.substring(0, colonIndex);
      });

      var context = this.getSelectionActionContext(selectedItems, this.get('listRowData'));

      var supportsDelete = selectedItems && !context.every(function(item) {
            return Em.isNone(item.get('data.dataValue'));
          });

      // Delete Command
      actions.push({
        name: this.get('tDeleteCommand'),
        actionName: 'deleteCustomFieldDataAction',
        context: context,
        disabled: selectedItems === null || selectedItems.length === 0 || !supportsDelete,
        iconClassNames: 'icon-eraser'
      });

      // Edit
      actions.push({
        name: this.get('tEditCommand'),
        actionName: 'editCustomFieldDataAction',
        context: context[0],
        disabled: this.get('selections.length') !== 1,
        iconClassNames: 'icon-edit2'
      });

      return actions;
    }.property('selections.[]'),

    dataStoreContext: function() {
      return { mobileDeviceId: this.get('parentController.id') };
    }.property('parentController.id'),

    visibleColumnNames: 'information dataValue dataType description'.w()
  });
});
