define([
  'ember',
  'desktop',
  'am-desktop',
  'formatter',

  '../namespace',

  'packages/am/am-session',
  'am-data'
], function (Em,
             Desktop,
             AmDesktop,
             Formatter,
             AmMobilePolicy,
             AmSession,
             AmData) {
  'use strict';

  return AmDesktop.ListControllerColumns.extend(Desktop.ChildController, {

    tRemoveThirdPartyApplication: 'amMobilePolicies.mobilePolicyDetailsPage.body.actionsMenu.options.removeThirdPartyApplication'.tr(),

    hasRowClick: true,
    selectOnRowClick: true,
    selectionEnabled: true,

    apiBase: '/api/thirdpartyapps/',

    userPrefsEndpointName: 'thirdPartyAppRelatedFromPolicyListColumns',

    visibleColumnNames: 'icon name assignmentRule osPlatform category'.w(),

    dataStore: function () {
      return AmData.get('stores.thirdPartyApplicationFromPolicyStore');
    }.property(),

    relatedListTitle: 'amMobilePolicies.mobilePolicyDetailsPage.body.tabDescriptions.thirdPartyApplications'.tr(),

    dataStoreContext: function () {
      return {mobilePolicyId: this.get('parentController.id')};
    }.property('parentController.id'),

    listActions: function () {
      var actions = null;
      if (AmSession.hasInstallApplicationPermission()) {
        if (!Em.isNone(this.get('parentController.model.data'))) {
          actions = [{
            labelResource: 'amMobilePolicies.mobilePolicyDetailsPage.body.buttons.addThirdPartyApplication',
            iconClassNames: 'plus-content icon-plus', actionName: 'addThirdPartyApp'
          }];
        }
      }
      return actions;
    }.property('parentController.model.data.isManaged', 'parentController.model.data.osPlatformEnum',
      'parentController.model.data.absoluteAppsVersion'),

    selectionActions: function () {
      var selectedItems = this.get('selections'), actions = Em.A();

      // Delete
      actions.push({
        name: this.get('tRemoveThirdPartyApplication'),
        actionName: 'removeThirdPartyApp',
        contextPath: 'selections',
        disabled: selectedItems === null ||
        selectedItems.length === 0 || !AmSession.hasUninstallApplicationPermission() ||
        Em.isNone(this.get('parentController.model.data')),
        iconClassNames: 'icon-trashcan'
      });

      return actions;
    }.property('selections.[]', 'parentController.model.data.isManaged', 'parentController.model.data.osPlatformEnum',
      'parentController.model.data.absoluteAppsVersion'),

    createColumns: function(names) {
      var columns = this._super(names);

      columns.forEach(function(column) {
        // We need to show specific icons for content type
        if (column.get('name') === 'osPlatform') {
          column.set('valueComponent', 'am-formatted-os-platform');
        }
      });

      return columns;
    }
  });
});
