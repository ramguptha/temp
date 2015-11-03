define([
  'ember',
  'help',
  'ui',
  'desktop',
  'am-desktop',

  '../namespace'
], function (
  Em,
  Help,
  UI,
  Desktop,
  AmDesktop,

  AmMobilePolicy
  ) {
  'use strict';

  return AmDesktop.AmListController.extend({
    tTitle: 'amMobilePolicies.mobilePoliciesListPage.title'.tr(),
    tDeletePolicy: 'amMobilePolicies.mobilePoliciesListPage.actionsMenu.options.deletePolicy'.tr(),
    tDeletePolicies: 'amMobilePolicies.mobilePoliciesListPage.actionsMenu.options.deletePolicies'.tr(),
    tEditPolicy: 'amMobilePolicies.mobilePoliciesListPage.actionsMenu.options.editPolicy'.tr(),
    tEditSmartPolicy: 'amMobilePolicies.mobilePoliciesListPage.actionsMenu.options.editSmartPolicy'.tr(),

    selectionEnabled: true,
    paused: false,
    hasRowClick: true,

    urlForHelp: Help.uri(1020),

    name: function () {
      return this.get('tTitle').toString();
    }.property(),

    breadcrumb: UI.Breadcrumb.create({
      path: 'am_mobile_policy_list',
      titleResource: 'amMobilePolicies.mobilePoliciesListPage.title'
    }),

    list: function () { return this.get('content'); }.property('content'),

    dataStore: function () {
      return AmMobilePolicy.get('store');
    }.property(),

    visibleColumnNames: 'name isSmartPolicy'.w(),

    listActions: function() {
      return [{
        labelResource: 'amMobilePolicies.mobilePoliciesListPage.addPolicyMenu.label',
        iconClassNames: 'icon-plus',
        dropDownName: 'add-device-group',
        children: [
          { iconClass: 'icon-policy-2', labelResource: 'amMobilePolicies.mobilePoliciesListPage.addPolicyMenu.options.newPolicy', actionName: 'newFixedPolicy' },
          { iconClass: 'icon-smartpolicy', labelResource: 'amMobilePolicies.mobilePoliciesListPage.addPolicyMenu.options.newSmartPolicy', actionName: 'newSmartPolicyWizard' }
        ]
      }];
    }.property(),

    selectionActions: function () {
      var selectedPolicies = this.get('selections'), actions = [], readOnlyPropertySelected = false;
      var context = Em.A();

      if (!Em.isEmpty(selectedPolicies)) {
        context = this.getSelectionActionContext(selectedPolicies, this.get('listRowData')).mapBy('data');

        for (var i = 0; i < selectedPolicies.length; i++) {
          if (context[i] && context[i].filterType === 255) {
            readOnlyPropertySelected = true;
            break;
          }
        }
      }

      var policyStr = this.get('tDeletePolicy');

      if (null != selectedPolicies && selectedPolicies.length > 1) {
        policyStr = this.get('tDeletePolicies');
      }

      // Delete policies
      actions.push({
        name: policyStr,
        actionName: 'deletePolicy',
        context: context,
        disabled: readOnlyPropertySelected || selectedPolicies === null || selectedPolicies.length === 0 || Em.isEmpty(context),
        iconClassNames: 'icon-trashcan'
      });

      var isSmartPolicy = selectedPolicies !== null && selectedPolicies.length === 1 && !Em.isEmpty(context) && context[0].isSmartPolicy == 1;

      // Edit only one item
      actions.push({
        name: isSmartPolicy ? this.get('tEditSmartPolicy') : this.get('tEditPolicy'),
        actionName: isSmartPolicy ? 'editSmartPolicy' : 'renamePolicy',
        context: context[0],
        disabled: readOnlyPropertySelected || selectedPolicies === null || selectedPolicies.length !== 1 || Em.isEmpty(context),
        iconClassNames: 'icon-edit2'
      });

      return actions;
    }.property('selections.[]')
  });
});
